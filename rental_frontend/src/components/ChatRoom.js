import React, { useState, useEffect, useRef, useCallback } from "react";
import {
  Box,
  Paper,
  List,
  ListItem,
  TextField,
  ListSubheader,
  Typography,
  ListItemText,
  ListItemButton,
  Avatar,
  Divider,
  IconButton,
} from "@mui/material";
import { styled } from "@mui/material/styles";
import SendIcon from "@mui/icons-material/Send";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

// ---------------------------- 日期分隔的樣式 -------------------------------------
const DateSeparator = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  margin: theme.spacing(2, 0),
}));

const DateLine = styled(Box)(({ theme }) => ({
  flex: 1,
  height: 1,
  backgroundColor: theme.palette.divider,
}));

const DateText = styled(Typography)(({ theme }) => ({
  margin: theme.spacing(0, 2),
  color: theme.palette.text.secondary,
}));

// 日期分隔符
const MessageDateSeparator = ({ date }) => (
  <DateSeparator>
    <DateLine />
    <DateText variant="body2">{date}</DateText>
    <DateLine />
  </DateSeparator>
);

// ---------------------------- 聊天室樣式 -------------------------------------

const StyledPaper = styled(Paper)({
  display: "flex",
  flexDirection: "column",
  height: "100vh",
  borderRadius: 0,
});

const ChatHeader = styled(Box)(({ theme }) => ({
  padding: theme.spacing(2),
  backgroundColor: theme.palette.primary.main,
  color: theme.palette.primary.contrastText,
  display: "flex",
  alignItems: "center",
}));

const UserList = styled(List)(({ theme }) => ({
  width: "25%",
  borderRight: `1px solid ${theme.palette.divider}`,
  overflowY: "auto",
}));

const ChatArea = styled(Box)({
  flex: 1,
  display: "flex",
  flexDirection: "column",
  overflow: "hidden",
});

const MessageList = styled(List)(({ theme }) => ({
  flex: 1,
  overflowY: "auto",
  padding: theme.spacing(2),
  backgroundColor: theme.palette.grey[100],
  display: "flex",
  flexDirection: "column",
}));

const StyledMessageBubble = styled(Box, {
  shouldForwardProp: (prop) => prop !== "isCurrentUser",
})(({ theme, isCurrentUser }) => ({
  backgroundColor: isCurrentUser
    ? theme.palette.primary.light
    : theme.palette.background.paper,
  color: isCurrentUser
    ? theme.palette.primary.contrastText
    : theme.palette.text.primary,
  borderRadius: 20,
  padding: theme.spacing(1, 2),
  maxWidth: "70%",
  wordBreak: "break-word", // 處理長詞換行
  whiteSpace: "pre-wrap", // 保持換行符和空格
  boxShadow: theme.shadows[1],
}));

const InputArea = styled(Box)(({ theme }) => ({
  display: "flex",
  padding: theme.spacing(2),
  backgroundColor: theme.palette.background.paper,
}));

// ---------------------------- 聊天室組件 -------------------------------------
const MessageBubble = ({ isCurrentUser, children }) => (
  <StyledMessageBubble isCurrentUser={isCurrentUser}>
    <Typography variant="body1">{children}</Typography>
  </StyledMessageBubble>
);

const MessageInput = React.memo(
  ({
    inputMessage,
    setInputMessage,
    sendMessage,
    isComposing,
    setIsComposing,
    disabled,
  }) => (
    <InputArea>
      <TextField
        fullWidth
        variant="outlined"
        placeholder="請輸入訊息"
        value={inputMessage}
        onChange={(e) => setInputMessage(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter" && !isComposing) {
            sendMessage();
          }
        }}
        onCompositionStart={() => setIsComposing(true)}
        onCompositionEnd={() => setIsComposing(false)}
        disabled={disabled}
        sx={{ mr: 1 }}
      />
      <IconButton
        color="primary"
        onClick={sendMessage}
        disabled={disabled || !inputMessage.trim()}
      >
        <SendIcon />
      </IconButton>
    </InputArea>
  )
);

// ---------------------------- 聊天室本體 -------------------------------------

