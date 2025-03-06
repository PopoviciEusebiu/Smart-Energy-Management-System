import { Container } from "react-bootstrap";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom";
import "../../styles/navbar.css";
import { CgProfile } from "react-icons/cg";
import { IoMdLogOut } from "react-icons/io";
import { TbDeviceLandlinePhone } from "react-icons/tb";
import { IoChatbubbleEllipsesOutline } from "react-icons/io5";
import { useNavigate } from "react-router-dom";

function ColorSchemesExample6() {
  const navigate = useNavigate();

  return (
    <>
      <Navbar className="custom-navbar" data-bs-theme="light">
        <Container>
          <Navbar.Brand>Chat</Navbar.Brand>
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/userProfile">
              <span className="icon">
                <CgProfile />
              </span>
              Profile
            </Nav.Link>
            <Nav.Link as={Link} to="/userHome">
              <span className="icon">
                <TbDeviceLandlinePhone />
              </span>
              My Devices
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

export default ColorSchemesExample6;
