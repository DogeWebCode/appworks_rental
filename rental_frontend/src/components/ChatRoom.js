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

const StyledPaper = styled(Paper)(({ theme }) => ({
  height: "55vh",
  maxWidth: "800px",
  margin: "auto", // 居中
  display: "flex",
  flexDirection: "column",
  borderRadius: theme.shape.borderRadius,
  overflow: "hidden",
  boxShadow: theme.shadows[3],
}));

const ChatContainer = styled(Box)({
  display: "flex",
  flex: 1,
  overflow: "hidden",
});

const ChatHeader = styled(Box)(({ theme }) => ({
  padding: theme.spacing(1, 2),
  backgroundColor: theme.palette.primary.main,
  color: theme.palette.primary.contrastText,
  display: "flex",
  alignItems: "center",
}));

const UserList = styled(List)(({ theme }) => ({
  width: "40%",
  maxWidth: "200px",
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
  padding: theme.spacing(1),
  backgroundColor: theme.palette.grey[100],
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
  padding: theme.spacing(1),
  backgroundColor: theme.palette.background.paper,
  borderTop: `1px solid ${theme.palette.divider}`,
}));

// ---------------------------- 聊天室組件 -------------------------------------
const MessageBubble = ({ isCurrentUser, children }) => (
  <StyledMessageBubble isCurrentUser={isCurrentUser}>
    <Typography variant="body1">{children}</Typography>
  </StyledMessageBubble>
);

// ---------------------------- 聊天室本體 -------------------------------------

