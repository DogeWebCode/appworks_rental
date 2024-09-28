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
  Badge,
  Dropdown,
  Menu,
  Row,
  Col,
} from "antd";
import {
  LogoutOutlined,
  LoginOutlined,
  MessageOutlined,
  HeartOutlined,
  UserOutlined,
  MenuOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";

const { Header } = Layout;
const { Title } = Typography;

const HeaderComponent = ({
  token,
  currentUserId,
  onLogin,
  onLogout,
  isLoginModalVisible,
  setIsLoginModalVisible,
  setIsChatVisible,
  setChatTargetUser,
  totalUnreadCount,
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

  const menu = (
    <Menu>
      <Menu.Item key="profile" icon={<UserOutlined />}>
        個人資料
      </Menu.Item>
      <Menu.Item
        key="favorites"
        icon={<HeartOutlined />}
        onClick={handleFavoriteClick}
      >
        收藏夾
      </Menu.Item>
      <Menu.Item
        key="chat"
        icon={<MessageOutlined />}
        onClick={handleChatClick}
      >
        聊天室
        <Badge count={totalUnreadCount} offset={[5, -5]} size="small" />
      </Menu.Item>
      <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={onLogout}>
        登出
      </Menu.Item>
    </Menu>
  );

  return (
    <Header
      style={{
        background: "linear-gradient(135deg, #f0f0f0 0%, #ffffff 100%)", // 背景漸變
        padding: "0 16px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.15)",
        borderRadius: "8px 0",
        position: "sticky",
        top: 0,
        zIndex: 1000,
      }}
    >
      <Row align="middle" justify="space-between">
        <Col>
          <Row align="middle" gutter={8}>
            <Col>
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
            </Col>
            <Col>
              <Title
                level={3}
                style={{
                  margin: 0,
                  cursor: "pointer",
                  color: "#333",

                  fontFamily: "system-ui",
                }}
                onClick={handleLogoClick}
              >
                柴好租
              </Title>
            </Col>
          </Row>
        </Col>
        {token && (
          <Col flex="auto" style={{ textAlign: "end", marginRight: 12 }}>
            <span style={{ fontFamily: "system-ui", fontSize: "16px" }}>
              歡迎回來，{currentUserId}
            </span>
          </Col>
        )}
        <Col>
          <Row align="middle" gutter={16}>
            {token ? (
              <>
                <Col xs={0} md={24}>
                  <Row gutter={16}>
                    <Col>
                      <Button
                        type="link"
                        style={{ fontFamily: "system-ui" }}
                        icon={<UserOutlined />}
                      >
                        個人資料
                      </Button>
                    </Col>
                    <Col>
                      <Button
                        type="link"
                        style={{ fontFamily: "system-ui" }}
                        onClick={handleFavoriteClick}
                        icon={<HeartOutlined />}
                      >
                        收藏夾
                      </Button>
                    </Col>
                    <Col>
                      <Button
                        type="link"
                        style={{ fontFamily: "system-ui", marginRight: 10 }}
                        onClick={handleChatClick}
                      >
                        <Badge
                          count={totalUnreadCount}
                          offset={[5, -5]}
                          size="small"
                        >
                          <MessageOutlined style={{ color: "#1677FF" }} />
                        </Badge>
                        聊天室
                      </Button>
                    </Col>
                    <Col>
                      <Button
                        type="primary"
                        icon={<LogoutOutlined />}
                        danger
                        onClick={onLogout}
                      >
                        登出
                      </Button>
                    </Col>
                  </Row>
                </Col>
                <Col xs={24} md={0}>
                  <Dropdown overlay={menu} placement="bottomRight">
                    <Button icon={<MenuOutlined />} />
                  </Dropdown>
                </Col>
              </>
            ) : (
              <Col>
                <Button
                  type="primary"
                  onClick={handleLoginClick}
                  icon={<LoginOutlined />}
                >
                  登入
                </Button>
              </Col>
            )}
          </Row>
        </Col>
      </Row>

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
    </Header>
  );
};

export default HeaderComponent;
