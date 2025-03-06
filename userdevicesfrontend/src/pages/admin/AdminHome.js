import React, { Component } from "react";
import "../../styles/admin.css";
import history from "../../history";
import NavbarHome from "../../utils/navbars/NavbarHome";
import { jwtDecode } from "jwt-decode";

class AdminHome extends Component {
  constructor(props) {
    super(props);

    const token = localStorage.getItem("jwtToken");
    let username = "";
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        username = decodedToken.username || "";
      } catch (error) {
        console.error("Invalid token:", error);
      }
    }

    this.state = {
      username,
    };
    console.log("AdminPage component mounted!");
  }

  handleNavigation = (path) => {
    history.push(path);
    window.location.reload();
  };

  render() {
    const { username } = this.state;
    return (
      <div>
        <NavbarHome />
        <div className="admin-container">
          <h1 className="admin-title">Welcome, {username}!</h1>
          <div className="options-container">
            <div className="option-box">
              <h3>Manage Users</h3>
              <button
                onClick={() => this.handleNavigation("/manageUsers")}
                className="option-button"
              >
                Go to Users
              </button>
            </div>
            <div className="option-box">
              <h3>Manage Devices</h3>
              <button
                onClick={() => this.handleNavigation("/manageDevices")}
                className="option-button"
              >
                Go to Devices
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default AdminHome;
