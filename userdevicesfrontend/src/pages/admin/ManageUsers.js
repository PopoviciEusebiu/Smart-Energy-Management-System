import React from "react";
import {
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Container,
  IconButton,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  tableCellClasses,
  styled,
  Box,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add"; // Import Add icon
import { axiosInstance8080 } from "../../axios";
import Navbar from "../../utils/navbars/Navbar.js";
import { jwtDecode } from "jwt-decode"; // Import jwtDecode

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

class ManageUsers extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      users: [],
      errorMessage: "",
      editDialogOpen: false,
      deleteDialogOpen: false,
      addDialogOpen: false,
      selectedUser: null,
      newUser: {
        username: "",
        firstName: "",
        lastName: "",
        emailAddress: "",
        password: "",
      },
    };
  }

  authenticatedAxios() {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      axiosInstance8080.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${token}`;
    }
    return axiosInstance8080;
  }

  componentDidMount() {
    this.fetchUsers();
  }

  fetchUsers = () => {
    this.authenticatedAxios()
      .get("/user/withUserRole")
      .then((res) => {
        this.setState({
          users: res.data,
        });
      })
      .catch((error) => {
        console.error("Failed to fetch users:", error);
        this.setState({ errorMessage: "Failed to load users." });
      });
  };

  handleEditOpen = (user) => {
    this.setState({ editDialogOpen: true, selectedUser: user });
  };

  handleDeleteOpen = (user) => {
    this.setState({ deleteDialogOpen: true, selectedUser: user });
  };

  handleAddOpen = () => {
    this.setState({ addDialogOpen: true }); // Open the add user dialog
  };

  handleDialogClose = () => {
    this.setState({
      editDialogOpen: false,
      deleteDialogOpen: false,
      addDialogOpen: false, // Close Add User dialog
      selectedUser: null,
      newUser: {
        username: "",
        firstName: "",
        lastName: "",
        emailAddress: "",
        password: "",
      },
    });
  };

  handleDeleteUser = () => {
    const { selectedUser } = this.state;
    this.authenticatedAxios()
      .delete(`/user/delete/${selectedUser.id}`)
      .then(() => {
        this.fetchUsers();
        this.handleDialogClose();
      })
      .catch((error) => {
        console.error("Failed to delete user:", error);
      });
  };

  handleNewUserChange = (event) => {
    const { name, value } = event.target;
    this.setState((prevState) => ({
      newUser: {
        ...prevState.newUser,
        [name]: value,
      },
    }));
  };

  handleUserChange = (event) => {
    const { name, value } = event.target;
    this.setState((prevState) => ({
      selectedUser: {
        ...prevState.selectedUser,
        [name]: value,
      },
    }));
  };

  handleAddUser = () => {
    const { newUser } = this.state;
    const token = localStorage.getItem("jwtToken");

    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        const adminId = decodedToken.id;

        const userWithAdminId = { ...newUser, adminId };

        this.authenticatedAxios()
          .post("/user/create", userWithAdminId)
          .then(() => {
            this.fetchUsers();
            this.handleDialogClose();
          })
          .catch((error) => {
            console.error("Failed to add user:", error);
            this.setState({
              errorMessage: "Failed to add user. Please try again later.",
            });
          });
      } catch (error) {
        console.error("Invalid token:", error);
        this.setState({ errorMessage: "Failed to decode token." });
      }
    } else {
      console.error("No token found");
      this.setState({ errorMessage: "No authentication token found." });
    }
  };

  handleUpdateUser = () => {
    const { selectedUser } = this.state;
    this.authenticatedAxios()
      .put(`/user/update/${selectedUser.id}`, selectedUser)
      .then(() => {
        this.fetchUsers();
        this.handleDialogClose();
      })
      .catch((error) => {
        console.error("Failed to save user:", error);
      });
  };

  render() {
    const {
      users,
      errorMessage,
      editDialogOpen,
      deleteDialogOpen,
      addDialogOpen,
      selectedUser,
      newUser,
    } = this.state;

    return (
      <div>
        <Navbar />
        <Container>
          <Box display="flex" justifyContent="flex-start" mt={4} mb={4}>
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={this.handleAddOpen}
            >
              Add User
            </Button>
          </Box>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <StyledTableCell>Username</StyledTableCell>
                  <StyledTableCell>First Name</StyledTableCell>
                  <StyledTableCell>Last Name</StyledTableCell>
                  <StyledTableCell>Email Address</StyledTableCell>
                  <StyledTableCell>
                    Actions
                    <IconButton
                      onClick={this.handleAddOpen} // Open Add dialog
                      color="primary"
                      style={{ marginLeft: "10px" }}
                    >
                      <AddIcon />
                    </IconButton>
                  </StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((user) => (
                  <StyledTableRow key={user.id}>
                    <StyledTableCell>{user.username}</StyledTableCell>
                    <StyledTableCell>{user.firstName}</StyledTableCell>
                    <StyledTableCell>{user.lastName}</StyledTableCell>
                    <StyledTableCell>{user.emailAddress}</StyledTableCell>
                    <StyledTableCell>
                      <IconButton onClick={() => this.handleEditOpen(user)}>
                        <EditIcon />
                      </IconButton>
                      Edit
                      <IconButton onClick={() => this.handleDeleteOpen(user)}>
                        <DeleteIcon />
                      </IconButton>
                      Delete
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          {errorMessage && (
            <Typography color="error">{errorMessage}</Typography>
          )}

          {/* Add User Dialog */}
          <Dialog open={addDialogOpen} onClose={this.handleDialogClose}>
            <DialogTitle>Add New User</DialogTitle>
            <DialogContent>
              <TextField
                label="Username"
                name="username"
                value={newUser.username}
                onChange={this.handleNewUserChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="First Name"
                name="firstName"
                value={newUser.firstName}
                onChange={this.handleNewUserChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="Last Name"
                name="lastName"
                value={newUser.lastName}
                onChange={this.handleNewUserChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="Email Address"
                name="emailAddress"
                value={newUser.emailAddress}
                onChange={this.handleNewUserChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="Password"
                name="password"
                type="password"
                value={newUser.password}
                onChange={this.handleNewUserChange}
                margin="normal"
                fullWidth
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={this.handleDialogClose}>Cancel</Button>
              <Button onClick={this.handleAddUser} color="primary">
                Add
              </Button>
            </DialogActions>
          </Dialog>

          {/* Edit User Dialog */}
          {selectedUser && (
            <Dialog open={editDialogOpen} onClose={this.handleDialogClose}>
              <DialogTitle>Edit User</DialogTitle>
              <DialogContent>
                <TextField
                  label="Username"
                  name="username"
                  value={selectedUser.username}
                  onChange={this.handleUserChange}
                  margin="normal"
                  fullWidth
                />
                <TextField
                  label="First Name"
                  name="firstName"
                  value={selectedUser.firstName}
                  onChange={this.handleUserChange}
                  margin="normal"
                  fullWidth
                />
                <TextField
                  label="Last Name"
                  name="lastName"
                  value={selectedUser.lastName}
                  onChange={this.handleUserChange}
                  margin="normal"
                  fullWidth
                />
                <TextField
                  label="Email Address"
                  name="emailAddress"
                  value={selectedUser.emailAddress}
                  onChange={this.handleUserChange}
                  margin="normal"
                  fullWidth
                />
              </DialogContent>
              <DialogActions>
                <Button onClick={this.handleDialogClose}>Cancel</Button>
                <Button onClick={this.handleUpdateUser} color="primary">
                  Save
                </Button>
              </DialogActions>
            </Dialog>
          )}

          {/* Delete User Dialog */}
          {selectedUser && (
            <Dialog open={deleteDialogOpen} onClose={this.handleDialogClose}>
              <DialogTitle>Delete User</DialogTitle>
              <DialogContent>
                <Typography>
                  Are you sure you want to delete {selectedUser.username}?
                </Typography>
              </DialogContent>
              <DialogActions>
                <Button onClick={this.handleDialogClose}>Cancel</Button>
                <Button onClick={this.handleDeleteUser} color="secondary">
                  Delete
                </Button>
              </DialogActions>
            </Dialog>
          )}
        </Container>
      </div>
    );
  }
}

export default ManageUsers;
