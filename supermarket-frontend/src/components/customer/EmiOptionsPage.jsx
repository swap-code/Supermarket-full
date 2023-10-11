import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { useSelector } from "react-redux";
import { toast } from "react-toastify";

const EmiOptionsPage = (props) => {
  const navigate = useNavigate();
  const location = useLocation();
  const totalCartAmount = location.state.totalCartAmount;
  const [selectedOption, setSelectedOption] = useState(null);
  const [emiDetails, setEmiDetails] = useState({
    emiAmount: null,
    selectedMonths: "",
    rateOfInterest: null,
  });
  const { role, username } = useSelector((state) => state.auth);

  const emiOptions = [
    {
      id: 1,
      name: "Option 1",
      emi: "Tenure of 1 Year – 8%",
      minMonths: 1,
      maxMonths: 12,
      rateOfInterest: 8,
    },
    {
      id: 2,
      name: "Option 2",
      emi: "Tenure of 1 Year to 2 Year – 10%",
      minMonths: 13,
      maxMonths: 24,
      rateOfInterest: 10,
    },
    {
      id: 3,
      name: "Option 3",
      emi: "Tenure of 2 Year to 5 Year – 14%",
      minMonths: 25,
      maxMonths: 60,
      rateOfInterest: 14,
    },
  ];

  useEffect(() => {
    if (role !== "USER") {
      navigate("/");
    }
  }, [role, navigate]);

  const handleSelectOption = (option) => {
    setSelectedOption(option);
    setEmiDetails((prevState) => ({
      ...prevState,
      selectedMonths: "",
      emiAmount: null,
      rateOfInterest: null,
    }));
  };

  const handleMonthsChange = (e) => {
    const enteredMonths = parseInt(e.target.value);
    setEmiDetails((prevState) => ({
      ...prevState,
      selectedMonths: enteredMonths,
    }));
    calculateEmi(enteredMonths);
  };

  const calculateEmi = (months) => {
    if (selectedOption && months) {
      const { minMonths, maxMonths, rateOfInterest } = selectedOption;
      if (months >= minMonths && months <= maxMonths) {
        const principalAmount = totalCartAmount;
        const interest =
          (principalAmount * rateOfInterest * months) / (12 * 100);
        const totalAmount = principalAmount + interest;
        const emi = totalAmount / months;
        setEmiDetails((prevState) => ({
          ...prevState,
          emiAmount: emi.toFixed(2),
          rateOfInterest: rateOfInterest,
        }));
      } else {
        setEmiDetails((prevState) => ({
          ...prevState,
          emiAmount: null,
          rateOfInterest: null,
        }));
      }
    } else {
      setEmiDetails((prevState) => ({
        ...prevState,
        emiAmount: null,
        rateOfInterest: null,
      }));
    }
  };

  const handleConfirmOrder = async () => {
    const { emiAmount, selectedMonths, rateOfInterest } = emiDetails;
    const monthlyInstallment = parseInt(emiAmount);
    const roi = rateOfInterest;
    const tenure = selectedMonths;
    if (selectedMonths && emiAmount && rateOfInterest) {
      try {
        const response = await axios.post("/api/orders", {
          monthlyInstallment,
          roi,
          tenure,
        });
        if (response.status === 200 || response.status === 201) {
          toast.success("Order placed successfully!");
          navigate(`/${username}/orders`);
        } else {
          toast.error("Failed to place order. Please try again.");
        }
      } catch (error) {
        console.error("Error placing order:", error);
        toast.error("Failed to place order. Please try again.");
      }
    } else {
      toast.error("Please select a valid EMI option");
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center vh-100">
      <div className="row">
        <div className="col-sm-12">
          <div className="card-group">
            {emiOptions.map((option) => (
              <div key={option.id} className="card">
                <div className="card-body">
                  <h2 className="card-title">{option.name}</h2>
                  <p className="card-text">EMI: {option.emi}</p>
                  <button
                    className="btn btn-primary mt-2"
                    onClick={() => handleSelectOption(option)}
                  >
                    Select
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
        {selectedOption && (
          <div className="col-md-12 mt-4">
            <div className="selected-option">
              <h2>Selected Option</h2>
              <p>Name: {selectedOption.name}</p>
              <p>TotalAmount: {totalCartAmount}</p>
              <p>EMI: {selectedOption.emi}</p>
            </div>
            <div className="form-group mt-4">
              <label htmlFor="monthsInput">Enter Number of Months:</label>
              <input
                type="number"
                className="form-control form-control-sm"
                id="monthsInput"
                value={emiDetails.selectedMonths}
                onChange={handleMonthsChange}
                min={selectedOption.minMonths}
                max={selectedOption.maxMonths}
                style={{ width: "100px" }}
              />
              {emiDetails.selectedMonths &&
                (emiDetails.selectedMonths < selectedOption.minMonths ||
                  emiDetails.selectedMonths > selectedOption.maxMonths) && (
                  <p className="text-danger">
                    Please enter a valid number of months between{" "}
                    {selectedOption.minMonths} and {selectedOption.maxMonths}.
                  </p>
                )}
            </div>
            {emiDetails.emiAmount !== null && (
              <div className="emi-amount">
                <h2>EMI Calculation</h2>
                <p>Number of Months: {emiDetails.selectedMonths}</p>
                <p>EMI Amount: {emiDetails.emiAmount}</p>
              </div>
            )}
            {emiDetails.selectedMonths && emiDetails.emiAmount && (
              <button
                className="btn btn-success mt-4"
                onClick={handleConfirmOrder}
              >
                Confirm Order
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default EmiOptionsPage;
