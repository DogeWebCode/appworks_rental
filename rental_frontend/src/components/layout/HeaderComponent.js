import React, { useState } from "react";
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
import { LogoutOutlined, LoginOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";

const { Header } = Layout;
const { Title } = Typography;

const HeaderComponent = ({ token, currentUserId, onLogin, onLogout }) => {
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false);
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

  const handleSubmit = async (values) => {
    const { username, password } = values;
    setLoading(true);
    try {
      const response = await fetch("/api/user/login", {
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
        handleModalClose(); // 成功後關閉登入表單
        message.success("登入成功！"); // 顯示成功訊息
      } else {
        message.error("登入失敗，請檢查帳號或密碼！"); // 顯示失敗訊息
      }
    } catch (error) {
      console.error("Error during login:", error);
      message.error("登入過程中發生錯誤，請稍後再試！"); // 顯示錯誤訊息
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Header style={{ background: "#fff", padding: 0 }}>
        <div
          style={{
            maxWidth: 1200,
            margin: "0 auto",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <div style={{ display: "flex", alignItems: "center" }}>
            <Avatar
              src="/shiba-logo.png"
              size="large"
              style={{ marginRight: 8, cursor: "pointer" }}
              onClick={handleLogoClick}
            />
            <Title
              level={3}
              style={{ margin: 0, cursor: "pointer" }}
              onClick={handleLogoClick} // 點擊事件
            >
              柴好租
            </Title>
          </div>
          <div>
            {token ? (
              <>
                <span style={{ marginRight: 20 }}>
                  歡迎回來，{currentUserId}
                </span>
                <Button type="link">個人資料</Button>
                <Button type="link">房源收藏夾</Button>
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

      {/* 登入表單的 Modal 彈窗 */}
      <Modal
        title="登入"
        open={isLoginModalVisible}
        onCancel={handleModalClose}
        footer={null}
      >
        <Form layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            label="帳號"
            name="username"
            rules={[{ required: true, message: "請輸入帳號!" }]}
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
