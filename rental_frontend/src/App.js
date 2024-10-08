import { useState, useEffect, useRef } from "react";
import { Route, Routes } from "react-router-dom";
import PropertyDetail from "./components/PropertyDetail";
import HeaderComponent from "./components/layout/HeaderComponent";
import FavoriteList from "./components/FavoriteList";
import FooterComponent from "./components/layout/FooterComponent";
import { jwtDecode } from "jwt-decode";
import HomePage from "./components/HomePage";
import { message } from "antd";
import ChatRoom from "./components/ChatRoom";
import { Client } from "@stomp/stompjs";
import PropertyUploadForm from "./components/PropertyUploadForm";

function App() {
  const [token, setToken] = useState(null);
  const [currentUserId, setCurrentUserId] = useState(null);
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);
  const [isChatVisible, setIsChatVisible] = useState(false);
  const [chatTargetUser, setChatTargetUser] = useState(null);
  const [unreadCounts, setUnreadCounts] = useState({});
  const [wsConnected, setWsConnected] = useState(false);
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 5;
  const totalUnreadCount = Object.values(unreadCounts).reduce(
    (sum, count) => sum + count,
    0
  );
  const stompClient = useRef(null);
  const wsEndpoint =
    process.env.NODE_ENV === "development"
      ? process.env.REACT_APP_WS_ENDPOINT_DEV
      : process.env.REACT_APP_WS_ENDPOINT_PROD;

  const handleWebSocketError = (errorMessage) => {
    // 設置 WebSocket 連接狀態為斷開
    setWsConnected(false);

    // 如果尚未超過最大重連次數，嘗試重連
    if (reconnectAttempts.current < maxReconnectAttempts) {
      reconnectAttempts.current += 1;
      console.log(
        `嘗試重新連接... (${reconnectAttempts.current}/${maxReconnectAttempts})`
      );

      // 這裡可以直接嘗試重啟 stompClient 連接
      if (stompClient.current) {
        stompClient.current.deactivate(); // 先停用現有的連接
        setTimeout(() => {
          stompClient.current.activate(); // 然後重新啟動連接
        }, 5000); // 設定 5 秒的延遲後重新連接
      }
    } else {
      console.error("WebSocket 連接失敗: ", errorMessage);
    }
  };

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

  const chatTargetUserRef = useRef(chatTargetUser);

  useEffect(() => {
    chatTargetUserRef.current = chatTargetUser;
  }, [chatTargetUser]);

  useEffect(() => {
    if (token && currentUserId) {
      stompClient.current = new Client({
        brokerURL: `wss://goodshiba.com/ws?token=${token}`,
        // brokerURL: `ws://localhost:8080/ws?token=${token}`,
        reconnectDelay: 5000, // 設置重新連接的延遲
        heartbeatIncoming: 20000, // 設置心跳檢查
        heartbeatOutgoing: 20000,

        onConnect: (frame) => {
          console.log("Connected: " + frame);
          setWsConnected(true);
          reconnectAttempts.current = 0;

          // 訂閱新訊息
          stompClient.current.subscribe("/user/queue/message", (message) => {
            const newMessage = JSON.parse(message.body);
            const senderId = newMessage.senderId;

            if (senderId !== chatTargetUserRef.current) {
              setUnreadCounts((prevCounts) => ({
                ...prevCounts,
                [senderId]: (prevCounts[senderId] || 0) + 1,
              }));
            }
          });
        },

        onStompError: (frame) => {
          console.error("Broker reported error: " + frame.headers["message"]);
          handleWebSocketError("STOMP Error");
        },

        onWebSocketClose: (evt) => {
          console.log("WebSocket closed", evt);
          handleWebSocketError("Connection closed");
        },

        onWebSocketError: (evt) => {
          console.error("WebSocket error", evt);
          handleWebSocketError("Connection error");
        },
      });

      stompClient.current.activate();
    }

    if (token) {
      fetch(`/api/chat/messages/unread/count`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then((response) => {
          if (!response.ok) {
            return response.json().then((errorData) => {
              throw new Error(
                errorData.message || "Failed to fetch unread counts"
              );
            });
          }
          return response.json();
        })
        .then((data) => {
          setUnreadCounts(data);
        })
        .catch((error) =>
          console.error("Error fetching unread counts:", error)
        );
    }

    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
        setWsConnected(false); // 斷開連接時更新狀態
        console.log("WebSocket connection closed");
      }
    };
  }, [token, currentUserId, wsEndpoint]);

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
    setUnreadCounts({});
    localStorage.removeItem("jwtToken"); // 清除 Token
    message.success("登出成功！");
  };

  // 顯示聊天室並設置聊天對象
  const showChat = (targetUserId) => {
    if (!wsConnected) {
      console.log(wsConnected);
      message.warning("正在嘗試連接聊天服務，請稍後再試。");
      return;
    }
    if (targetUserId) {
      setChatTargetUser(targetUserId);
      setIsChatVisible(true);
    } else {
      console.log("Please select a user first");
    }
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
        totalUnreadCount={totalUnreadCount}
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
        <Route
          path="/upload-property"
          element={
            <PropertyUploadForm
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
              setChatTargetUser={setChatTargetUser}
              unreadCounts={unreadCounts}
              setUnreadCounts={setUnreadCounts}
              stompClient={stompClient.current}
              wsConnected={wsConnected}
            />
          </div>
        </div>
      )}
      <FooterComponent />
    </>
  );
}

export default App;
