import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Container,
  Box,
  Typography,
  styled,
  tableCellClasses,
  Snackbar,
  Alert,
  Button,
} from "@mui/material";
import { axiosInstance8081 } from "../../axios";
import NavbarUser from "../../utils/navbars/NavbarUser.js";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { jwtDecode } from "jwt-decode";
import history from "../../history";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: theme.palette.action.hover,
  },
  "&:last-child td, &:last-child th": {
    border: 0,
  },
}));

class UserHomeDevices extends React.Component {
  constructor(props) {
    super(props);

    let user = null;
    try {
      const token = localStorage.getItem("jwtToken");
      if (token) {
        const decodedToken = jwtDecode(token);
        user = {
          id: decodedToken.id,
          username: decodedToken.username,
          firstName: decodedToken.firstName,
          lastName: decodedToken.lastName,
          emailAddress: decodedToken.emailAddress,
        };
      }
    } catch (error) {
      console.error("Error decoding JWT:", error);
    }

    this.state = {
      devices: [],
      user,
      errorMessage: "",
      showNotification: false,
      currentNotification: "",
    };

    this.stompClient = null;
  }

  authenticatedAxios() {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      axiosInstance8081.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${token}`;
    }
    return axiosInstance8081;
  }

  componentDidMount() {
    this.fetchDevices();
    this.connectWebSocket();
  }

  componentWillUnmount() {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.deactivate();
      console.log("WebSocket disconnected");
    }
  }

  fetchDevices = () => {
    const { user } = this.state;
    if (user) {
      const userId = parseInt(user.id);
      this.authenticatedAxios()
        .get(`/device/user/${userId}`)
        .then((res) => {
          this.setState({
            devices: res.data,
          });
        })
        .catch((error) => {
          console.error("This user has no devices:", error);
          this.setState({ errorMessage: "This user has no devices." });
        });
    } else {
      this.setState({ errorMessage: "User information not available." });
    }
  };

  connectWebSocket = () => {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
      console.error("JWT token not found. WebSocket cannot connect.");
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () =>
        new SockJS("http://consumption.localhost:80/socket"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: function (str) {
        console.log("[STOMP DEBUG]", str);
      },
      onConnect: (frame) => {
        console.log("Connected to WebSocket:", frame);
        const { user } = this.state;

        if (user) {
          const userId = user.id;
          this.stompClient.subscribe(
            `/topic/notifications/${userId}`,
            (message) => {
              console.log("Notification received:", message.body);
              this.setState({
                showNotification: true,
                currentNotification: message.body,
              });
            }
          );
        }
      },
      onStompError: (frame) => {
        console.error("STOMP connection error", frame);
      },
    });

    this.stompClient.activate();
  };

  handleCloseNotification = () => {
    this.setState({ showNotification: false });
  };

  handleGoToDevice = (deviceId) => {
    localStorage.setItem("deviceId", deviceId);
    console.log("Device ID saved to localStorage:", deviceId);
    history.push("/consumption");
    window.location.reload();
  };

  render() {
    const { devices, errorMessage, showNotification, currentNotification } =
      this.state;

    return (
      <div>
        <NavbarUser />
        <Container>
          <Box mt={10}>
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <StyledTableCell>Address</StyledTableCell>
                    <StyledTableCell>Description</StyledTableCell>
                    <StyledTableCell>Max Hourly Consumption</StyledTableCell>
                    <StyledTableCell>Actions</StyledTableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {devices.map((device) => (
                    <StyledTableRow key={device.id}>
                      <StyledTableCell>{device.address}</StyledTableCell>
                      <StyledTableCell>{device.description}</StyledTableCell>
                      <StyledTableCell>
                        {device.maxHourlyConsumption}
                      </StyledTableCell>
                      <StyledTableCell>
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => this.handleGoToDevice(device.id)}
                        >
                          Go to Calendar
                        </Button>
                      </StyledTableCell>
                    </StyledTableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>

          {errorMessage && (
            <Typography color="error">{errorMessage}</Typography>
          )}

          <Snackbar
            open={showNotification}
            autoHideDuration={6000}
            onClose={this.handleCloseNotification}
          >
            <Alert
              onClose={this.handleCloseNotification}
              severity="warning"
              sx={{ width: "100%" }}
            >
              {currentNotification}
            </Alert>
          </Snackbar>
        </Container>
      </div>
    );
  }
}

export default UserHomeDevices;
