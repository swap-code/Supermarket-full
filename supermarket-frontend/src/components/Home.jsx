import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Card,
  Toast,
} from "react-bootstrap";
import { toast } from "react-toastify";

const Home = () => {
  const [products, setProducts] = useState([]);
  const [searchCriteria, setSearchCriteria] = useState("");
  const [searchValue, setSearchValue] = useState("");
  const [loading, setLoading] = useState(true);
  const [showToast, setShowToast] = useState(false);
  const navigate = useNavigate();
  const { username, role } = useSelector((state) => state.auth);

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchCriteria, searchValue]);

  const fetchData = async () => {
    try {
      setLoading(true);
      let url = "/api/products";
      if (searchCriteria && searchValue) {
        url += `/searchBy/${searchCriteria}/${searchValue}`;
      }
      const response = await axios.get(url);
      setProducts(response.data.data.filter((product) => product.active));
      setLoading(false);
    } catch (error) {
      console.error(error);
      setLoading(false);
    }
  };

  const getProductImageURL = (imageFileName) => {
    return `http://localhost:9090/api/products/images/${imageFileName}`;
  };

  const handleSearchCriteriaChange = (event) => {
    setSearchCriteria(event.target.value);
  };

  const handleSearchValueChange = (event) => {
    setSearchValue(event.target.value);
  };

  const handleReset = () => {
    setSearchCriteria("");
    setSearchValue("");
  };

  const addToCart = async (id) => {
    try {
      if (!username) {
        navigate("/login");
        return;
      }
      await axios.post(`/api/cart/addProduct/${id}`);
      setShowToast(true);
      toast.success("Product added to cart successfully!");
    } catch (error) {
      console.error("Error adding product to cart:", error);
      if (role === "ADMIN") {
        toast.error("Login as user to order an item!");
      } else {
        toast.error("Failed to add product to cart. Please try again.");
      }
    }
  };

  return (
    <Container>
      <h2 className="text-center mt-4">All Products in the SuperMarket</h2>
      <Row>
        <Col md={4} className="mb-4">
          <Form.Group>
            <Form.Control
              as="select"
              value={searchCriteria}
              onChange={handleSearchCriteriaChange}
            >
              <option value="">Select Search Criteria</option>
              <option value="name">Name</option>
              <option value="category">Category</option>
              <option value="description">Description</option>
            </Form.Control>
          </Form.Group>
        </Col>
        <Col md={4} className="mb-4">
          <Form.Group>
            <Form.Control
              type="text"
              placeholder="Enter Search Value"
              value={searchValue}
              onChange={handleSearchValueChange}
            />
          </Form.Group>
        </Col>
        <Col md={4} className="mb-4">
          <Button variant="danger" className="ml-2" onClick={handleReset}>
            Reset
          </Button>
        </Col>
      </Row>
      {loading ? (
        <div className="text-center">Loading...</div>
      ) : (
        <Row>
          {products.length === 0 ? (
            <Col className="text-center">No products found.</Col>
          ) : (
            products.map((product) => (
              <Col md={3} className="mb-4" key={product.productId}>
                <Card className="h-100 small-card">
                  <Card.Img
                    src={getProductImageURL(product.imageFileName)}
                    alt={product.imageFileName}
                    className="card-img img-thumbnail"
                    style={{ width: "270px", height: "270px" }}
                  />
                  <Card.Body>
                    <Card.Title>{product.name}</Card.Title>
                    <Card.Text>
                      <strong>Category:</strong> {product.category}
                    </Card.Text>
                    <Card.Text>
                      <strong>Description:</strong> {product.description}
                    </Card.Text>
                    <Card.Text>
                      <strong>Price: ₹ </strong> {product.price}
                    </Card.Text>
                    {product.inStock ? (
                      <Button
                        className="mt-auto"
                        variant="primary"
                        onClick={() => addToCart(product.productId)}
                      >
                        Add to Cart
                      </Button>
                    ) : (
                      <strong className="text-danger">Out of Stock</strong>
                    )}
                  </Card.Body>
                </Card>
              </Col>
            ))
          )}
        </Row>
      )}
      <Toast
        show={showToast}
        onClose={() => setShowToast(false)}
        delay={2000}
        autohide
        className="toast-success"
      >
        <Toast.Body>Product added to cart successfully!</Toast.Body>
      </Toast>
    </Container>
  );
};

export default Home;
