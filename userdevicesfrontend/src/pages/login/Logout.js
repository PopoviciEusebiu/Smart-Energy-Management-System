import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { axiosInstance8080 } from "../../axios";

function Logout() {
  const navigate = useNavigate();

  useEffect(() => {
    const axiosInstance = axiosInstance8080;
    axiosInstance
      .get("/logout", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
        },
      })
      .then((response) => {
        localStorage.clear();
        navigate("/login");
      })
      .catch((error) => {
        console.error("Logout failed:", error);
      });
  }, [navigate]);

  return <div>Logging out...</div>;
}

export default Logout;
