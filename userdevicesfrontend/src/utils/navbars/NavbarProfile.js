import { Container } from "react-bootstrap";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom";
import "../../styles/navbar.css";
import { TbDeviceLandlinePhone } from "react-icons/tb";
import { FaUsers } from "react-icons/fa6";
import { CgProfile } from "react-icons/cg";
import { IoMdHome } from "react-icons/io";
import { IoMdLogOut } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import { IoChatbubbleEllipsesOutline } from "react-icons/io5";

function ColorSchemesExample4() {
  const navigate = useNavigate();

  return (
    <>
      <Navbar className="custom-navbar" data-bs-theme="light">
        <Container>
          <Navbar.Brand>Profile</Navbar.Brand>
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/adminHome">
              <span className="icon">
                <IoMdHome />
              </span>
              Home
            </Nav.Link>
            <Nav.Link as={Link} to="/adminProfile">
              <span className="icon">
                <CgProfile />
              </span>
              Profile
            </Nav.Link>
            <Nav.Link as={Link} to="/manageUsers">
              <span className="icon">
                <FaUsers />
              </span>
              Users
            </Nav.Link>
            <Nav.Link as={Link} to="/manageDevices">
              <span className="icon">
                <TbDeviceLandlinePhone />
              </span>
              Devices
            </Nav.Link>
            <Nav.Link as={Link} to="/chat">
              <span className="icon">
                <IoChatbubbleEllipsesOutline />
              </span>
              Chat
            </Nav.Link>
          </Nav>
          <Nav className="ml-auto">
            <Nav.Link
              onClick={() => {
                navigate("/logout");
              }}
              className="logout-link"
            >
              {" "}
              <span className="icon">
                <IoMdLogOut />
              </span>
              Logout
            </Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </>
  );
}

export default ColorSchemesExample4;