const ChatRoom = ({
  token,
  currentUserId,
  targetUserId: propTargetUserId,
  unreadCounts,
  setUnreadCounts,
  stompClient,
  setChatTargetUser,
}) => {
  const [inputMessage, setInputMessage] = useState(""); // 輸入的訊息
  const [targetUserId, setTargetUserId] = useState(null); // 目標用戶 ID
  const [userList, setUserList] = useState([]); // 用戶列表
  const [targetUserName, setTargetUserName] = useState(""); // 目標用戶名稱
  const [isComposing, setIsComposing] = useState(false); // 是否正在輸入
  const messageListRef = useRef(null); // 訊息列表的引用
  const targetUserIdRef = useRef(targetUserId); // 目標用戶 ID 的引用
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    if (propTargetUserId) {
      setTargetUserId(propTargetUserId);
    }
  }, [propTargetUserId]);

  useEffect(() => {
    targetUserIdRef.current = targetUserId; // 更新目標用戶 ID
  }, [targetUserId]);

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
        setUserList(filteredUsers); // 設置過去聊過天的使用者
      })
      .catch((error) => console.error("Error fetching chat partners:", error));
  }, [token, currentUserId]);

  const scrollToBottom = useCallback(() => {
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  }, []);

  useEffect(() => {
    if (stompClient) {
      const subscription = stompClient.subscribe(
        "/user/queue/message",
        (message) => {
          const newMessage = JSON.parse(message.body);
          const { senderId, receiverId } = newMessage;

          // 判斷是否是正在聊天的對象
          const isCurrentChatPartner =
            senderId === targetUserId || receiverId === targetUserId;

          if (isCurrentChatPartner) {
            // 如果是正在聊天的對象，將訊息標記為已讀並更新狀態
            // 標記訊息為已讀
            setMessages((prevMessages) => [...prevMessages, newMessage]);

            fetch("/api/chat/messages/read", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
              body: JSON.stringify({ partnerId: targetUserId }),
            }).catch((error) =>
              console.error("Error marking messages as read:", error)
            );
          }
        }
      );

      return () => {
        subscription.unsubscribe();
      };
    }
  }, [stompClient, targetUserId, setUnreadCounts, token]);

  useEffect(() => {
    if (targetUserId) {
      fetch(`/api/chat/messages?receiverId=${targetUserId}`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then((response) => response.json())
        .then((data) => {
          setMessages(Array.isArray(data) ? data : []);
          scrollToBottom();
        })
        .catch((error) =>
          console.error("Error fetching chat messages:", error)
        );
    }
  }, [targetUserId, token, scrollToBottom, setMessages]);

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
    if (!inputMessage.trim()) {
      return;
    }

    try {
      const chatMessage = {
        message: inputMessage.trim(),
        senderId: currentUserId,
        receiverId: targetUserId,
        timestamp: new Date().toISOString(),
        read: false,
      };

      // 更新訊息
      setMessages((prevMessages) => [...prevMessages, chatMessage]);

      // 原本是 send，後來官方有說下版就移除 method，這邊換成新的 client.publish method
      if (stompClient && stompClient.connected) {
        stompClient.publish({
          destination: "/app/message",
          body: JSON.stringify(chatMessage),
          headers: {},
        });
      } else {
        console.error("STOMP client is not connected or initialized.");
      }

      setInputMessage("");
      scrollToBottom();
    } catch (error) {
      console.error("Error sending message:", error);
    }
  }, [
    inputMessage,
    targetUserId,
    currentUserId,
    scrollToBottom,
    stompClient,
    setMessages,
  ]);

  const selectUser = useCallback(
    (userId) => {
      setTargetUserId(userId);
      setTargetUserName(userId);
      setChatTargetUser(userId);

      // 清除未讀計數
      setUnreadCounts((prevCounts) => ({
        ...prevCounts,
        [userId]: 0,
      }));

      // 標記訊息為已讀
      fetch("/api/chat/messages/read", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ partnerId: userId }),
      }).catch((error) =>
        console.error("Error marking messages as read:", error)
      );

      // 獲取聊天記錄
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
    [token, scrollToBottom, setUnreadCounts, setMessages, setChatTargetUser]
  );

  // ---------------------------- 渲染畫面 -------------------------------------

  return (
    <StyledPaper elevation={0}>
      <ChatContainer>
        <UserList subheader={<ListSubheader>聊天記錄</ListSubheader>}>
          {userList.map((user) => (
            <ListItemButton key={user} onClick={() => selectUser(user)}>
              <Avatar sx={{ mr: 2 }}>{user[0].toUpperCase()}</Avatar>
              <ListItemText primary={user} />
              {unreadCounts?.[user] > 0 && (
                <Box
                  sx={{
                    backgroundColor: "red",
                    color: "white",
                    borderRadius: "50%",
                    width: 20,
                    height: 20,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    marginLeft: 1,
                    fontSize: 12,
                  }}
                >
                  {unreadCounts[user]}
                </Box>
              )}
            </ListItemButton>
          ))}
        </UserList>

        <ChatArea>
          <ChatHeader>
            <Avatar sx={{ mr: 2 }}>
              {targetUserName && targetUserName.length > 0
                ? targetUserName[0].toUpperCase()
                : propTargetUserId && propTargetUserId.length > 0
                ? propTargetUserId[0].toUpperCase()
                : "?"}
            </Avatar>
            <Typography variant="h6" align="center">
              {targetUserName ? `${targetUserName}` : `${propTargetUserId}`}
            </Typography>
          </ChatHeader>
          <Divider />
          <MessageList ref={messageListRef}>
            {targetUserId &&
              Object.entries(
                groupMessagesByDate(
                  messages.filter(
                    (msg) =>
                      (msg.senderId === targetUserId &&
                        msg.receiverId === currentUserId) ||
                      (msg.senderId === currentUserId &&
                        msg.receiverId === targetUserId)
                  )
                )
              ).map(([date, msgs]) => (
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
              ))}
          </MessageList>
          <Divider />
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
              disabled={!targetUserId}
              sx={{ mr: 1 }}
            />
            <IconButton
              color="primary"
              onClick={sendMessage}
              disabled={!targetUserId || !inputMessage.trim()}
            >
              <SendIcon />
            </IconButton>
          </InputArea>
        </ChatArea>
      </ChatContainer>
    </StyledPaper>
  );
};

export default ChatRoom;
