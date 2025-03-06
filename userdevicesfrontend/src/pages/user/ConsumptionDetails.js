import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Container, Box, Typography, Select, MenuItem } from "@mui/material";
import { Line, Bar } from "react-chartjs-2";
import { axiosInstance8087 } from "../../axios";
import NavbarUser from "../../utils/navbars/NavbarUser.js";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const ConsumptionDetails = () => {
  const { deviceId, date } = useParams(); // Extract deviceId and date from the URL
  const [consumptionData, setConsumptionData] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [chartType, setChartType] = useState("line");

  useEffect(() => {
    if (!date) {
      setErrorMessage("Date is missing in the URL.");
      return;
    }

    const authenticatedAxios = () => {
      const token = localStorage.getItem("jwtToken");
      if (token) {
        axiosInstance8087.defaults.headers.common[
          "Authorization"
        ] = `Bearer ${token}`;
      }
      return axiosInstance8087;
    };

    const fetchConsumptionData = async () => {
      try {
        console.log(`Device ID: ${deviceId}, Date: ${date}`);
        const authAxios = authenticatedAxios();

        const response = await authAxios.get(
          `/consumption/historical/${deviceId}?date=${date}`
        );
        setConsumptionData(response.data);
        console.log(response.data);
      } catch (error) {
        setErrorMessage("Error fetching consumption data.");
        console.error(error);
      }
    };

    fetchConsumptionData();
  }, [deviceId, date]);

  const allHours = Array.from({ length: 24 }, (_, i) => i);

  const consumptionDataForChart = allHours.map((hour) => {
    const dataForHour = consumptionData.find((item) => item.hour === hour);
    return dataForHour ? dataForHour.energyValue : 0;
  });

  const chartData = {
    labels: allHours.map((hour) => `${hour}:00`),
    datasets: [
      {
        label: "Energy Consumption (kWh)",
        data: consumptionDataForChart,
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        fill: chartType === "line",
        borderWidth: 2,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    scales: {
      x: {
        title: {
          display: true,
          text: "Hour of the Day",
        },
      },
      y: {
        title: {
          display: true,
          text: "Energy Consumption (kWh)",
        },
        beginAtZero: true,
        suggestedMax: Math.max(...consumptionDataForChart) + 50,
      },
    },
  };

  return (
    <div>
      <NavbarUser />
      <Container>
        <Box mt={5}>
          <Typography variant="h6">
            Energy Consumption for {new Date(date).toLocaleDateString()}
          </Typography>

          <Select
            value={chartType}
            onChange={(e) => setChartType(e.target.value)}
            sx={{ mb: 2 }}
          >
            <MenuItem value="line">Line Chart</MenuItem>
            <MenuItem value="bar">Bar Chart</MenuItem>
          </Select>

          {chartType === "line" ? (
            <Line data={chartData} options={chartOptions} />
          ) : (
            <Bar data={chartData} options={chartOptions} />
          )}
        </Box>

        {errorMessage && (
          <Typography color="error" variant="body2">
            {errorMessage}
          </Typography>
        )}
      </Container>
    </div>
  );
};

export default ConsumptionDetails;
