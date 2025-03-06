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
  FormControl,
  InputLabel,
  MenuItem,
  styled,
  Select,
  Box,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import { axiosInstance8080, axiosInstance8081 } from "../../axios";
import NavbarDev from "../../utils/navbars/NavbarDev.js";

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

class ManageDevices extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      devices: [],
      users: [],
      errorMessage: "",
      editDialogOpen: false,
      deleteDialogOpen: false,
      addDialogOpen: false,
      selectedDevice: null,
      newDevice: {
        address: "",
        description: "",
        maxHourlyConsumption: "",
        validationError: "",
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

  authenticatedAxios2() {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      axiosInstance8081.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${token}`;
    }
    return axiosInstance8081;
  }

  componentDidMount() {
    this.fetchUsers();
    this.fetchDevices();
  }

  fetchUsers = () => {
    this.authenticatedAxios()
      .get("/user")
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

  fetchDevices = () => {
    this.authenticatedAxios2()
      .get("/device")
      .then((res) => {
        this.setState({
          devices: res.data,
        });
      })
      .catch((error) => {
        console.error("Failed to fetch devices:", error);
        this.setState({ errorMessage: "Failed to load devices." });
      });
  };

  handleEditOpen = (device) => {
    this.setState({
      editDialogOpen: true,
      selectedDevice: device,
      validationError: "",
    });
  };

  handleDeleteOpen = (device) => {
    this.setState({ deleteDialogOpen: true, selectedDevice: device });
  };

  handleAddOpen = () => {
    this.setState({ addDialogOpen: true, validationError: "" });
  };

  handleDialogClose = () => {
    this.setState({
      editDialogOpen: false,
      deleteDialogOpen: false,
      addDialogOpen: false,
      selectedDevice: null,
      newDevice: {
        address: "",
        description: "",
        maxHourlyConsumption: "",
      },
    });
  };

  handleDeleteDevice = () => {
    const { selectedDevice } = this.state;
    this.authenticatedAxios2()
      .delete(`/device/delete/${selectedDevice.id}`)
      .then(() => {
        this.fetchDevices();
        this.handleDialogClose();
      })
      .catch((error) => {
        console.error("Failed to delete device:", error);
      });
  };

  handleNewDeviceChange = (event) => {
    const { name, value } = event.target;

    if (name === "maxHourlyConsumption") {
      if (isNaN(value)) {
        this.setState((prevState) => ({
          validationError: "Max Hourly Consumption must be a number",
          selectedDevice: {
            ...prevState.selectedDevice,
            [name]: value,
          },
        }));
        return;
      }
    }

    this.setState((prevState) => ({
      validationError: "",
      newDevice: {
        ...prevState.newDevice,
        [name]: value,
      },
    }));
  };

  handleDeviceChange = (event) => {
    const { name, value } = event.target;
    if (name === "maxHourlyConsumption") {
      if (isNaN(value)) {
        this.setState((prevState) => ({
          validationError: "Max Hourly Consumption must be a number",
          selectedDevice: {
            ...prevState.selectedDevice,
            [name]: value,
          },
        }));
        return;
      }
    }

    this.setState((prevState) => ({
      validationError: "",
      selectedDevice: {
        ...prevState.selectedDevice,
        [name]: value,
      },
    }));
  };

  handleAddDevice = () => {
    const { newDevice, validationError } = this.state;

    if (validationError) {
      return;
    }

    this.authenticatedAxios2()
      .post("/device/create", newDevice)
      .then(() => {
        this.fetchDevices();
        this.handleDialogClose();
      })
      .catch((error) => {
        console.error("Failed to add device:", error);
      });
  };

  handleUpdateDevice = () => {
    const { selectedDevice, validationError } = this.state;

    if (validationError) {
      return;
    }

    console.log(selectedDevice);
    this.authenticatedAxios2()
      .put(`/device/update/${selectedDevice.id}`, selectedDevice)
      .then(() => {
        this.fetchDevices();
        this.handleDialogClose();
      })
      .catch((error) => {
        console.error("Failed to save device:", error);
      });
  };

  getUsernameByUserId = (userId) => {
    const { users } = this.state;
    const user = users.find((user) => user.id === userId);
    return user ? user.username : "Unknown User";
  };

  render() {
    const {
      devices,
      users,
      errorMessage,
      editDialogOpen,
      deleteDialogOpen,
      addDialogOpen,
      selectedDevice,
      newDevice,
      validationError,
    } = this.state;

    return (
      <div>
        <NavbarDev />
        <Container>
          <Box display="flex" justifyContent="flex-start" mt={4} mb={4}>
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={this.handleAddOpen}
            >
              Add Device
            </Button>
          </Box>

          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <StyledTableCell>Address</StyledTableCell>
                  <StyledTableCell>Description</StyledTableCell>
                  <StyledTableCell>Max Hourly Consumption</StyledTableCell>
                  <StyledTableCell>User</StyledTableCell>
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
                      {this.getUsernameByUserId(device.userId)}
                    </StyledTableCell>
                    <StyledTableCell>
                      <IconButton onClick={() => this.handleEditOpen(device)}>
                        <EditIcon />
                      </IconButton>
                      Edit
                      <IconButton onClick={() => this.handleDeleteOpen(device)}>
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

          <Dialog open={addDialogOpen} onClose={this.handleDialogClose}>
            <DialogTitle>Add New Device</DialogTitle>
            <DialogContent>
              <TextField
                label="Address"
                name="address"
                value={newDevice.address}
                onChange={this.handleNewDeviceChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="Description"
                name="description"
                value={newDevice.description}
                onChange={this.handleNewDeviceChange}
                margin="normal"
                fullWidth
              />
              <TextField
                label="Max hourly consumption"
                name="maxHourlyConsumption"
                value={newDevice.maxHourlyConsumption}
                onChange={this.handleNewDeviceChange}
                margin="normal"
                fullWidth
                error={validationError !== ""}
                helperText={validationError}
              />
              <FormControl fullWidth margin="normal">
                <InputLabel>User</InputLabel>
                <Select
                  name="userId"
                  value={newDevice.userId}
                  onChange={this.handleNewDeviceChange}
                >
                  {users.map((user) => (
                    <MenuItem key={user.id} value={user.id}>
                      {user.username}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </DialogContent>
            <DialogActions>
              <Button onClick={this.handleDialogClose}>Cancel</Button>
              <Button onClick={this.handleAddDevice} color="primary">
                Add
              </Button>
            </DialogActions>
          </Dialog>

          {selectedDevice && (
            <Dialog open={editDialogOpen} onClose={this.handleDialogClose}>
              <DialogTitle>Edit Device</DialogTitle>
              <DialogContent>
                <TextField
                  label="Address"
                  name="address"
                  value={selectedDevice.address || ""}
                  onChange={this.handleDeviceChange}
                  margin="normal"
                  fullWidth
                />
                <TextField
                  label="Description"
                  name="description"
                  value={selectedDevice.description || ""}
                  onChange={this.handleDeviceChange}
                  margin="normal"
                  fullWidth
                />
                <TextField
                  label="Max hourly consumption"
                  name="maxHourlyConsumption"
                  value={selectedDevice.maxHourlyConsumption || ""}
                  onChange={this.handleDeviceChange}
                  margin="normal"
                  fullWidth
                  error={validationError !== ""}
                  helperText={validationError}
                />
                <FormControl fullWidth margin="normal">
                  <InputLabel>User</InputLabel>
                  <Select
                    name="userId"
                    value={selectedDevice.userId || ""}
                    onChange={this.handleDeviceChange}
                  >
                    {users.map((user) => (
                      <MenuItem key={user.id} value={user.id}>
                        {user.username}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </DialogContent>
              <DialogActions>
                <Button onClick={this.handleDialogClose}>Cancel</Button>
                <Button onClick={this.handleUpdateDevice} color="primary">
                  Save
                </Button>
              </DialogActions>
            </Dialog>
          )}

          {selectedDevice && (
            <Dialog open={deleteDialogOpen} onClose={this.handleDialogClose}>
              <DialogTitle>Delete Device</DialogTitle>
              <DialogContent>
                <Typography>
                  Are you sure you want to delete the device with description:{" "}
                  {selectedDevice.description}?
                </Typography>
              </DialogContent>
              <DialogActions>
                <Button onClick={this.handleDialogClose}>Cancel</Button>
                <Button onClick={this.handleDeleteDevice} color="secondary">
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

export default ManageDevices;