const ChatRoom = ({ token, currentUserId }) => {
  const [messages, setMessages] = useState([]); // 訊息列表
  const [inputMessage, setInputMessage] = useState(""); // 輸入的訊息
  const [targetUserId, setTargetUserId] = useState(null); // 目標用戶 ID
  const [userList, setUserList] = useState([]); // 用戶列表
  const [targetUserName, setTargetUserName] = useState(""); // 目標用戶名稱
  const [isComposing, setIsComposing] = useState(false); // 是否正在輸入
  const stompClient = useRef(null); // Stomp 客戶端
  const messageListRef = useRef(null); // 訊息列表的引用

  const targetUserIdRef = useRef(targetUserId); // 目標用戶 ID 的引用

  useEffect(() => {
    targetUserIdRef.current = targetUserId; // 更新目標用戶 ID 的引用
  }, [targetUserId]);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws?token=" + token);
    stompClient.current = Stomp.over(socket); // 使用 Stomp 包裝 SockJS

    stompClient.current.connect(
      {},
      (frame) => {
        console.log("Connected: " + frame);

        stompClient.current.subscribe("/user/queue/message", (message) => {
          const newMessage = JSON.parse(message.body);

          // 確保聊天訊息不會跑到別人的聊天室
          if (
            (newMessage.senderId === targetUserIdRef.current &&
              newMessage.receiverId === currentUserId) ||
            (newMessage.senderId === currentUserId &&
              newMessage.receiverId === targetUserIdRef.current)
          ) {
            setMessages((prevMessages) => [...prevMessages, newMessage]);
          }
        });
      },
      (error) => {
        console.error("WebSocket connection error: " + error);
      }
    );

    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, [token, currentUserId]);

  useEffect(() => {
    fetch("/api/chat/partners", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        const filteredUsers = data.filter((user) => user !== currentUserId);
        setUserList(filteredUsers); // 設定用戶列表
      })
      .catch((error) => console.error("Error fetching chat partners:", error));
  }, [token, currentUserId]);

  const scrollToBottom = useCallback(() => {
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  }, []);

  const groupMessagesByDate = useCallback((messages) => {
    const groups = {}; // 訊息分組
    messages.forEach((msg) => {
      const date = new Date(msg.timestamp).toLocaleDateString(); // 取得日期
      if (!groups[date]) {
        groups[date] = [];
      }
      groups[date].push(msg); // 按日期分組
    });
    return groups;
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages, scrollToBottom]);

  const sendMessage = useCallback(() => {
    if (stompClient.current && inputMessage && targetUserId) {
      const chatMessage = {
        message: inputMessage,
        senderId: currentUserId,
        receiverId: targetUserId,
        timestamp: new Date().toISOString(),
      };

      setMessages((prevMessages) => [...prevMessages, chatMessage]); // 更新訊息
      stompClient.current.send("/app/message", {}, JSON.stringify(chatMessage));
      setInputMessage("");
      scrollToBottom();
    } else {
      console.log("Select a user and enter a message.");
    }
  }, [inputMessage, targetUserId, currentUserId, scrollToBottom]);

  const selectUser = useCallback(
    (userId) => {
      setTargetUserId(userId);
      setTargetUserName(userId);

      fetch(`/api/chat/messages?receiverId=${userId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then((response) => response.json())
        .then((data) => {
          setMessages(data);
          scrollToBottom();
        })
        .catch((error) =>
          console.error("Error fetching chat messages:", error)
        );
    },
    [token, scrollToBottom]
  );

  // ---------------------------- 渲染畫面 -------------------------------------

  return (
    <StyledPaper
      elevation={0}
      sx={{ height: "100vh", display: "flex", flexDirection: "column" }}
    >
      <Box display="flex" flexGrow={1} overflow="hidden" height="100vh">
        <UserList
          subheader={<ListSubheader>聊天記錄</ListSubheader>}
          sx={{ overflowY: "auto" }}
        >
          {userList.map((user) => (
            <ListItemButton key={user} onClick={() => selectUser(user)}>
              <Avatar sx={{ mr: 2 }}>{user[0].toUpperCase()}</Avatar>
              <ListItemText primary={user} />
            </ListItemButton>
          ))}
        </UserList>
        <ChatArea
          sx={{
            flexGrow: 1,
            display: "flex",
            flexDirection: "column",
            height: "100vh",
          }}
        >
          <ChatHeader sx={{ flexShrink: 0 }}>
            <Avatar sx={{ mr: 2 }}>
              {targetUserName ? targetUserName[0].toUpperCase() : "?"}
            </Avatar>
            <Typography variant="h6">
              {targetUserName
                ? `與 ${targetUserName} 聊天中`
                : "請選擇想聊天的是誰～"}
            </Typography>
          </ChatHeader>
          <Divider sx={{ flexShrink: 0 }} />

          {/* 訊息區域設定為可滾動 */}
          <MessageList
            ref={messageListRef}
            sx={{ flexGrow: 1, overflowY: "auto" }}
          >
            {Object.entries(groupMessagesByDate(messages)).map(
              ([date, msgs]) => (
                <React.Fragment key={date}>
                  <MessageDateSeparator date={date} />
                  {msgs.map((msg, index) => (
                    <ListItem
                      key={msg.id || index}
                      sx={{
                        justifyContent:
                          msg.senderId === currentUserId
                            ? "flex-end"
                            : "flex-start",
                        padding: "10px",
                      }}
                    >
                      <Box
                        sx={{
                          display: "flex",
                          flexDirection: "column",
                          alignItems:
                            msg.senderId === currentUserId
                              ? "flex-end"
                              : "flex-start",
                        }}
                      >
                        <MessageBubble
                          isCurrentUser={msg.senderId === currentUserId}
                        >
                          {msg.message}
                        </MessageBubble>
                        <Typography
                          variant="caption"
                          color="textSecondary"
                          sx={{ mt: 0.5 }}
                        >
                          {new Date(msg.timestamp).toLocaleTimeString()}
                        </Typography>
                      </Box>
                    </ListItem>
                  ))}
                </React.Fragment>
              )
            )}
          </MessageList>
          <Divider sx={{ flexShrink: 0 }} />
          <MessageInput
            inputMessage={inputMessage}
            setInputMessage={setInputMessage}
            sendMessage={sendMessage}
            isComposing={isComposing}
            setIsComposing={setIsComposing}
            disabled={!targetUserId}
            sx={{ flexShrink: 0 }}
          />
        </ChatArea>
      </Box>
    </StyledPaper>
  );
};

export default ChatRoom;
