import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AdminOrders from "./components/admin/AdminOrders";
import Header from "./components/Header";
import Login from "./components/Login";
import UserOrders from "./components/customer/UserOrders";
import ActiveProductsAdmin from "./components/admin/ActiveProductsAdmin";
import Cart from "./components/customer/Cart";
import AllUser from "./components/admin/AllUser";
import Home from "./components/Home";
import AddNewProduct from "./components/admin/AddNewProduct";
import EditProduct from "./components/admin/EditProduct";
import InActiveProducts from "./components/admin/InActiveProducts";
import SignUp from "./components/SignUp";
import EmiOptionsPage from "./components/customer/EmiOptionsPage";
import NotFound from "./components/NotFound";
import { useSelector } from "react-redux";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const { role } = useSelector((state) => state.auth);

  return (
    <Router>
      <ToastContainer />
      <Header />
      <Routes>
        {/* common routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        <Route element={<NotFound />} path="*" />

        {role === "USER" && (
          <>
            <Route
              path="/:username/cart/emi-options"
              element={<EmiOptionsPage />}
            />
            <Route path="/:username/cart" element={<Cart />} />
            <Route path="/:username/orders" element={<UserOrders />} />
          </>
        )}

        {role === "ADMIN" && (
          <>
            <Route path="/:username" element={<ActiveProductsAdmin />} />
            <Route path="/:username/add-product" element={<AddNewProduct />} />
            <Route path="/:username/edit-product" element={<EditProduct />} />
            <Route
              path="/:username/inactive-products"
              element={<InActiveProducts />}
            />
            <Route path="/:username/all-orders" element={<AdminOrders />} />
            <Route path="/:username/all-users" element={<AllUser />} />
          </>
        )}
      </Routes>
    </Router>
  );
}

export default App;
