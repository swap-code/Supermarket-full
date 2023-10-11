import React from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import "bootstrap/dist/css/bootstrap.min.css";
import axios from "axios";
import { Provider } from "react-redux";
import { store } from "./features/store";
import "@fortawesome/fontawesome-free/css/all.css";
import "@fortawesome/fontawesome-svg-core/styles.css";

axios.defaults.baseURL = "http://localhost:9090";
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

const root = createRoot(document.getElementById("root"));
root.render(
  <Provider store={store}>
    <App />
  </Provider>
);

reportWebVitals();
