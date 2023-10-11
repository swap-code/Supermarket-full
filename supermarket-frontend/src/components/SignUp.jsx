import React, { useEffect, useState } from "react";
import { Button, Card, Col, Container, Form, Row } from "react-bootstrap";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { reset, signup } from "../features/auth/authSlice";
import Loader from "./Loader";

const SignUp = () => {
  const nav = useNavigate();
  const dispatch = useDispatch();
  const [signupForm, setSignupForm] = useState({
    username: "",
    password: "",
  });
  const [errors, setErrors] = useState({});
  const { role, isLoading, isError, message, isSuccess } = useSelector(
    (state) => state.auth
  );

  useEffect(() => {
    if (isError) {
      toast.error(message);
    }
    if (isSuccess || role) {
      toast.success(message);
      nav("/login");
    }
    dispatch(reset());
  }, [isSuccess, isError, message, dispatch, nav, role]);

  const validateForm = () => {
    const errors = {};

    if (!signupForm.username.trim()) {
      errors.username = "Username is required";
    }

    if (!signupForm.password.trim()) {
      errors.password = "Password is required";
    }

    setErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setErrors((prevErrors) => ({ ...prevErrors, [name]: "" }));

    setSignupForm({ ...signupForm, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const isValid = validateForm();

    if (isValid) {
      dispatch(signup(signupForm));
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <Container fluid>
      <Row>
        <Col md={12} lg={6}>
          <img
            src={
              "https://img.freepik.com/premium-vector/mobile-online-shopping-people-buy-dresses-shirts-pants-online-shops-shoppers-buying-internet-sale-flat-illustration-online-clothing-store-discount-total-sale-concept_229548-60.jpg?w=900"
            }
            alt="supermarket"
            className="w-100 p-1 p-md-4 mh-100"
          />
        </Col>
        <Col
          md={12}
          lg={6}
          className="mh-100 d-flex align-items-center justify-content-center"
        >
          <Card className="w-75 shadow mt-5 m-auto">
            <Card.Body className="mx-5">
              <Form onSubmit={handleSubmit} className="p-3 py-5">
                <div className="text-center">
                  <i className="fa fa-duotone py-2 fa-classic fs-1 fa-user" />
                </div>
                <h2 className="text-center py-2">SignUp</h2>
                <Form.Group controlId="formBasicEmail" className="p-2 py-3">
                  <Form.Label>Username</Form.Label>
                  <Form.Control
                    type="text"
                    name="username"
                    placeholder="Enter username"
                    value={signupForm.username}
                    onChange={handleChange}
                    isInvalid={!!errors.username}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.username}
                  </Form.Control.Feedback>
                </Form.Group>
                <Form.Group controlId="formBasicPassword" className="p-2 py-3">
                  <Form.Label>Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={signupForm.password}
                    onChange={handleChange}
                    isInvalid={!!errors.password}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.password}
                  </Form.Control.Feedback>
                </Form.Group>
                <div className="py-3">
                  <Button
                    variant="dark"
                    type="submit"
                    className="my-2 mx-2 px-3"
                  >
                    SignUp
                  </Button>
                </div>
                <div className="p-2 text-center">
                  Already have an account? <Link to="/login">signIn</Link>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default SignUp;
