import React from "react";
import "../../styles/profile.css";
import NavbarProfile from "../../utils/navbars/NavbarProfile";
import { jwtDecode } from "jwt-decode";

class AdminProfile extends React.Component {
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
        lastName: decodedToken.lastName,
        firstName: decodedToken.firstName,
        emailAddress: decodedToken.emailAddress,
      };
      this.setState({
        user: userData,
      });
    }
  }

  render() {
    const { user } = this.state;
    if (!user) {
      return <div>Loading user data...</div>;
    }
    return (
      <>
        <NavbarProfile />
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

export default AdminProfile;
