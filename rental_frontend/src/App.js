import { useState, useEffect } from "react";
import { Route, Routes } from "react-router-dom";
import PropertyDetail from "./components/PropertyDetail";
import HeaderComponent from "./components/layout/HeaderComponent";
import FavoriteList from "./components/FavoriteList";
import FooterComponent from "./components/layout/FooterComponent";
import { jwtDecode } from "jwt-decode"; // 用於解析 JWT Token
import HomePage from "./components/HomePage";
import { message } from "antd";
import ChatRoom from "./components/ChatRoom";

function App() {
  const [token, setToken] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);
  const [isChatVisible, setIsChatVisible] = useState(false);
  const [chatTargetUser, setChatTargetUser] = useState(null);

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

  // 顯示聊天室並設置聊天對象
  const showChat = (targetUserId) => {
    setChatTargetUser(targetUserId);
    setIsChatVisible(true);
  };

  // 隱藏聊天室
  const hideChat = () => {
    setIsChatVisible(false);
    setChatTargetUser(null);
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
        isChatVisible={isChatVisible}
        setIsChatVisible={setIsChatVisible}
        chatTargetUser={chatTargetUser}
        setChatTargetUser={setChatTargetUser}
        hideChat={hideChat}
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
              showChat={showChat}
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
      {/* 固定在底部的小聊天室 */}
      {isChatVisible && (
        <div
          style={{
            position: "fixed",
            bottom: 0,
            right: 20,
            width: 500,
            background: "#fff",
            border: "1px solid #ddd",
            boxShadow: "0px 0px 10px rgba(0,0,0,0.1)",
            borderRadius: "10px 10px 0 0",
            zIndex: 1000,
            overflow: "hidden",
          }}
        >
          <div
            style={{
              padding: "10px",
              background: "#fafafa",
              borderBottom: "1px solid #ddd",
            }}
          >
            <span style={{ fontWeight: "bold" }}>聊天室</span>
            <button
              style={{
                float: "right",
                border: "none",
                background: "none",
                cursor: "pointer",
              }}
              onClick={hideChat}
            >
              關閉
            </button>
          </div>
          <div style={{ height: "100%", overflowY: "auto" }}>
            <ChatRoom
              token={token}
              currentUserId={currentUserId}
              targetUserId={chatTargetUser}
            />
          </div>
        </div>
      )}
      <FooterComponent />
    </>
  );
}

export default App;
