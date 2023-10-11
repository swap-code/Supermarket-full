import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { Form, Button, Container } from "react-bootstrap";
import SideBar from "../SideBar";

const EditProductAdmin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const productId = new URLSearchParams(location.search).get("productId");
  const { role, username } = useSelector((state) => state.auth);
  const [product, setProduct] = useState({});
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (role !== "ADMIN") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    axios
      .get(`/api/products/${productId}`)
      .then((response) => {
        setProduct(response.data.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, [productId]);

  const handleChange = (e) => {
    const { name, value, type, checked, files } = e.target;
    const fieldValue = type === "checkbox" ? checked : value;

    setProduct((prevProduct) => {
      return {
        ...prevProduct,
        [name]:
          name === "imageFile"
            ? files.length
              ? files[0]
              : prevProduct.imageFileName
            : fieldValue,
      };
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      const formData = new FormData();
      formData.append("productId", product.productId);
      formData.append("name", product.name);
      formData.append("category", product.category);
      formData.append("description", product.description);
      formData.append("price", product.price);
      formData.append("quantity", product.quantity);
      formData.append("active", product.active);

      if (product.imageFile) {
        axios
          .put(`api/products/${productId}`, formData, {
            headers: {
              "Content-Type": "application/json",
            },
          })
          .then((response) => {
            const imageFormData = new FormData();
            imageFormData.append("imageFile", product.imageFile);

            axios
              .put(
                `api/products/update-product-image/${productId}`,
                imageFormData,
                {
                  headers: {
                    "Content-Type": "multipart/form-data",
                  },
                }
              )
              .then((imageResponse) => {
                navigate(`/${username}`);
              })
              .catch((imageError) => {
                console.error(imageError);
              });
          })
          .catch((error) => {
            console.error(error);
          });
      } else {
        formData.append("imageFileName", product.imageFileName);
        axios
          .put(`api/products/${productId}`, formData, {
            headers: {
              "Content-Type": "application/json",
            },
          })
          .then((response) => {
            navigate(`/${username}`);
          })
          .catch((error) => {
            console.error(error);
          });
      }
    }
  };

  const validateForm = () => {
    const errors = {};
    let formIsValid = true;

    if (!product.name) {
      formIsValid = false;
      errors.name = "Product name is required";
    }

    if (!product.category) {
      formIsValid = false;
      errors.category = "Product category is required";
    }

    if (!product.description) {
      formIsValid = false;
      errors.description = "Product description is required";
    }

    if (!product.price) {
      formIsValid = false;
      errors.price = "Product price is required";
    }

    if (!product.quantity) {
      formIsValid = false;
      errors.quantity = "Product quantity is required";
    }

    setErrors(errors);
    return formIsValid;
  };

  return (
    <div className="d-flex">
      <SideBar />
      <Container>
        <h2>Edit Product Details</h2>
        <Form onSubmit={handleSubmit}>
          <Form.Group>
            <Form.Label>Product Id</Form.Label>
            <Form.Control
              type="text"
              name="productId"
              value={product.productId || ""}
              onChange={handleChange}
              disabled
            />
          </Form.Group>
          <Form.Group>
            <Form.Label>Product Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={product.name || ""}
              onChange={handleChange}
              isInvalid={errors.name}
            />
            <Form.Control.Feedback type="invalid">
              {errors.name}
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group>
            <Form.Label>Product Category</Form.Label>
            <Form.Control
              type="text"
              name="category"
              value={product.category || ""}
              onChange={handleChange}
              isInvalid={errors.category}
            />
            <Form.Control.Feedback type="invalid">
              {errors.category}
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group>
            <Form.Label>Product Description</Form.Label>
            <Form.Control
              as="textarea"
              name="description"
              value={product.description || ""}
              onChange={handleChange}
              isInvalid={errors.description}
            />
            <Form.Control.Feedback type="invalid">
              {errors.description}
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group>
            <Form.Label>Product Price</Form.Label>
            <Form.Control
              type="number"
              name="price"
              value={product.price || ""}
              onChange={handleChange}
              isInvalid={errors.price}
            />
            <Form.Control.Feedback type="invalid">
              {errors.price}
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group>
            <Form.Label>Product Quantity</Form.Label>
            <Form.Control
              type="number"
              name="quantity"
              value={product.quantity || ""}
              onChange={handleChange}
              isInvalid={errors.quantity}
            />
            <Form.Control.Feedback type="invalid">
              {errors.quantity}
            </Form.Control.Feedback>
          </Form.Group>
          <Form.Group>
            <Form.Check
              type="checkbox"
              name="active"
              checked={product.active || false}
              onChange={handleChange}
              label="Active"
            />
          </Form.Group>
          <Form.Group>
            <Form.Label>Image File</Form.Label>
            <Form.Control
              type="file"
              name="imageFile"
              accept="image/*"
              onChange={handleChange}
            />
          </Form.Group>
          <Button type="submit" variant="primary">
            Save
          </Button>
        </Form>
      </Container>
    </div>
  );
};

export default EditProductAdmin;
