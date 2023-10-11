import React from "react";
import { Col, Nav } from "react-bootstrap";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const SideBar = () => {
  const nav = useNavigate();
  const { username, role } = useSelector((state) => state.auth);

  return (
    <Col className="col-auto col-md-3 col-xl-2 px-sm-2 px-0 bg-dark">
      <div className="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 text-white min-vh-100">
        <a
          href="/"
          className="d-flex align-items-center pb-3 mb-md-0 me-md-auto text-white text-decoration-none"
        >
          <span className="fs-5 d-none d-sm-inline">Menu</span>
        </a>
        <Nav
          className="flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start"
          id="menu"
        >
          {role === "ADMIN" && (
            <>
              <Nav.Link onClick={() => nav(`/${username}/add-product`)}>
                <i className="me-3 fa-solid fa-plus fa-lg" />
                <span className="ms-1 d-none d-sm-inline">Add Products</span>
              </Nav.Link>
              <Nav.Link onClick={() => nav(`/${username}/inactive-products`)}>
                <i className="me-1 fa-solid fa-users-slash fa-sm" />
                <span className="ms-1 d-none d-sm-inline">
                  Inactive Products
                </span>
              </Nav.Link>
              <Nav.Link onClick={() => nav(`/${username}/all-orders`)}>
                <i className="me-3 fa-sharp fa-solid fa-bag-shopping fa-lg" />
                <span className="ms-1 d-none d-sm-inline">View Orders</span>
              </Nav.Link>
              <Nav.Link onClick={() => nav(`/${username}/all-users`)}>
                <i className="me-3 fa-solid fa-users fa-sm" />
                <span className="ms-1 d-none d-sm-inline">View Users</span>
              </Nav.Link>
            </>
          )}
        </Nav>
      </div>
    </Col>
  );
};

export default SideBar;
