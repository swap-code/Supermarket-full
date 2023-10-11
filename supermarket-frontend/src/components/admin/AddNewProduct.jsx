import React, { useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import SideBar from "../SideBar";
import axios from "axios";
import { toast } from "react-toastify";

const AddNewProduct = () => {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [category, setCategory] = useState("");
  const [price, setPrice] = useState("");
  const [quantity, setQuantity] = useState("");
  const [validated, setValidated] = useState(false);
  const navigate = useNavigate();
  const { role, username } = useSelector((state) => state.auth);

  useEffect(() => {
    if (role !== "ADMIN") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    axios.get("api/products");
  }, []);

  const handleAddProduct = (event) => {
    event.preventDefault();
    const form = event.currentTarget;

    if (form.checkValidity()) {
      const productData = {
        name,
        description,
        category,
        price,
        quantity,
      };

      axios
        .post("api/products", productData)
        .then((response) => {
          toast.success("Product Added Successfully!");
          navigate(`/${username}`);
        })
        .catch((error) => {
          toast.error("Error adding product!");
          console.error(error);
        });
    }

    setValidated(true);
  };

  return (
    <div className="d-flex">
      <SideBar />
      <div className="container">
        <h2 className="text-center my-4">Add New Product</h2>
        <Form noValidate validated={validated} onSubmit={handleAddProduct}>
          <Form.Group controlId="name">
            <Form.Label>Product Name:</Form.Label>
            <Form.Control
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <Form.Control.Feedback type="invalid">
              Please provide a product name.
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group controlId="description">
            <Form.Label>Product Description:</Form.Label>
            <Form.Control
              as="textarea"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
            />
            <Form.Control.Feedback type="invalid">
              Please provide a product description.
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group controlId="category">
            <Form.Label>Product Category:</Form.Label>
            <Form.Control
              type="text"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              required
            />
            <Form.Control.Feedback type="invalid">
              Please provide a product category.
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group controlId="price">
            <Form.Label>Product Price:</Form.Label>
            <Form.Control
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              required
              min="0"
            />
            <Form.Control.Feedback type="invalid">
              Please provide a valid product price.
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group controlId="quantity">
            <Form.Label>Product Quantity:</Form.Label>
            <Form.Control
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
              required
              min="0"
            />
            <Form.Control.Feedback type="invalid">
              Please provide a valid product quantity.
            </Form.Control.Feedback>
          </Form.Group>
          <div className="text-center mt-4">
            <Button type="submit" variant="primary">
              Add Product
            </Button>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default AddNewProduct;
