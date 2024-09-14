import React, { useState, useEffect } from "react";
import { Box, Paper, List, ListItem, TextField, Button } from "@mui/material";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const ChatRoom = ({ token, onLogout }) => {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState("");
  let stompClient = null;

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({ Authorization: `Bearer ${token}` }, () => {
      stompClient.subscribe("/user/queue/messages", (message) => {
        const newMessage = JSON.parse(message.body);
        setMessages((prevMessages) => [...prevMessages, newMessage]);
      });
    });

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [token]);

  const sendMessage = () => {
    if (stompClient && inputMessage) {
      const chatMessage = { message: inputMessage };
      stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
      setInputMessage("");
    }
  };

  return (
    <Box sx={{ width: "400px", margin: "auto", mt: 5 }}>
      <Paper elevation={3} sx={{ padding: 2 }}>
        <List sx={{ maxHeight: 300, overflow: "auto" }}>
          {messages.map((msg, index) => (
            <ListItem key={index}>{msg.message}</ListItem>
          ))}
        </List>
        <TextField
          label="Enter message"
          fullWidth
          variant="outlined"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
        />
        <Button
          variant="contained"
          fullWidth
          onClick={sendMessage}
          sx={{ marginTop: 2 }}
        >
          Send
        </Button>
        <Button
          variant="outlined"
          fullWidth
          onClick={onLogout}
          sx={{ marginTop: 2 }}
        >
          Logout
        </Button>
      </Paper>
    </Box>
  );
};

export default ChatRoom;
