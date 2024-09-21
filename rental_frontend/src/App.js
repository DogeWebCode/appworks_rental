import React, { useState, useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import MainPage from "./components/MainPage";
import FooterComponent from "./components/layout/FooterComponent";
import ChatRoom from "./components/ChatRoom";
import PropertyDetail from "./components/PropertyDetail";
import HeaderComponent from "./components/layout/HeaderComponent"; // 更新導入路徑
import { jwtDecode } from "jwt-decode"; // 用於解析 JWT Token

function App() {
  const [token, setToken] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);

  // 檢查 Token 是否有效
  useEffect(() => {
    const savedToken = localStorage.getItem("jwtToken");
    if (savedToken) {
      try {
        const decodedToken = jwtDecode(savedToken);
        const currentTime = Date.now() / 1000;

        // 檢查 Token 是否過期
        if (decodedToken.exp && decodedToken.exp > currentTime) {
          setToken(savedToken);
          setCurrentUserId(decodedToken.username || decodedToken.sub); // 設置當前使用者 ID
        } else {
          localStorage.removeItem("jwtToken"); // Token 過期，移除 Token
        }
      } catch (error) {
        console.error("Invalid token:", error);
        localStorage.removeItem("jwtToken"); // 若解析出錯，也移除 Token
      }
    }
  }, []);

  // 處理登入
  const handleLogin = (newToken) => {
    localStorage.setItem("jwtToken", newToken); // 儲存 Token 到 localStorage
    setToken(newToken);

    // 解析 Token，獲取當前使用者的 ID 或 Username
    const decodedToken = jwtDecode(newToken);
    setCurrentUserId(decodedToken.sub || decodedToken.username); // Token 中的 "sub" 或 "username" 是使用者 ID
  };

  // 處理登出
  const handleLogout = () => {
    setToken(null);
    setCurrentUserId(null);
    localStorage.removeItem("jwtToken"); // 清除 Token
  };

  return (
    <>
      <HeaderComponent
        token={token}
        currentUserId={currentUserId}
        onLogin={handleLogin}
        onLogout={handleLogout}
      />
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/property/:propertyId" element={<PropertyDetail />} />
      </Routes>
      <FooterComponent />
    </>
  );
}

export default App;
