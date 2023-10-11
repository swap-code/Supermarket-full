import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { Container, Table, Form } from "react-bootstrap";
import SideBar from "../SideBar";

export default function AllUser() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [filter, setFilter] = useState("all");
  const { role } = useSelector((state) => state.auth);

  useEffect(() => {
    if (role !== "ADMIN") {
      navigate("/");
    }
  }, [role, navigate]);

  useEffect(() => {
    fetchAllUsers();
  }, []);

  const activateUser = async (userId) => {
    try {
      await axios.post(`auth/${userId}/activate`);
      updateUserStatus(userId, true);
    } catch (error) {
      console.error(error);
    }
  };

  const deactivateUser = async (userId) => {
    try {
      await axios.post(`auth/${userId}/inactivate`);
      updateUserStatus(userId, false);
    } catch (error) {
      console.error(error);
    }
  };

  const updateUserStatus = (userId, active) => {
    setUsers((prevUsers) => {
      return prevUsers.map((user) => {
        if (user.userId === userId) {
          return { ...user, active };
        }
        return user;
      });
    });
  };

  const fetchAllUsers = async () => {
    try {
      const response = await axios.get("auth/users");
      setUsers(response.data.data);
    } catch (error) {
      console.error("Unable to process", error);
    }
  };

  const handleFilterChange = (event) => {
    setFilter(event.target.value);
  };

  const filteredUsers =
    filter === "active"
      ? users.filter((user) => user.active)
      : filter === "inactive"
      ? users.filter((user) => !user.active)
      : users;

  return (
    <>
      <div className="d-flex">
        <SideBar />
        <Container className="mt-5">
          <div className="mb-3">
            <Form.Label htmlFor="filterDropdown">Filter by status:</Form.Label>
            <Form.Select
              id="filterDropdown"
              value={filter}
              onChange={handleFilterChange}
            >
              <option value="all">All</option>
              <option value="active">Activated</option>
              <option value="inactive">Inactivated</option>
            </Form.Select>
          </div>
          <Table bordered striped>
            <thead>
              <tr className="text-center">
                <th style={{ backgroundColor: "black", color: "white" }}>
                  USERID
                </th>
                <th style={{ backgroundColor: "black", color: "white" }}>
                  USERNAME
                </th>
                <th style={{ backgroundColor: "black", color: "white" }}>
                  ACTIVE STATUS
                </th>
                <th style={{ backgroundColor: "black", color: "white" }}>
                  CHANGE STATUS
                </th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((user) => (
                <tr className="text-center" key={user.userId}>
                  <td>{user.userId}</td>
                  <td>{user.username}</td>
                  <td>{user.active ? "Active" : "Inactive"}</td>
                  <td>
                    {user.active ? (
                      <button
                        className="btn btn-danger"
                        onClick={() => deactivateUser(user.userId)}
                      >
                        Deactivate
                      </button>
                    ) : (
                      <button
                        className="btn btn-success"
                        onClick={() => activateUser(user.userId)}
                      >
                        Activate
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Container>
      </div>
    </>
  );
}
