import React, { useState } from "react";
import LoginForm from "./components/LoginForm";
import ChatRoom from "./components/ChatRoom";
import { jwtDecode } from "jwt-decode"; // 用於解析 JWT Token

function App() {
  const [token, setToken] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);

  const handleLogin = (token) => {
    setToken(token);

    // 解析 Token，獲取當前使用者的 ID 或 Username
    const decodedToken = jwtDecode(token);
    console.log(decodedToken);
    setCurrentUserId(decodedToken.sub || decodedToken.username); // Token 中的 "sub" 或 "username" 是使用者 ID
  };

  const handleLogout = () => {
    setToken(null);
    setCurrentUserId(null);
  };

  return (
    <div>
      {token ? (
        <ChatRoom
          token={token}
          currentUserId={currentUserId} // 動態設置當前使用者 ID
          onLogout={handleLogout}
        />
      ) : (
        <LoginForm onLogin={handleLogin} />
      )}
    </div>
  );
}

export default App;
