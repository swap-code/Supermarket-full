import axios from "axios";

const API_URL = "/auth";

//login user
const login = async (userData) => {
  const response = await axios.post(API_URL + "/login", userData);

  if (response.data) {
    localStorage.setItem("token", response.data.jwt);
  }
  return response.data;
};

//logout user
const logout = () => {
  localStorage.removeItem("token");
};

//signup user

const signup = async (userData, token) => {
  const response = await axios.post(`${API_URL}/register`, userData);
  return response.data;
};

//exported Function
const authService = {
  login,
  logout,
  signup,
};

export default authService;
