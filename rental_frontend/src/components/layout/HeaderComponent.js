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
  Row,
  Col,
  Space,
} from "antd";
import {
  LogoutOutlined,
  LoginOutlined,
  MessageOutlined,
  HeartOutlined,
  MenuOutlined,
  UploadOutlined,
} from "@ant-design/icons";
import { useNavigate, Link } from "react-router-dom";

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
  const [isRegisterModalVisible, setIsRegisterModalVisible] = useState(false);

  const handleLoginClick = () => {
    setIsLoginModalVisible(true); // 顯示登入表單
  };

  const handleModalClose = () => {
    setIsLoginModalVisible(false); // 隱藏登入表單
  };

  const handleRegisterClick = () => {
    setIsLoginModalVisible(false); // 關閉登入表單
    setIsRegisterModalVisible(true); // 顯示註冊表單
  };

  const handleRegisterModalClose = () => {
    setIsRegisterModalVisible(false); // 隱藏註冊表單
  };

  const navigate = useNavigate();

  const handleLogoClick = () => {
    navigate("/");
    window.location.reload();
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

  const handleSubmitRegister = async (values) => {
    const {
      username,
      email,
      password,
      confirmPassword,
      mobilePhone,
      homePhone,
    } = values;
    if (password !== confirmPassword) {
      message.error("密碼與確認密碼不一致！");
      return;
    }
    setLoading(true);
    try {
      const response = await fetch("/api/user/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username,
          email,
          password,
          mobilePhone,
          homePhone,
        }),
      });

      if (response.ok) {
        message.success("註冊成功，請登入！");
        setIsRegisterModalVisible(false); // 註冊成功後關閉註冊彈窗
        setIsLoginModalVisible(true); // 打開登入彈窗
      } else {
        message.error("註冊失敗，請稍後再試！");
      }
    } catch (error) {
      console.error("Error during registration:", error);
      message.error("註冊過程中發生錯誤，請稍後再試！");
    } finally {
      setLoading(false);
    }
  };

  const handleChatClick = () => {
    setChatTargetUser("");
    setIsChatVisible(true);
  };

  const menuItems = [
    // {
    //   key: "profile",
    //   icon: <UserOutlined />,
    //   label: "個人資料",
    // },
    {
      key: "upload",
      icon: <UploadOutlined />,
      label: <Link to="/upload-property">上傳房源</Link>,
    },
    ...(token
      ? [
          {
            key: "logout",
            icon: <LogoutOutlined />,
            label: "登出",
            onClick: onLogout,
          },
        ]
      : []),
  ];

  return (
    <Header
      style={{
        background: "linear-gradient(135deg, #f0f0f0 0%, #ffffff 100%)",
        padding: "0 16px",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.15)",
        position: "sticky",
        top: 0,
        zIndex: 1000,
        height: "auto",
        minHeight: "64px",
      }}
    >
      <Row
        align="middle"
        justify="space-between"
        style={{ flexWrap: "nowrap" }}
      >
        <Col flex="none">
          <Space align="center">
            <Avatar
              src="/shiba-logo.png"
              size="large"
              style={{
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
                fontFamily: "system-ui",
                whiteSpace: "nowrap",
              }}
              onClick={handleLogoClick}
            >
              柴好租
            </Title>
          </Space>
        </Col>

        <Col>
          <Space wrap>
            {token ? (
              <>
                <span style={{ marginRight: 2 }}>您好，{currentUserId}</span>
                <Button icon={<HeartOutlined />} onClick={handleFavoriteClick}>
                  收藏夾
                </Button>
                <Button icon={<MessageOutlined />} onClick={handleChatClick}>
                  聊天室
                  <Badge
                    count={totalUnreadCount}
                    offset={[5, -5]}
                    size="small"
                  />
                </Button>

                <Dropdown menu={{ items: menuItems }} placement="bottomRight">
                  <Button icon={<MenuOutlined />}>選單</Button>
                </Dropdown>
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
          </Space>
        </Col>
      </Row>

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
            rules={[
              { required: true, message: "請輸入電子信箱!" },
              { type: "email", message: "請輸入有效的電子信箱！" },
            ]}
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
          <Form.Item>
            <Form.Item>
              <Typography.Text>
                還沒有帳號嗎？{" "}
                <Button type="link" onClick={handleRegisterClick}>
                  立即註冊
                </Button>
              </Typography.Text>
            </Form.Item>
          </Form.Item>
        </Form>
      </Modal>

      {/* 註冊表單 */}
      <Modal
        title="註冊"
        open={isRegisterModalVisible}
        onCancel={handleRegisterModalClose}
        footer={null}
      >
        <Form layout="vertical" onFinish={handleSubmitRegister}>
          <Form.Item
            label="使用者名稱(會呈現在網頁上)"
            name="username"
            rules={[{ required: true, message: "請輸入使用者名稱!" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="電子信箱(登入用)"
            name="email"
            rules={[
              { required: true, message: "請輸入電子信箱!" },
              { type: "email", message: "請輸入有效的電子信箱！" },
            ]}
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
          <Form.Item
            label="確認密碼"
            name="confirmPassword"
            rules={[{ required: true, message: "請再次輸入密碼!" }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            label="手機號碼"
            name="mobilePhone"
            rules={[
              { required: true, message: "請輸入手機號碼!" },
              {
                pattern: /^[0-9]{10}$/,
                message: "請輸入有效的手機號碼!",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="住家電話"
            name="homePhone"
            rules={[
              { required: false, message: "請輸入住家電話!" },
              {
                pattern: /^[0-9]{10}$/,
                message: "請輸入有效的住家電話!",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              註冊
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </Header>
  );
};

export default HeaderComponent;
