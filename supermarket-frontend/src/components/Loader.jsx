import React from "react";
import { Spinner } from "react-bootstrap";

const Loader = () => {
  return (
    <div className="d-flex align-items-center justify-content-center vw100 vh100">
      <Spinner animation="border" />
    </div>
  );
};

export default Loader;
