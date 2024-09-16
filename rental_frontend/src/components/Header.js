import React from "react";
import { Box, Typography, Avatar, IconButton } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import ManageAccountsIcon from "@mui/icons-material/ManageAccounts";
import AnnouncementIcon from "@mui/icons-material/Announcement";
import NotificationsIcon from "@mui/icons-material/Notifications";
import MailIcon from "@mui/icons-material/Mail";

const Header = ({ children }) => {
  return (
    <Box sx={{ height: "100vh", display: "flex", flexDirection: "column" }}>
      {/* Header 固定在頂部 */}
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "10px 20px",
          backgroundColor: "rgb(255,165,0)",
          boxShadow: "0px 2px 5px rgba(0,0,0,0.1)",
          flexShrink: 0,
          position: "sticky",
          top: 0,
          zIndex: 1000,
        }}
      >
        {/* 左側標誌 */}
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <img src="/path-to-logo.png" alt="logo" style={{ height: 40 }} />{" "}
          {/* 替換為你的 logo 圖片路徑 */}
          <Typography variant="h6" sx={{ ml: 2, color: "red" }}>
            柴好租
          </Typography>
        </Box>

        {/* 右側圖標導航 */}
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <IconButton>
            <HomeIcon />
          </IconButton>
          <IconButton>
            <ManageAccountsIcon />
          </IconButton>
          <IconButton>
            <AnnouncementIcon />
          </IconButton>
          <IconButton>
            <NotificationsIcon />
          </IconButton>
          <IconButton>
            <MailIcon />
          </IconButton>
          <Avatar sx={{ ml: 2 }} />
        </Box>
      </Box>

      {/* 剩餘的部分 */}
      <Box
        sx={{
          flexGrow: 1,
          overflow: "auto",
          display: "flex",
          flexDirection: "column",
        }}
      >
        {children}
      </Box>
    </Box>
  );
};

export default Header;
