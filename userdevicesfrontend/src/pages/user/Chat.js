import React, { useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { axiosInstance8080 } from "../../axios";
import { jwtDecode } from "jwt-decode"; // Correct import
import NavbarChatAdmin from "../../utils/navbars/NavbarChatAdmin";
import NavbarChatUser from "../../utils/navbars/NavbarChatUser";

const ChatApp = () => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [input, setInput] = useState("");
  const [users, setUsers] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [isAdmin, setIsAdmin] = useState(false);
  const [typingNotification, setTypingNotification] = useState(null);

  const [messagesDict, setMessagesDict] = useState({});
  const stompClientRef = useRef(null);
  const subscriptionRef = useRef(null);

  const authenticatedAxios = (instance) => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      instance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    }
    return instance;
  };

  const getUserIdFromToken = () => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      const decodedToken = jwtDecode(token);
      return decodedToken.id;
    }
    return null;
  };

  const getUserRolesFromToken = () => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      const decodedToken = jwtDecode(token);
      return decodedToken.roles || [];
    }
    return [];
  };

  useEffect(() => {
    const roles = getUserRolesFromToken();
    if (roles.includes("ADMIN")) {
      setIsAdmin(true);
      fetchUsers();
    } else {
      setIsAdmin(false);
      fetchAdmins();
    }

    const token = localStorage.getItem("jwtToken");
    if (token) {
      connectWebSocket(token);
    }

    return () => {
      if (stompClientRef.current && stompClientRef.current.connected) {
        stompClientRef.current.deactivate();
        console.log("WebSocket disconnected");
      }
    };
  }, []);

  const connectWebSocket = (token) => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://chat.localhost:80/socket"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => {
        console.log("[STOMP DEBUG]", str);
      },
      onConnect: () => {
        console.log("WebSocket connected");
      },
      onStompError: (frame) => {
        console.error("STOMP connection error:", frame);
      },
    });

    client.activate();
    stompClientRef.current = client;
  };

  useEffect(() => {
    if (selectedUser && stompClientRef.current?.connected) {
      // Dezabonare de la subcripțiile existente
      if (subscriptionRef.current) {
        subscriptionRef.current.newSubscription?.unsubscribe();
        subscriptionRef.current.newReceiverSubscription?.unsubscribe();
        subscriptionRef.current.readNotificationSubscription?.unsubscribe();
        subscriptionRef.current.typingNotificationSubscription?.unsubscribe();
      }

      const senderId = getUserIdFromToken();
      const receiverId = selectedUser.id;

      // Canalele pentru mesajele și notificările curente
      const senderToReceiverChannel = `/queue/messages/${senderId}/${receiverId}`;
      const receiverToSenderChannel = `/queue/messages/${receiverId}/${senderId}`;
      const readNotificationChannel = `/queue/notifications/${senderId}`;
      const typingNotificationChannel = `/queue/typing/${senderId}`;

      // Subcripție pentru mesajele trimise de la sender la receiver
      const newSubscription = stompClientRef.current.subscribe(
        senderToReceiverChannel,
        (message) => {
          const receivedMessage = JSON.parse(message.body);
          if (receivedMessage.senderId !== senderId) {
            setMessagesDict((prevMessages) => {
              const newMessages = prevMessages[selectedUser.id] || [];
              return {
                ...prevMessages,
                [selectedUser.id]: [...newMessages, receivedMessage],
              };
            });
          }
        }
      );

      // Subcripție pentru mesajele primite de la receiver la sender
      const newReceiverSubscription = stompClientRef.current.subscribe(
        receiverToSenderChannel,
        (message) => {
          const receivedMessage = JSON.parse(message.body);
          if (receivedMessage.receiverId !== senderId) {
            setMessagesDict((prevMessages) => {
              const newMessages = prevMessages[selectedUser.id] || [];
              return {
                ...prevMessages,
                [selectedUser.id]: [...newMessages, receivedMessage],
              };
            });

            // Trimitere notificare de "read" pentru mesajul primit
            const notification = {
              senderId: senderId,
              receiverId: receiverId,
            };

            if (stompClientRef.current?.connected) {
              stompClientRef.current.publish({
                destination: "/app/markMessageAsRead",
                body: JSON.stringify(notification),
              });
            }
          }
        }
      );

      // Subcripție pentru notificările de scriere
      const typingNotificationSubscription = stompClientRef.current.subscribe(
        typingNotificationChannel,
        (message) => {
          const typingNotification = JSON.parse(message.body);
          if (
            typingNotification.senderId === String(selectedUser?.id) &&
            typingNotification.senderId !== getUserIdFromToken()
          ) {
            setTypingNotification(`${selectedUser.username} is typing...`);
            setTimeout(() => setTypingNotification(null), 1000);
          }
        }
      );

      // Subcripție pentru notificările de citire
      const readNotificationSubscription = stompClientRef.current.subscribe(
        readNotificationChannel,
        (frame) => {
          const decodedBody = new TextDecoder().decode(frame.binaryBody);

          let notification;
          try {
            notification = JSON.parse(decodedBody);
          } catch (error) {
            console.error("Error parsing JSON:", error);
            return;
          }

          const { senderId } = notification;
          if (Number(selectedUser.id) === Number(senderId)) {
            updateMessagesDict(selectedUser.id, (userMessages) =>
              userMessages.map((msg) =>
                msg.senderId !== senderId && !msg.read
                  ? { ...msg, read: true }
                  : msg
              )
            );
          }
        }
      );

      // Funcție pentru actualizarea listei de mesaje
      const updateMessagesDict = (userId, updateCallback) => {
        setMessagesDict((prevMessages) => {
          const userMessages = prevMessages[userId] || [];
          const updatedMessages = updateCallback(userMessages);
          return {
            ...prevMessages,
            [userId]: updatedMessages,
          };
        });
      };

      // **Verificare și marcarea mesajelor necitite ca "read"**
      if (selectedUser && messagesDict[selectedUser.id]) {
        const unreadMessages = messagesDict[selectedUser.id].filter(
          (msg) => msg.receiverId === senderId && !msg.read
        );

        if (unreadMessages.length > 0) {
          const notification = {
            senderId: senderId,
            receiverId: receiverId,
          };

          if (stompClientRef.current?.connected) {
            // Trimitere notificare pentru toate mesajele necitite
            stompClientRef.current.publish({
              destination: "/app/markMessageAsRead",
              body: JSON.stringify(notification),
            });

            // Actualizarea stării mesajelor în interfață
            updateMessagesDict(selectedUser.id, (userMessages) =>
              userMessages.map((msg) =>
                unreadMessages.includes(msg) ? { ...msg, read: true } : msg
              )
            );
          }
        }
      }

      // Stocarea noilor subcripții
      subscriptionRef.current = {
        newSubscription,
        newReceiverSubscription,
        readNotificationSubscription,
        typingNotificationSubscription,
      };
    }

    return () => {
      if (subscriptionRef.current) {
        subscriptionRef.current.newSubscription?.unsubscribe();
        subscriptionRef.current.newReceiverSubscription?.unsubscribe();
        subscriptionRef.current.readNotificationSubscription?.unsubscribe();
        subscriptionRef.current.typingNotificationSubscription?.unsubscribe();
      }
    };
  }, [selectedUser]);

  useEffect(() => {
    if (selectedUser) {
      setMessagesDict((prevMessages) => {
        return prevMessages;
      });
    }
  }, [selectedUser]);

  const fetchUsers = () => {
    authenticatedAxios(axiosInstance8080)
      .get("/user/withUserRole")
      .then((res) => {
        setUsers(res.data);
      })
      .catch((error) => {
        console.error("Failed to fetch users:", error);
        setErrorMessage("Failed to load users.");
      });
  };

  const fetchAdmins = () => {
    authenticatedAxios(axiosInstance8080)
      .get("/user/withAdminRole")
      .then((res) => {
        setUsers(res.data);
      })
      .catch((error) => {
        console.error("Failed to fetch admins:", error);
        setErrorMessage("Failed to load admins.");
      });
  };

  const handleSendMessage = () => {
    if (input.trim() && selectedUser) {
      const senderId = getUserIdFromToken();
      const receiverId = selectedUser.id;

      const messagePayload = {
        senderId: senderId,
        receiverId: receiverId,
        content: input,
        read: false,
      };

      if (stompClientRef.current?.connected) {
        stompClientRef.current.publish({
          destination: "/app/sendMessage",
          body: JSON.stringify(messagePayload),
        });

        setMessagesDict((prevMessages) => {
          const newMessages = prevMessages[selectedUser.id] || [];
          console.log(newMessages);
          return {
            ...prevMessages,
            [selectedUser.id]: [
              ...newMessages,
              { senderId, receiverId, content: input, sender: "me" },
            ],
          };
        });

        setInput("");
      } else {
        setErrorMessage("WebSocket is not connected.");
      }
    }
  };

  const handleTyping = () => {
    if (selectedUser && stompClientRef.current?.connected) {
      const senderId = getUserIdFromToken();
      const receiverId = selectedUser.id;

      const typingNotificationPayload = {
        senderId: senderId,
        receiverId: receiverId,
      };

      stompClientRef.current.publish({
        destination: "/app/typingNotification",
        body: JSON.stringify(typingNotificationPayload),
      });
    }
  };

  return (
    <div>
      {isAdmin ? <NavbarChatAdmin /> : <NavbarChatUser />}
      <div style={styles.container}>
        <div style={styles.usersPanel}>
          <h3 style={styles.panelHeader}>Users</h3>
          {errorMessage && <p style={styles.error}>{errorMessage}</p>}
          {users.map((user) => (
            <div
              key={user.id}
              style={{
                ...styles.userItem,
                backgroundColor:
                  selectedUser?.id === user.id ? "#e0f7fa" : "white",
              }}
              onClick={() => setSelectedUser(user)}
            >
              <div style={styles.avatar}>{user.username[0]}</div>
              <span>{user.username}</span>
            </div>
          ))}
        </div>

        <div style={styles.chatSection}>
          {selectedUser ? (
            <>
              <div style={styles.chatHeader}>
                Chat with {selectedUser.username}
              </div>
              <div style={styles.messagesContainer}>
                {(messagesDict[selectedUser.id] || []).map((msg, index) => (
                  <div
                    key={index}
                    style={{
                      ...styles.message,
                      alignSelf:
                        msg.sender === "me" ? "flex-end" : "flex-start",
                      backgroundColor:
                        msg.sender === "me" ? "#d1c4e9" : "#f1f8e9",
                    }}
                  >
                    {msg.content}
                    {msg.sender === "me" &&
                      index ===
                        (messagesDict[selectedUser.id] || []).length - 1 &&
                      msg.read && <span style={styles.seenText}>Seen</span>}
                  </div>
                ))}
              </div>

              {typingNotification && (
                <div style={styles.typingNotification}>
                  {typingNotification}
                </div>
              )}
              <div style={styles.inputContainer}>
                <input
                  style={styles.inputField}
                  type="text"
                  placeholder="Type a message..."
                  value={input}
                  onChange={(e) => {
                    setInput(e.target.value);
                    handleTyping();
                  }}
                  onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
                />
                <button style={styles.sendButton} onClick={handleSendMessage}>
                  Send
                </button>
              </div>
            </>
          ) : (
            <div style={styles.selectUserMessage}>
              Select a user to start chatting
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    display: "flex",
    height: "70vh",
    width: "60vw",
    margin: "20px auto",
    border: "1px solid #ddd",
    borderRadius: "10px",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
    overflow: "hidden",
  },
  usersPanel: {
    width: "30%",
    borderRight: "1px solid #ddd",
    backgroundColor: "#f9f9f9",
  },
  panelHeader: {
    textAlign: "center",
    padding: "10px",
    backgroundColor: "#6200ea",
    color: "white",
    margin: 0,
  },
  userItem: {
    display: "flex",
    alignItems: "center",
    padding: "10px",
    cursor: "pointer",
    borderBottom: "1px solid #ddd",
  },
  avatar: {
    width: "30px",
    height: "30px",
    borderRadius: "50%",
    backgroundColor: "#6200ea",
    color: "white",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    marginRight: "10px",
  },
  chatSection: {
    width: "70%",
    display: "flex",
    flexDirection: "column",
    backgroundColor: "#ffffff",
  },
  chatHeader: {
    padding: "10px",
    backgroundColor: "#6200ea",
    color: "white",
    textAlign: "center",
  },
  messagesContainer: {
    flex: 1,
    padding: "10px",
    overflowY: "auto",
    display: "flex",
    flexDirection: "column",
    gap: "10px",
    backgroundColor: "#f9f9f9",
  },
  message: {
    maxWidth: "70%",
    padding: "10px",
    borderRadius: "10px",
    wordWrap: "break-word",
    position: "relative",
  },
  inputContainer: {
    display: "flex",
    padding: "10px",
    borderTop: "1px solid #ddd",
    backgroundColor: "#f9f9f9",
  },
  inputField: {
    flex: 1,
    padding: "10px",
    borderRadius: "5px",
    border: "1px solid #ddd",
    marginRight: "10px",
  },
  sendButton: {
    padding: "10px 20px",
    backgroundColor: "#6200ea",
    color: "white",
    border: "none",
    borderRadius: "5px",
    cursor: "pointer",
  },
  selectUserMessage: {
    textAlign: "center",
    margin: "auto",
    color: "gray",
  },
  seenText: {
    fontSize: "12px",
    color: "gray",
    marginTop: "5px",
    display: "block",
    textAlign: "right",
  },
  typingNotification: {
    fontSize: "14px",
    color: "gray",
    marginBottom: "10px",
    textAlign: "left",
    display: "block",
    marginLeft: "10px",
  },
};

export default ChatApp;
