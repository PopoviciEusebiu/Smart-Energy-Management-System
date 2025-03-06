import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, allowedRoles }) => {
  const userRole = sessionStorage.getItem("userRole");

  if (!userRole || !allowedRoles.includes(userRole)) {
    return <Navigate to="/accessDenied" replace />;
  }

  return children;
};

export default ProtectedRoute;
