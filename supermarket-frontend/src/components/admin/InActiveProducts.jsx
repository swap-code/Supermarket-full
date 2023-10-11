import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { Table, Button, Container } from "react-bootstrap";
import SideBar from "../SideBar";
import { toast } from "react-toastify";

const InActiveProducts = () => {
  const [products, setProducts] = useState([]);
  const navigate = useNavigate();
  const { role, username } = useSelector((state) => state.auth);

  useEffect(() => {
    if (role !== "ADMIN") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    axios
      .get("api/products/inactive")
      .then((response) => {
        setProducts(response.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  const getProductImageURL = (imageFileName) => {
    return `http://localhost:9090/api/products/images/${imageFileName}`;
  };

  const handleEdit = (productId) => {
    navigate(`/${username}/edit-product?productId=${productId}`);
  };

  const handleDelete = (productId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this product?"
    );

    if (confirmed) {
      axios
        .delete(`api/products/${productId}`, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          toast.success("Product delete successfully!");
          setProducts((prevProducts) =>
            prevProducts.filter((product) => product.productId !== productId)
          );
        })
        .catch((error) => {
          toast.error(
            "Product can't be deleted as customer's order has been processed"
          );
          console.error(error);
        });
    }
  };

  return (
    <>
      <div className="d-flex">
        <SideBar />
        <Container className="ml-2">
          <h2 className="text-center mt-4">
            InActive Products in the SuperMarket
          </h2>
          <Table striped bordered>
            <thead>
              <tr>
                <th>S. No.</th>
                <th>Product Image</th>
                <th>Product Name</th>
                <th>Product Category</th>
                <th>Product Description</th>
                <th>Product Price</th>
                <th>Product Quantity</th>
                <th>Product Availability</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>
            </thead>
            <tbody>
              {products.length > 0 &&
                products.map((product, index) => (
                  <tr key={product.productId}>
                    <td>{index + 1}</td>
                    <td>
                      {product.imageFileName ? (
                        <img
                          src={getProductImageURL(product.imageFileName)}
                          alt={product.imageFileName}
                          style={{ width: "100px", height: "100px" }}
                        />
                      ) : (
                        <div>No image added</div>
                      )}
                    </td>
                    <td>{product.name}</td>
                    <td>{product.category}</td>
                    <td>{product.description}</td>
                    <td>{product.price}</td>
                    <td>{product.quantity}</td>
                    <td>{product.inStock ? "Available" : "Out of Stock"}</td>
                    <td>
                      <Button
                        variant="warning"
                        onClick={() => handleEdit(product.productId)}
                      >
                        Edit
                      </Button>
                    </td>
                    <td>
                      <Button
                        variant="danger"
                        onClick={() => handleDelete(product.productId)}
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                ))}
            </tbody>
          </Table>
        </Container>
      </div>
    </>
  );
};

export default InActiveProducts;
