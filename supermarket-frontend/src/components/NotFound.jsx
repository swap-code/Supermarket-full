import React from "react";
import { Link } from "react-router-dom";
import styled from "styled-components";
import "bootstrap/dist/css/bootstrap.min.css";

const NotFound = () => {
  return (
    <PageNotFound>
      <div className="container-fluid">
        <div className="row">
          <div className="col-md-8 m-auto text-center">
            <div className="Page404">
              <h1 className="text-center p-4" style={{ fontSize: "60px" }}>
                404
              </h1>
            </div>
            <div className="font-avro">
              <h1 className="fs-1 p-0">Looks like you're lost</h1>
              <p className="fs-6 p-0">
                The page you are looking for is not available!
              </p>
              <Link className="btn btn-green px-4 text-decoration-none" to="/">
                Go to Home
              </Link>
            </div>
          </div>
        </div>
      </div>
    </PageNotFound>
  );
};

const PageNotFound = styled.div`
  @import url("https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css");

  .Page404 {
    background-image: url(https://cdn.dribbble.com/users/285475/screenshots/2083086/dribbble_1.gif);
    height: 60vh;
    background-position: center;
    background-size: contain;
    background-repeat: no-repeat;
  }

  .font-avro {
    font-family: "Arvo", serif;
  }

  .btn-green {
    background-color: #39ad31 !important;
    color: white !important;
    border: 0px;
  }
`;

export default NotFound;
