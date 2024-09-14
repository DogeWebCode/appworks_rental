// src/components/LoginForm.js
import React, { useState } from "react";
import { TextField, Button, Box } from "@mui/material";

const LoginForm = ({ onLogin }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async () => {
    try {
      const response = await fetch("/api/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const token = await response.text();
        localStorage.setItem("jwtToken", token);
        onLogin(token); // 傳遞 token 到上層
      } else {
        console.error("Login failed");
      }
    } catch (error) {
      console.error("Error during login:", error);
    }
  };

  return (
    <Box sx={{ width: "300px", margin: "auto", mt: 5 }}>
      <TextField
        label="Username"
        fullWidth
        variant="outlined"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <TextField
        label="Password"
        type="password"
        fullWidth
        variant="outlined"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        sx={{ marginTop: 2 }}
      />
      <Button
        variant="contained"
        fullWidth
        onClick={handleSubmit}
        sx={{ marginTop: 2 }}
      >
        Login
      </Button>
    </Box>
  );
};

export default LoginForm;
