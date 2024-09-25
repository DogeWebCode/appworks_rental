import { useState } from "react";
import {
  Layout,
  Button,
  Avatar,
  Typography,
  Modal,
  Form,
  Input,
  message,
} from "antd";
import {
  LogoutOutlined,
  LoginOutlined,
  MessageOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import ChatRoom from "../ChatRoom";

const { Header } = Layout;
const { Title } = Typography;

const HeaderComponent = ({
  token,
  currentUserId,
  onLogin,
  onLogout,
  isLoginModalVisible,
  setIsLoginModalVisible,
  isChatVisible,
  setIsChatVisible,
  setChatTargetUser,
  chatTargetUser,
  hideChat,
}) => {
  const [loading, setLoading] = useState(false);

  const handleLoginClick = () => {
    setIsLoginModalVisible(true); // 顯示登入彈窗
  };

  const handleModalClose = () => {
    setIsLoginModalVisible(false); // 隱藏登入彈窗
  };

  const navigate = useNavigate();

  const handleLogoClick = () => {
    navigate("/");
  };

  const handleFavoriteClick = () => {
    navigate("/favorites");
  };

  const handleSubmit = async (values) => {
    const { email, password } = values;
    setLoading(true);
    try {
      const response = await fetch("/api/user/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const token = await response.text();
        localStorage.setItem("jwtToken", token);
        onLogin(token); // 傳遞 token 到父組件，透過 app.js 去做狀態管理
        handleModalClose(); // 成功後關閉登入表單
        message.success("登入成功！"); // 顯示成功訊息
      } else {
        message.error("登入失敗，請檢查帳號或密碼！");
      }
    } catch (error) {
      console.error("Error during login:", error);
      message.error("登入過程中發生錯誤，請稍後再試！");
    } finally {
      setLoading(false);
    }
  };

  const handleChatClick = () => {
    setChatTargetUser("");
    setIsChatVisible(true);
  };

  return (
    <>
      <Header
        style={{
          background: "#fff",
          padding: 0,
          boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
          zIndex: 1000,
          position: "relative",
          borderBottom: "2px solid #f0f0f0",
        }}
      >
        <div
          style={{
            width: "97.5%",
            margin: "0 auto",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            padding: "0 16px",
            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.15)",
            borderRadius: "8px",
            background: "linear-gradient(135deg, #f0f0f0 0%, #ffffff 100%)", // 背景漸變
          }}
        >
          <div style={{ display: "flex", alignItems: "center" }}>
            <Avatar
              src="/shiba-logo.png"
              size="large"
              style={{
                marginRight: 8,
                cursor: "pointer",
                boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
              }}
              onClick={handleLogoClick}
            />
            <Title
              level={3}
              style={{
                margin: 0,
                cursor: "pointer",
                color: "#333",
                textShadow: "1px 1px 3px rgba(0, 0, 0, 0.2)",
                fontFamily: "system-ui",
              }}
              onClick={handleLogoClick}
            >
              柴好租
            </Title>
          </div>
          <div>
            {token ? (
              <>
                <span style={{ marginRight: 20, fontFamily: "system-ui" }}>
                  歡迎回來，{currentUserId}
                </span>
                <Button type="link" style={{ fontFamily: "system-ui" }}>
                  個人資料
                </Button>
                <Button
                  type="link"
                  style={{ fontFamily: "system-ui" }}
                  onClick={handleFavoriteClick}
                >
                  收藏夾
                </Button>
                {/* 聊天室圖示 */}
                <Button
                  type="link"
                  icon={<MessageOutlined />}
                  style={{ fontFamily: "system-ui", marginRight: 20 }}
                  onClick={handleChatClick}
                >
                  聊天室
                </Button>
                <Button
                  type="primary"
                  icon={<LogoutOutlined />}
                  danger
                  onClick={onLogout}
                >
                  登出
                </Button>
              </>
            ) : (
              <Button
                type="primary"
                onClick={handleLoginClick}
                icon={<LoginOutlined />}
              >
                登入
              </Button>
            )}
          </div>
        </div>
      </Header>

      {/* 固定在底部的小聊天室 */}
      {isChatVisible && (
        <div
          style={{
            position: "fixed",
            bottom: 0,
            right: 20,
            width: "50vh",
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
            <Button
              type="link"
              style={{ float: "right" }}
              onClick={() => setIsChatVisible(false)}
            >
              關閉
            </Button>
          </div>
          <div style={{ height: "100%", overflowY: "auto" }}>
            <ChatRoom token={token} currentUserId={currentUserId} />
          </div>
        </div>
      )}

      {/* 登入表單的 Modal 彈窗 */}
      <Modal
        title="登入"
        open={isLoginModalVisible}
        onCancel={handleModalClose}
        footer={null}
      >
        <Form layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="電子信箱"
            name="email"
            rules={[{ required: true, message: "請輸入電子信箱!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="密碼"
            name="password"
            rules={[{ required: true, message: "請輸入密碼!" }]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登入
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default HeaderComponent;
