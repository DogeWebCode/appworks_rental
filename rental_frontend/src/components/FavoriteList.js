import { useEffect, useState } from "react";
import { List, Button, message, Typography, Image, Descriptions } from "antd";
import { useNavigate } from "react-router-dom";
import "../FavoriteList.css";

const { Title } = Typography;

const FavoriteList = ({ token, setIsLoginModalVisible }) => {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      setIsLoginModalVisible(true);
      return;
    }
    setLoading(true);
    fetch("/api/favorite", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error("Failed to fetch favorites");
        }
      })
      .then((data) => {
        setFavorites(data.data);
      })
      .catch((error) => {
        console.error("Error fetching favorites:", error);
        message.error("無法載入收藏列表");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [token, setIsLoginModalVisible]);

  const handleRemoveFavorite = (propertyId) => {
    fetch(`/api/favorite/${propertyId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (response.ok) {
          setFavorites((prevFavorites) =>
            prevFavorites.filter((fav) => fav.propertyId !== propertyId)
          );
          message.success("已從收藏中移除");
        } else {
          throw new Error("Failed to remove favorite");
        }
      })
      .catch((error) => {
        console.error("Error removing favorite:", error);
        message.error("移除收藏時發生錯誤");
      });
  };

  const handlePropertyClick = (propertyId) => {
    navigate(`/property/${propertyId}`);
  };

  return (
    <div className="favorite-list-container">
      <Title level={2} style={{ fontFamily: "system-ui" }}>
        我的收藏
      </Title>
      <List
        loading={loading}
        itemLayout="vertical"
        dataSource={favorites}
        renderItem={(item) => (
          <List.Item
            className="favorite-item"
            actions={[
              <Button
                type="primary"
                danger
                onClick={() => handleRemoveFavorite(item.propertyId)}
                style={{ marginLeft: "20px" }}
              >
                移除
              </Button>,
            ]}
          >
            <div className="favorite-item-content">
              <Image
                width={180}
                height={130}
                src={item.mainImage}
                alt={item.title}
                style={{
                  borderRadius: "8px",
                  objectFit: "cover",
                }}
                onClick={() => handlePropertyClick(item.propertyId)}
              />

              <Descriptions
                title={item.title}
                layout="horizontal"
                bordered
                column={1}
                size="middle"
                style={{
                  marginLeft: "20px",
                  marginRight: "20px",
                  width: "100%",
                }}
              >
                <Descriptions.Item label="地址">
                  {`${item.cityName} ${item.districtName} ${item.roadName}`}
                </Descriptions.Item>
                <Descriptions.Item label="月租費">
                  NT$ {item.price}
                </Descriptions.Item>
                <Descriptions.Item label="房源類型">
                  {item.propertyType}
                </Descriptions.Item>
                <Descriptions.Item label="加入收藏日期">
                  {new Date(item.createdAt).toLocaleDateString()}
                </Descriptions.Item>
              </Descriptions>
            </div>
          </List.Item>
        )}
      />
    </div>
  );
};

export default FavoriteList;
