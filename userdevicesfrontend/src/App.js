import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import AdminHome from "./pages/admin/AdminHome.js";
import ManageUsers from "./pages/admin/ManageUsers.js";
import Login from "./pages/login/Log-in.js";
import "bootstrap/dist/css/bootstrap.min.css";
import ManageDevices from "./pages/admin/ManageDevices.js";
import UserHome from "./pages/user/UserHome.js";
import UserProfile from "./pages/user/UserProfile.js";
import AdminProfile from "./pages/admin/AdminProfile.js";
import AccessDenied from "./utils/protectedRoutes/ErrorPage.js";
import EnergyConsumption from "./pages/user/Calendar.js";
import ConsumptionDetails from "./pages/user/ConsumptionDetails.js";
import Chat from "./pages/user/Chat.js";
import Register from "./pages/login/Register.js";
import Logout from "./pages/login/Logout.js";

function App() {
  const defaultRoute =
    window.location.pathname === "/" ? (
      <Navigate to="/login" replace={true} />
    ) : null;

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/accessDenied" element={<AccessDenied />} />
        <Route path="/adminHome" element={<AdminHome />} />
        <Route path="/manageUsers" element={<ManageUsers />} />
        <Route path="/manageDevices" element={<ManageDevices />} />
        <Route path="/userHome" element={<UserHome />} />
        <Route path="/userProfile" element={<UserProfile />} />
        <Route path="/adminProfile" element={<AdminProfile />} />
        <Route path="/consumption" element={<EnergyConsumption />} />
        <Route path="/chat" element={<Chat />} />
        <Route path="/logout" element={<Logout />} />
        <Route
          path="/history/:deviceId/:date"
          element={<ConsumptionDetails />}
        />
        <Route path="/" element={defaultRoute} />
      </Routes>
    </Router>
  );
}

export default App;
