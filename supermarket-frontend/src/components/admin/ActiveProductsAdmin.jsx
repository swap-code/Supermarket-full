import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { Container, Table, Button, Image } from "react-bootstrap";
import SideBar from "../SideBar";
import { toast } from "react-toastify";

const ActiveProductsAdmin = () => {
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
      .get("api/products")
      .then((response) => {
        setProducts(response.data.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  const handleAddProduct = () => {
    navigate(`/${username}/add-product`);
  };

  const handleAddImage = (productId) => {
    const imageInput = document.createElement("input");
    imageInput.type = "file";
    imageInput.accept = "image/*";
    imageInput.onchange = (event) => {
      const selectedFile = event.target.files[0];
      const formData = new FormData();
      formData.append("imageFile", selectedFile);

      axios
        .post(`api/products/add-product-image/${productId}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          setProducts((prevProducts) =>
            prevProducts.map((product) =>
              product.productId === productId
                ? { ...product, imageFileName: selectedFile.name }
                : product
            )
          );
        })
        .catch((error) => {
          console.error(error);
        });
    };
    imageInput.click();
  };

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
        .delete(`api/products/${productId}`)
        .then((response) => {
          toast.success("Product Deleted Successfully!");
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
        <Container style={{ marginLeft: "10px" }}>
          <h2 style={{ textAlign: "center", margin: "20px" }}>
            All Active Products in the SuperMarket
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
              </tr>
            </thead>
            <tbody>
              {products.length > 0 &&
                products.map((product, index) => (
                  <tr key={product.productId}>
                    <td>{index + 1}</td>
                    <td>
                      {product.imageFileName ? (
                        <Image
                          src={getProductImageURL(product.imageFileName)}
                          alt={product.imageFileName}
                          style={{ width: "100px", height: "100px" }}
                        />
                      ) : (
                        <div>
                          No image added
                          <Button
                            variant="primary"
                            onClick={() => handleAddImage(product.productId)}
                          >
                            Add Image
                          </Button>
                        </div>
                      )}
                    </td>
                    <td>{product.name}</td>
                    <td>{product.category}</td>
                    <td>{product.description}</td>
                    <td>₹ {product.price}</td>
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
          <Button
            variant="primary"
            className="mx-auto d-block"
            onClick={() => handleAddProduct()}
          >
            Add a New Product
          </Button>
        </Container>
      </div>
    </>
  );
};

export default ActiveProductsAdmin;
