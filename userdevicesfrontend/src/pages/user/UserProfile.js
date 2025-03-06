import React from "react";
import "../../styles/profile.css";
import NavbarU from "../../utils/navbars/NavbarUserProfile";
import { jwtDecode } from "jwt-decode";

class UserProfile extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      user: null,
    };
  }

  componentDidMount() {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      const decodedToken = jwtDecode(token);
      const userData = {
        username: decodedToken.username,
        role: decodedToken.role,
        firstName: decodedToken.firstName,
        lastName: decodedToken.lastName,
        emailAddress: decodedToken.emailAddress,
      };
      this.setState({ user: userData });
    }
  }

  render() {
    const { user } = this.state;
    if (!user) {
      return <div>Loading user data...</div>;
    }
    return (
      <>
        <NavbarU />
        <div className="profile-container">
          <div className="profile-left">
            <div className="profile-icon">U</div>
            <div className="profile-info">
              <h3>{user.username}</h3>
              <p>{user.role}</p>
            </div>
          </div>
          <div className="profile-right">
            <div className="information">
              <p>
                <strong>Last Name:</strong> {user.lastName}
              </p>
              <p>
                <strong>First Name:</strong> {user.firstName}
              </p>
              <p>
                <strong>Email:</strong> {user.emailAddress}
              </p>
            </div>
          </div>
        </div>
      </>
    );
  }
}

export default UserProfile;
