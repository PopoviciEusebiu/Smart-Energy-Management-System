import React, { Component } from "react";
import { Link } from "react-router-dom";
import history from "../../history";
import { axiosInstance8080 } from "../../axios";
import "bootstrap/dist/css/bootstrap.min.css";
import { jwtDecode } from "jwt-decode";

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
      errorMessage: "",
    };
  }

  handleInput = (event) => {
    const { name, value } = event.target;
    this.setState({
      [name]: value,
    });
  };

  onSubmitFunction = (event) => {
    event.preventDefault();
    const { username, password } = this.state;

    const axiosInstance = axiosInstance8080;
    axiosInstance
      .post("/login", { username, password })
      .then((res) => {
        const { token } = res.data;

        if (token) {
          localStorage.setItem("jwtToken", token);
          const decodedToken = jwtDecode(token);
          const roles = decodedToken.roles;

          if (roles && roles.length > 0) {
            this.redirectUser(roles[0]);
          } else {
            this.setState({
              errorMessage:
                "Account does not have any roles assigned or token format is incorrect.",
            });
          }
        } else {
          this.setState({
            errorMessage:
              "Login successful but no token received, contact support.",
          });
        }
      })
      .catch((error) => {
        let message = "Login failed due to server error.";
        if (error.response) {
          const { status, data } = error.response;
          if (status === 400 || status === 401) {
            message =
              data && data.message
                ? data.message
                : "Invalid username or password.";
          } else if (status === 403) {
            message = "Cannot login, please verify your email.";
          } else if (status === 404) {
            message = "User not found.";
          }
        } else {
          message = "Network error or server not responding.";
        }
        this.setState({ errorMessage: message });
      });
  };

  redirectUser(role) {
    switch (role) {
      case "ADMIN":
        history.push("/adminHome");
        break;
      case "USER":
        history.push("/userHome");
        break;
      default:
        history.push("/login");
        break;
    }
    // sessionStorage.setItem("userRole", role);
    window.location.reload();
  }

  render() {
    return (
      <section className="vh-100" style={{ backgroundColor: "white" }}>
        <div className="container py-5 h-100">
          <div className="row d-flex justify-content-center align-items-center h-100">
            <div className="col-12 col-md-8 col-lg-6 col-xl-5">
              <div
                className="card bg-light text-dark"
                style={{ borderRadius: "1rem" }}
              >
                <div className="card-body p-5 text-center">
                  <h2 className="fw-bold mb-4 text-uppercase">Login</h2>
                  <p className="text-muted mb-5">
                    Please enter your username and password!
                  </p>
                  <form onSubmit={this.onSubmitFunction}>
                    <div className="form-outline mb-4">
                      <input
                        type="text"
                        id="username"
                        name="username"
                        value={this.state.username}
                        onChange={this.handleInput}
                        className="form-control form-control-lg"
                        placeholder="Username"
                      />
                    </div>
                    <div className="form-outline mb-4">
                      <input
                        type="password"
                        id="password"
                        name="password"
                        value={this.state.password}
                        onChange={this.handleInput}
                        className="form-control form-control-lg"
                        placeholder="Password"
                      />
                    </div>
                    <button
                      type="submit"
                      className="btn btn-primary btn-lg px-5"
                    >
                      Login
                    </button>
                  </form>
                  {this.state.errorMessage && (
                    <p className="text-danger mt-3">
                      {this.state.errorMessage}
                    </p>
                  )}
                  <p className="small mb-5 pb-lg-2">
                    <a className="text-muted" href="#!">
                      Forgot password?
                    </a>
                  </p>
                  <div className="mt-4">
                    <p className="mb-0">
                      Don't have an account?{" "}
                      <Link className="text-muted fw-bold" to="/register">
                        Sign Up
                      </Link>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    );
  }
}

export default Login;
