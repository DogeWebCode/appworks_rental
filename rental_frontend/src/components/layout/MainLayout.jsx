import React from "react";
import { Layout } from "antd";

const { Content } = Layout;

const MainLayout = ({ children }) => {
  return (
    <Layout
      style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}
    >
      <Content style={{ padding: "0", flex: 1 }}>
        {children} {/* 子頁面內容 */}
      </Content>
    </Layout>
  );
};

export default MainLayout;
