import React, { useState } from "react";
import { Form, Input, Button, Modal } from "antd";

const LoginForm = ({ visible, onClose, onLogin }) => {
  const [loading, setLoading] = useState(false);

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
        onClose(); // 關閉 Modal
      } else {
        console.error("Login failed");
      }
    } catch (error) {
      console.error("Error during login:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title="登入" visible={visible} onCancel={onClose} footer={null}>
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
  );
};

export default LoginForm;
