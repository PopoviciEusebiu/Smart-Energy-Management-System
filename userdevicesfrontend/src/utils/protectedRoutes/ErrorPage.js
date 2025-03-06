import React from "react";
import { useNavigate } from "react-router-dom";

const ErrorPage = () => {
  const navigate = useNavigate();

  const handleBackToMainPage = () => {
    sessionStorage.clear();
    localStorage.clear();
    navigate("/login");
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h2>Permission denied!</h2>
      <p>You do not have the necessary permission to access this page.</p>
      <button
        style={{
          background: "none",
          border: "none",
          textDecoration: "underline",
          color: "blue",
          cursor: "pointer",
          fontSize: "16px",
        }}
        onClick={handleBackToMainPage}
      >
        Back to main page
      </button>
    </div>
  );
};

export default ErrorPage;
