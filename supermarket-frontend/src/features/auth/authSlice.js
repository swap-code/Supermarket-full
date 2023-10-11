import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { parseJwt } from "../utils";
import authService from "./authService";

//getting token in usr
const usr = localStorage.getItem("token");
const initialState = {
  token: usr ? usr : null,
  role: usr ? parseJwt(usr).roles : null,
  username: usr ? parseJwt(usr).sub : null,
  isError: false,
  isSuccess: false,
  isLoading: false,
  message: "",
};

//for login
export const login = createAsyncThunk("auth/signin", async (user, thunkAPI) => {
  try {
    return await authService.login(user);
  } catch (error) {
    const message = error?.response?.data?.title;

    return thunkAPI.rejectWithValue(message);
  }
});

//for logout
export const logout = createAsyncThunk("auth/signout", async () => {
  return await authService.logout();
});

export const signup = createAsyncThunk(
  "auth/signup",
  async (user, thunkAPI) => {
    try {
      return await authService.signup(user);
    } catch (error) {
      const message = error?.response?.data;
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    reset: (state) => {
      state.isLoading = false;
      state.isSuccess = false;
      state.isError = false;
      state.message = "";
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isSuccess = true;
        state.token = action.payload.jwt;
        state.role = parseJwt(action.payload.jwt).roles;
        state.username = parseJwt(action.payload.jwt).sub;
        state.message = "Login successful";
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.isSuccess = false;
        state.token = null;
        state.message = "Enter correct username/password";
      })
      .addCase(logout.fulfilled, (state) => {
        state.token = null;
        state.username = null;
        state.role = null;
        state.exp = null;
      })
      .addCase(signup.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(signup.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isSuccess = true;
        state.message = "user registered successfully";
      })
      .addCase(signup.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = "something went wrong";
      });
  },
});

export const { reset } = authSlice.actions;
export default authSlice.reducer;
