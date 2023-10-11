import axios from "axios";
import React, { useEffect, useState } from "react";
import { Button, Table, Modal, ListGroup, ButtonGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { toast } from "react-toastify";

const UserOrders = () => {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [selectedEMI, setSelectedEMI] = useState(null);
  const [emiDetails, setEMIDetails] = useState(null);
  const navigate = useNavigate();
  const { role } = useSelector((state) => state.auth);

  useEffect(() => {
    if (role !== "USER") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const response = await axios.get("/api/orders/customer");
      if (!response.data) {
        console.error("API response data is empty:", response);
        return;
      }
      setOrders(response.data.data);
    } catch (error) {
      console.error("Failed to fetch orders:", error);
    }
  };

  const handleCancelOrder = async (orderId) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this order?"
    );

    if (confirmed) {
      try {
        await axios.put(`/api/orders/cancel/${orderId}`);
        fetchOrders();
        toast.success("Order cancelled successfully!");
      } catch (error) {
        console.error(error);
      }
    }
  };

  const getEmiDetails = async (emiId) => {
    try {
      const response = await axios.get(`/api/emi/${emiId}`);
      setEMIDetails(response.data.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleViewOrder = (order) => {
    setSelectedOrder(order);
  };

  const handleViewEMI = (emi) => {
    setSelectedEMI(emi);
    getEmiDetails(emi.id);
  };

  const handleCloseModal = () => {
    setSelectedOrder(null);
    setSelectedEMI(null);
  };

  const renderProductList = (orderItems) => {
    const uniqueProducts = new Map();

    orderItems.forEach((item) => {
      const { name } = item.product;
      const { quantity, totalPrice } = item;
      if (uniqueProducts.has(name)) {
        const existingProduct = uniqueProducts.get(name);
        existingProduct.quantity += quantity;
        existingProduct.totalPrice += totalPrice;
      } else {
        uniqueProducts.set(name, {
          quantity,
          totalPrice,
        });
      }
    });

    return (
      <ListGroup>
        {[...uniqueProducts.entries()].map(([name, product]) => (
          <ListGroup.Item key={name}>
            <div>
              <strong>Product Name: </strong>
              {name}
            </div>
            <div>
              <strong>Quantity: </strong>
              {product.quantity}
            </div>
            <div>
              <strong>Total Price: ₹</strong>
              {product.totalPrice}
            </div>
          </ListGroup.Item>
        ))}
      </ListGroup>
    );
  };

  const renderEMIButton = (order) => {
    if (order.emiId) {
      return (
        <Button
          variant="info"
          onClick={() => handleViewEMI(order.emiId)}
          style={{ marginLeft: "10px" }}
        >
          EMI
        </Button>
      );
    }
    return null;
  };

  const renderEMIDetails = () => {
    if (selectedEMI && emiDetails) {
      return (
        <div>
          <h5>Tenure: {emiDetails.tenure} months</h5>
          <h5>ROI: {emiDetails.roi}% per annum</h5>
          <h5>Monthly Installment: ₹ {emiDetails.monthlyInstallment}</h5>
        </div>
      );
    }
    return null;
  };

  return (
    <div>
      <h1 className="text-center">Your Orders</h1>
      <div className="d-flex justify-content-center">
        <Table striped bordered hover responsive style={{ maxWidth: "800px" }}>
          <thead>
            <tr>
              <th>S. No.</th>
              <th>Order ID</th>
              <th>Order Amount</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order, index) => (
              <tr key={order.orderId}>
                <td>{index + 1}</td>
                <td>{order.orderId}</td>
                <td>₹ {order.totalOrderAmount}</td>
                <td>{order.status}</td>
                <td>
                  {order.status === "PROCESSING" ? (
                    <ButtonGroup>
                      {order.status !== "COMPLETED" &&
                      order.status !== "CANCELLED" ? (
                        <Button
                          variant="danger"
                          onClick={() => handleCancelOrder(order.orderId)}
                        >
                          Cancel Order
                        </Button>
                      ) : (
                        <Button variant="danger" disabled>
                          Cancel Order
                        </Button>
                      )}
                    </ButtonGroup>
                  ) : (
                    <ButtonGroup>
                      <Button variant="danger" disabled>
                        Cancel Order
                      </Button>
                    </ButtonGroup>
                  )}

                  <Button
                    variant="primary"
                    onClick={() => handleViewOrder(order)}
                    style={{ marginLeft: "10px" }}
                  >
                    View Order
                  </Button>
                  {renderEMIButton(order)}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>

      <Modal show={selectedOrder !== null} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>Order Details</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedOrder && (
            <>
              <h5>Order ID: {selectedOrder.orderId}</h5>
              <h5>Order Amount: ₹ {selectedOrder.totalOrderAmount}</h5>
              <h5>Status: {selectedOrder.status}</h5>
              <h5>Order Items:</h5>
              {renderProductList(selectedOrder.orderItems)}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      <Modal show={selectedEMI !== null} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>EMI Details</Modal.Title>
        </Modal.Header>
        <Modal.Body>{renderEMIDetails()}</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default UserOrders;
