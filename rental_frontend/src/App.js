import React, { useState, useEffect } from "react";
import LoginForm from "./components/LoginForm";
import ChatRoom from "./components/ChatRoom";

const App = () => {
  const [token, setToken] = useState(localStorage.getItem("jwtToken") || "");

  // 當使用者成功登入時，會接收到 token，並將其設置為狀態和 localStorage
  const handleLogin = (newToken) => {
    setToken(newToken);
    localStorage.setItem("jwtToken", newToken);
  };

  // 當使用者登出時，清除 token 並顯示登入表單
  const handleLogout = () => {
    setToken("");
    localStorage.removeItem("jwtToken");
  };

  return (
    <div>
      {token ? (
        <ChatRoom token={token} onLogout={handleLogout} />
      ) : (
        <LoginForm onLogin={handleLogin} />
      )}
    </div>
  );
};

export default App;
