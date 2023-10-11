import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Table,
  Button,
  Card,
  Container,
  Row,
  Col,
  Image,
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { toast } from "react-toastify";

const Cart = () => {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [totalCartAmount, setTotalCartAmount] = useState(0);
  const [showPaymentOptions, setShowPaymentOptions] = useState(false);
  const { role, username } = useSelector((state) => state.auth);

  useEffect(() => {
    if (role !== "USER") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    fetchCartItems();
    fetchTotalCartAmount();
  }, []);

  const fetchCartItems = async () => {
    try {
      const response = await axios.get("/api/cart");
      const { data } = response.data;
      setCartItems(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error("Error fetching cart items:", error);
    }
  };

  const fetchTotalCartAmount = async () => {
    try {
      const response = await axios.get("/api/cart/totalAmount");
      const { data } = response.data;
      setTotalCartAmount(Number(data));
    } catch (error) {
      console.error("Error fetching total cart amount:", error);
    }
  };

  const incrementQuantity = async (cartItemId) => {
    try {
      await axios.put(`/api/cart/${cartItemId}/incrementQuantity`);
      fetchCartItems();
      await fetchTotalCartAmount();
    } catch (error) {
      if (error.response.status === 500) {
        toast.error("Maximum product quantity reached!");
      }
      console.error("Error incrementing quantity:", error);
    }
  };

  const decrementQuantity = async (cartItemId) => {
    try {
      await axios.put(`/api/cart/${cartItemId}/decrementQuantity`);
      fetchCartItems();
      await fetchTotalCartAmount();
    } catch (error) {
      console.error("Error decrementing quantity:", error);
    }
  };

  const deleteCartItem = async (cartItemId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this item?"
    );

    if (confirmed) {
      try {
        await axios.delete(`/api/cart/deleteItem/${cartItemId}`);
        fetchCartItems();
        await fetchTotalCartAmount();
      } catch (error) {
        console.error("Error deleting cart item:", error);
      }
    }
  };

  const handleProceedToBuy = () => {
    setShowPaymentOptions(true);
  };

  const handleInCashPayment = async () => {
    try {
      const response = await axios.post("/api/orders");
      if (response.status === 201) {
        toast.success("Order placed successfully");
        navigate(`/${username}/orders`);
      } else {
        toast.error("Failed to place order");
      }
    } catch (error) {
      console.error("Error placing order:", error);
      toast.error("Failed to place order");
    }
  };

  const handleEMIPayment = () => {
    navigate(`/${username}/cart/emi-options`, {
      state: { totalCartAmount: totalCartAmount },
    });
  };

  const getProductImageURL = (imageFileName) => {
    return `http://localhost:9090/api/products/images/${imageFileName}`;
  };

  return (
    <Container>
      <h1>Nagarro Shopping Cart</h1>
      {cartItems.length > 0 ? (
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Price</th>
              <th>Quantity</th>
              <th>Total Price</th>
              <th>Image</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {cartItems.map((item) => (
              <tr key={item.cartItemId}>
                <td>{item.product.name}</td>
                <td>₹ {item.product.price}</td>
                <td>
                  <Button
                    variant="primary"
                    className="mr-2"
                    onClick={() => decrementQuantity(item.cartItemId)}
                  >
                    -
                  </Button>
                  {item.quantity}
                  <Button
                    variant="primary"
                    className="ml-2"
                    onClick={() => incrementQuantity(item.cartItemId)}
                  >
                    +
                  </Button>
                </td>
                <td>{item.totalPrice}</td>
                <td>
                  {item.product.imageFileName ? (
                    <Image
                      src={getProductImageURL(item.product.imageFileName)}
                      alt={item.product.imageFileName}
                      style={{ width: "100px", height: "100px" }}
                    />
                  ) : (
                    <div> No image </div>
                  )}
                </td>
                <td>
                  <Button
                    variant="danger"
                    onClick={() => deleteCartItem(item.cartItemId)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      ) : (
        <p>Your cart is empty. Keep Shopping!!</p>
      )}
      <Row>
        <Col sm={4}>
          <Card
            className="text-center"
            style={{
              backgroundColor: "#f5f5f5",
              padding: "20px",
              borderRadius: "8px",
              boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)",
            }}
          >
            <h4 style={{ marginBottom: "10px", fontSize: "20px" }}>
              Total Cart Amount
            </h4>
            <h2
              style={{
                color: "#ff3f6c",
                fontWeight: "bold",
                fontSize: "19px",
              }}
            >
              Rs. {totalCartAmount}
            </h2>
          </Card>
        </Col>
      </Row>
      {cartItems.length > 0 && !showPaymentOptions && (
        <Button variant="success" onClick={handleProceedToBuy}>
          Proceed to Buy
        </Button>
      )}
      {showPaymentOptions && (
        <div>
          <Button variant="success" onClick={handleInCashPayment}>
            InCash Payment
          </Button>{" "}
          <Button variant="success" onClick={handleEMIPayment}>
            EMI Payment
          </Button>{" "}
        </div>
      )}
    </Container>
  );
};

export default Cart;
