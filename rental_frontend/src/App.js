import { useState, useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import PropertyDetail from "./components/PropertyDetail";
import HeaderComponent from "./components/layout/HeaderComponent";
import FavoriteList from "./components/FavoriteList";
import FooterComponent from "./components/layout/FooterComponent";
import { jwtDecode } from "jwt-decode"; // 用於解析 JWT Token
import HomePage from "./components/HomePage";
import { message } from "antd";

function App() {
  const [token, setToken] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);

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
          console.log(decodedToken);
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
    localStorage.setItem("jwtToken", newToken);
    setToken(newToken);

    const decodedToken = jwtDecode(newToken);
    setCurrentUserId(decodedToken.sub || decodedToken.username);
    setIsLoginModalVisible(false);
  };

  // 處理登出
  const handleLogout = () => {
    setToken(null);
    setCurrentUserId(null);
    localStorage.removeItem("jwtToken"); // 清除 Token
    message.success("登出成功！");
  };

  return (
    <>
      <HeaderComponent
        token={token}
        currentUserId={currentUserId}
        onLogin={handleLogin}
        onLogout={handleLogout}
        isLoginModalVisible={isLoginModalVisible}
        setIsLoginModalVisible={setIsLoginModalVisible}
      />
      <Routes>
        <Route
          path="/property/:propertyId"
          element={
            <PropertyDetail
              token={token}
              currentUserId={currentUserId}
              isLoginModalVisible={isLoginModalVisible}
              setIsLoginModalVisible={setIsLoginModalVisible}
            />
          }
        />
        <Route
          path="/"
          element={
            <HomePage
              token={token}
              currentUserId={currentUserId}
              isLoginModalVisible={isLoginModalVisible}
              setIsLoginModalVisible={setIsLoginModalVisible}
            />
          }
        />
        <Route
          path="/favorites"
          element={
            <FavoriteList
              token={token}
              currentUserId={currentUserId}
              isLoginModalVisible={isLoginModalVisible}
              setIsLoginModalVisible={setIsLoginModalVisible}
            />
          }
        />
      </Routes>
      <FooterComponent /> {/* 固定 Footer 到頁面底部 */}
    </>
  );
}

export default App;
