import React, { useState } from "react";
import { Container, Box, Typography, Snackbar, Alert } from "@mui/material";
import { Line } from "react-chartjs-2";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import { axiosInstance8087 } from "../../axios.js";
import history from "../../history.js";
import NavbarUser from "../../utils/navbars/NavbarUser.js";
import "../../styles/calendar.css";

const axiosInstance = axiosInstance8087;

const EnergyConsumption = () => {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [consumptionData, setConsumptionData] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [showNotification, setShowNotification] = useState(false);
  const [currentNotification] = useState("");

  const deviceId = localStorage.getItem("deviceId") || 16;

  const authenticatedAxios = () => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      axiosInstance8087.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${token}`;
    }
    return axiosInstance8087;
  };

  const fetchConsumptionData = async (deviceId, date) => {
    try {
      const authAxios = authenticatedAxios();
      const formattedDate = date.toISOString().split("T")[0];
      const response = await authAxios.get(
        `/consumption/historical/${deviceId}?date=${formattedDate}`
      );
      setConsumptionData(response.data);
    } catch (error) {
      setErrorMessage("Error fetching consumption data.");
      console.error(error);
    }
  };

  const handleDateChange = (date) => {
    const normalizedDate = new Date(
      Date.UTC(date.getFullYear(), date.getMonth(), date.getDate())
    );

    setSelectedDate(normalizedDate);
    fetchConsumptionData(deviceId, normalizedDate);

    history.push(
      `/history/${deviceId}/${normalizedDate.toISOString().split("T")[0]}`
    );
    window.location.reload();
  };

  const handleCloseNotification = () => {
    setShowNotification(false);
  };

  const chartData = {
    labels: consumptionData.map((item) => `${item.hour}:00`),
    datasets: [
      {
        label: "Energy Consumption (kWh)",
        data: consumptionData.map((item) => item.energyValue),
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        fill: true,
      },
    ],
  };

  return (
    <div>
      <NavbarUser />
      <Container>
        <Box
          mt={5}
          display="flex"
          justifyContent="center"
          alignItems="center"
          flexDirection="column"
        >
          <Typography variant="h6">Select Date</Typography>
          <Box mt={2} className="calendar-container">
            <Calendar
              onChange={handleDateChange}
              value={selectedDate}
              className="react-calendar"
            />
          </Box>
        </Box>

        {consumptionData.length > 0 && (
          <Box mt={5}>
            <Typography variant="h6">
              Energy Consumption for {selectedDate?.toLocaleDateString()}
            </Typography>
            <Line data={chartData} />
          </Box>
        )}

        {errorMessage && (
          <Typography color="error" variant="body2">
            {errorMessage}
          </Typography>
        )}

        <Snackbar
          open={showNotification}
          autoHideDuration={6000}
          onClose={handleCloseNotification}
        >
          <Alert
            onClose={handleCloseNotification}
            severity="warning"
            sx={{ width: "100%" }}
          >
            {currentNotification}
          </Alert>
        </Snackbar>
      </Container>
    </div>
  );
};

export default EnergyConsumption;
