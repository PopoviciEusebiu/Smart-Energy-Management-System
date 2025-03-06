import React from "react";
import { Link } from "react-router-dom";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { axiosInstance8080 } from "../../axios";
import { Alert } from "@mui/material";

class Register extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      firstName: "",
      lastName: "",
      username: "",
      emailAddress: "",
      password: "",
      registerSuccess: false,
      errorMessage: "",
    };
    this.handleInput = this.handleInput.bind(this);
    this.onSubmitFunction = this.onSubmitFunction.bind(this);
  }

  handleInput(event) {
    const { value, name } = event.target;
    this.setState({
      [name]: value,
    });
  }

  onSubmitFunction(event) {
    event.preventDefault();
    const { firstName, lastName, username, emailAddress, password } =
      this.state;

    let user = {
      firstName,
      lastName,
      username,
      emailAddress,
      password,
    };

    const axiosInstance = axiosInstance8080;

    axiosInstance
      .post("/register", user)
      .then((res) => {
        this.setState({
          registerSuccess: true,
          errorMessage: "",
        });
      })
      .catch((error) => {
        console.log("Registration Failed: ", error);
        this.setState({
          errorMessage: "Registration failed. Please try again.",
        });
      });
  }

  render() {
    return (
      <Container maxWidth="sm">
        <div style={{ paddingTop: "80px" }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <form onSubmit={this.onSubmitFunction}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="firstName"
                  label="First Name"
                  name="firstName"
                  autoComplete="given-name"
                  onChange={this.handleInput}
                />
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="lastName"
                  label="Last Name"
                  name="lastName"
                  autoComplete="family-name"
                  onChange={this.handleInput}
                />
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="username"
                  label="Username"
                  name="username"
                  autoComplete="username"
                  onChange={this.handleInput}
                />
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="emailAddress"
                  label="Email Address"
                  name="emailAddress"
                  type="email"
                  autoComplete="email"
                  onChange={this.handleInput}
                />
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="password"
                  label="Password"
                  name="password"
                  type="password"
                  autoComplete="new-password"
                  onChange={this.handleInput}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  color="primary"
                >
                  Register
                </Button>
              </form>
              {this.state.registerSuccess && (
                <Alert severity="success" style={{ marginTop: 20 }}>
                  Registration successful.
                </Alert>
              )}
              {this.state.errorMessage && (
                <Alert severity="error" style={{ marginTop: 20 }}>
                  {this.state.errorMessage}
                </Alert>
              )}
            </Grid>
            <Grid
              item
              xs={12}
              container
              justifyContent="space-between"
              alignItems="center"
            >
              <Typography variant="body2">Already have an account?</Typography>
              <Link to="/login" style={{ textDecoration: "dashed" }}>
                <Typography variant="body2" style={{ cursor: "pointer" }}>
                  Login here
                </Typography>
              </Link>
            </Grid>
          </Grid>
        </div>
      </Container>
    );
  }
}

export default Register;
