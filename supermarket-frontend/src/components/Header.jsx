import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../features/auth/authSlice";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";

const Header = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { username, role } = useSelector((state) => state.auth);
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    if (!username || !role) {
      navigate("/");
    }
    axios
      .get("/api/cart")
      .then((response) => {
        setCartItems(response.data.data);
      })
      .catch((error) => {
        console.error("Failed to fetch cart items:", error);
      });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleCartClick = () => {
    navigate(`/${username}/cart`);
  };

  const handleHomeClick = () => {
    navigate("/");
  };

  const handleAdminDashboardClick = () => {
    navigate(`/${username}`);
  };

  const handleUserOrdersClick = () => {
    navigate(`/${username}/orders`);
  };

  const handleLogout = () => {
    const confirmed = window.confirm("Are you sure you want to logout?");

    if (confirmed) {
      dispatch(logout());
    }
    toast.success("Logged out successfully!");
    navigate("/");
  };

  const totalQuantity = cartItems.reduce(
    (total, item) => total + item.quantity,
    0
  );

  return (
    <Navbar bg="dark" expand="lg" variant="dark" collapseOnSelect>
      <Container fluid>
        <Navbar.Brand>
          <i className="bi bi-shop ms-2 me-1" />
          <span> The Nagarro Market </span>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="navbarScroll" />
        <Navbar.Collapse id="navbarScroll">
          <Nav className="my-2 my-lg-0 ms-auto me-3">
            {role === "USER" && (
              <Button variant="outline-light" onClick={handleCartClick}>
                <i className="bi bi-cart3" />
                {totalQuantity > 0 && (
                  <span className="cart-icon-count"> {totalQuantity}</span>
                )}
              </Button>
            )}
            <Nav.Link onClick={handleHomeClick}>
              <i className="bi-house" /> Home
            </Nav.Link>
            {role === "USER" && (
              <Nav.Link onClick={handleUserOrdersClick}>Your Orders</Nav.Link>
            )}
            {role === "ADMIN" && (
              <Nav.Link onClick={handleAdminDashboardClick}>
                Admin Dashboard
              </Nav.Link>
            )}
            {!username ? (
              <>
                <Nav.Link onClick={() => navigate("/login")}>
                  <i className="bi-person" /> Login/Signup
                </Nav.Link>
              </>
            ) : (
              <NavDropdown title={username} id="basic-nav-dropdown">
                <NavDropdown.Item onClick={handleHomeClick}>
                  Home
                </NavDropdown.Item>
                {role === "ADMIN" && (
                  <NavDropdown.Item onClick={handleAdminDashboardClick}>
                    Admin Dashboard
                  </NavDropdown.Item>
                )}
                {role === "USER" && (
                  <NavDropdown.Item onClick={handleUserOrdersClick}>
                    Your Orders
                  </NavDropdown.Item>
                )}
                <NavDropdown.Item onClick={handleLogout}>
                  LogOut
                </NavDropdown.Item>
              </NavDropdown>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
