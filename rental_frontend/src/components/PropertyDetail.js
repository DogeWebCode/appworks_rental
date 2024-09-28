import { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Layout,
  Typography,
  Button,
  Row,
  Col,
  Card,
  message,
  Divider,
  Avatar,
  Space,
  Tag,
} from "antd";
import {
  EnvironmentOutlined,
  HomeOutlined,
  TeamOutlined,
  CalendarOutlined,
  UserOutlined,
  PhoneOutlined,
  MessageOutlined,
  DollarOutlined,
  AppstoreOutlined,
  ApartmentOutlined,
  LeftOutlined,
  HeartOutlined,
  HeartFilled,
} from "@ant-design/icons";
import Lightbox from "yet-another-react-lightbox";
import Thumbnails from "yet-another-react-lightbox/plugins/thumbnails";
import Zoom from "yet-another-react-lightbox/plugins/zoom";
import "yet-another-react-lightbox/styles.css";
import "yet-another-react-lightbox/plugins/thumbnails.css";
import { GoogleMap, useJsApiLoader, MarkerF } from "@react-google-maps/api";

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;
const libraries = ["places"];
const GOOGLE_MAPS_API_KEY = "AIzaSyANaVByjlclPg6DmowQQOP9k9fSo76tMIQ";

const PropertyDetail = ({ token, setIsLoginModalVisible, showChat }) => {
  const { propertyId } = useParams();
  const [property, setProperty] = useState(null);
  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);
  const [photoIndex, setPhotoIndex] = useState(0);
  const [isFavorite, setIsFavorite] = useState(false);
  const [places, setPlaces] = useState([]);
  const navigate = useNavigate();

  const { isLoaded, loadError } = useJsApiLoader({
    googleMapsApiKey: GOOGLE_MAPS_API_KEY,
    libraries,
  });

  const [map, setMap] = useState(null);

  const onMapLoad = useCallback((map) => {
    setMap(map);
  }, []);

  useEffect(() => {
    const fetchPropertyDetail = async () => {
      try {
        const [propertyRes, facilityRes, featureRes] = await Promise.all([
          fetch(`/api/property/detail/${propertyId}`),
          fetch(`/api/facility`),
          fetch(`/api/feature`),
        ]);

        const propertyData = await propertyRes.json();
        const facilityData = await facilityRes.json();
        const featureData = await featureRes.json();

        setProperty(propertyData);
        setFacilities(facilityData.data);
        setFeatures(featureData.data);
      } catch (error) {
        console.error("Error fetching property details:", error);
        message.error("無法加載房源詳情");
      } finally {
        setLoading(false);
      }
    };

    fetchPropertyDetail();
  }, [propertyId]);

  useEffect(() => {
    if (isLoaded && map && property) {
      const fetchNearbyPlaces = () => {
        const service = new window.google.maps.places.PlacesService(map);
        const location = new window.google.maps.LatLng(
          property.latitude,
          property.longitude
        );

        const request = {
          location: location,
          radius: 500,
          type: ["store", "restaurant", "school"],
        };

        service.nearbySearch(request, (results, status) => {
          if (status === window.google.maps.places.PlacesServiceStatus.OK) {
            setPlaces(results);
          }
        });
      };

      fetchNearbyPlaces();
    }
  }, [isLoaded, map, property]);

  useEffect(() => {
    const recordViewAction = async () => {
      if (!token) return; // 確保已登入

      try {
        const response = await fetch(`/api/user-action/${propertyId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ actionType: "view" }),
        });

        if (!response.ok) {
          throw new Error("紀錄瀏覽動作失敗");
        }
      } catch (error) {
        console.error("Error recording view action:", error);
      }
    };

    recordViewAction();
  }, [propertyId, token]);

  useEffect(() => {
    if (!token) return;
    // 檢查該房源是否已經被用戶收藏
    const fetchFavorites = async () => {
      try {
        const response = await fetch("/api/favorite", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const data = await response.json();

        // 檢查當前的 propertyId 是否在收藏清單中
        if (
          data.data &&
          data.data.some(
            (favorite) => favorite.propertyId === Number(propertyId)
          )
        ) {
          setIsFavorite(true); // 已收藏
        } else {
          setIsFavorite(false); // 未收藏
        }
      } catch (error) {
        console.error("Error fetching favorites:", error);
        message.error("無法檢查收藏狀態");
      }
    };

    fetchFavorites();
  }, [propertyId, token]);

  const renderFacilities = useCallback(() => {
    return (
      <Space size={[8, 16]} wrap>
        {facilities
          .filter((facility) =>
            property.facility.includes(facility.facilityName)
          )
          .map((facility) => (
            <Tag
              key={facility.id}
              color="blue"
              style={{
                padding: "8px 12px",
                borderRadius: "16px",
                fontSize: "16px",
                display: "flex",
                alignItems: "center",
                marginBottom: "8px",
              }}
            >
              <img
                src={facility.iconUrl}
                alt={facility.facilityName}
                style={{
                  width: 24,
                  height: 24,
                  marginRight: 8,
                }}
              />
              {facility.facilityName}
            </Tag>
          ))}
      </Space>
    );
  }, [facilities, property]);

  const renderFeatures = useCallback(() => {
    return (
      <Space size={[8, 16]} wrap>
        {features
          .filter((feature) => property.features.includes(feature.featureName))
          .map((feature) => (
            <Tag
              key={feature.id}
              color="green"
              style={{
                padding: "8px 12px",
                borderRadius: "16px",
                fontSize: "16px",
                display: "flex",
                alignItems: "center",
                marginBottom: "8px",
              }}
            >
              <img
                src={feature.iconUrl}
                alt={feature.featureName}
                style={{
                  width: 24,
                  height: 24,
                  marginRight: 8,
                }}
              />
              {feature.featureName}
            </Tag>
          ))}
      </Space>
    );
  }, [features, property]);

  if (loading || !isLoaded) {
    return (
      <div style={{ textAlign: "center", padding: "50px" }}>加載中...</div>
    );
  }

  if (loadError) {
    return <div>地圖加載錯誤，請稍後再試。</div>;
  }

  if (!property) {
    return <div>讀取房源失敗。</div>;
  }

  const images = [
    { src: property.mainImage },
    ...property.images.map((image) => ({ src: image })),
  ];

  const handleGoBack = () => {
    navigate(-1); // 返回上一頁
  };

  const handleContactLandlord = () => {
    if (token) {
      showChat(property.landlord_info.landlord_username); // 有登入，跳聊天室
    } else {
      setIsLoginModalVisible(true); // 沒登入，跳登入表單
    }
  };

  const handleFavoriteClick = () => {
    if (token) {
      handleFavorite();
    } else {
      setIsLoginModalVisible(true);
    }
  };

  const handleFavorite = async () => {
    try {
      const method = isFavorite ? "DELETE" : "POST"; // 已收藏則刪除，否則新增
      const response = await fetch(`/api/favorite/${propertyId}`, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("操作失敗");
      }

      setIsFavorite(!isFavorite); // 切換收藏狀態
      message.success(isFavorite ? "已取消收藏" : "成功加入收藏");

      // 記錄或移除 "favorite" 操作
      if (!isFavorite) {
        // 新增收藏的情況下，記錄 "favorite" 操作
        await fetch(`/api/user-action/${propertyId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ actionType: "favorite" }),
        });
      } else {
        // 取消收藏時，移除 "favorite" 操作
        await fetch(`/api/user-action/${propertyId}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
      }
    } catch (error) {
      console.error("Error updating favorite status:", error);
      message.error("操作失敗，請稍後再試");
    }
  };

  const formatDescription = (description) => {
    if (!description) {
      return [];
    }

    // 將文字按照句號或問號分割
    const sentences = description.split(/(?<=[。？])/);

    // 將句子組合成段落，每3-5個句子一個段落
    const paragraphs = [];
    for (let i = 0; i < sentences.length; i += 4) {
      paragraphs.push(sentences.slice(i, i + 4).join(""));
    }

    return paragraphs;
  };

  return (
    <Layout style={{ background: "#f0f2f5" }}>
      <Content
        style={{
          padding: "20px 20px",
          maxWidth: "1450px",
          margin: "5px auto",
        }}
      >
        <Button
          icon={<LeftOutlined />}
          onClick={handleGoBack}
          style={{ marginBottom: "24px" }}
        >
          返回
        </Button>

        <Card
          style={{
            marginBottom: 24,
            borderRadius: "16px",
            boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
          }}
        >
          <Row gutter={24} align="middle" justify="space-between">
            <Col xs={24} md={16}>
              <Title level={2} style={{ marginBottom: 16 }}>
                {property.title}
              </Title>
              <Text
                style={{
                  fontSize: "18px",
                  display: "block",
                  marginBottom: "20px",
                  color: "#8c8c8c",
                }}
              >
                <EnvironmentOutlined />{" "}
                {`${property.cityName}, ${property.districtName}, ${property.roadName}`}
                {property.address && `, ${property.address}`}
              </Text>
            </Col>
            <Col xs={24} md={8} style={{ textAlign: "right" }}>
              <Title level={2} style={{ color: "#FFA500", marginBottom: 8 }}>
                NT$ {property.price}/月
              </Title>
              <Text
                style={{ fontSize: 18, display: "block", color: "#8c8c8c" }}
              >
                押金：
                {property.deposit ? `NT$ ${property.deposit}` : "押金面敘"}
              </Text>
            </Col>
          </Row>
          <Button
            type="text"
            icon={
              isFavorite ? (
                <HeartFilled style={{ color: "#ff4d4f" }} />
              ) : (
                <HeartOutlined />
              )
            }
            onClick={handleFavoriteClick}
            style={{
              position: "absolute",
              right: 16,
              top: 16,
              fontSize: "18px",
              fontWeight: "bold",
            }}
          >
            {isFavorite ? "已收藏" : "收藏"}
          </Button>
        </Card>

        <Row gutter={[32, 32]}>
          <Col xs={24} lg={16}>
            <Card
              style={{
                borderRadius: "16px",
                overflow: "hidden",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <div
                style={{
                  width: "100%",
                  paddingTop: "66.67%",
                  backgroundImage: `url(${property.mainImage})`,
                  backgroundSize: "cover",
                  backgroundPosition: "center",
                  cursor: "pointer",
                  borderRadius: "12px",
                  marginBottom: "20px",
                }}
                onClick={() => setIsOpen(true)}
              />
              <Row gutter={[12, 12]} justify="start">
                {images.slice(0, 5).map((image, index) => (
                  <Col span={4} key={index}>
                    <img
                      src={image.src}
                      alt={`Thumbnail ${index}`}
                      style={{
                        width: "100%",
                        aspectRatio: "1 / 1",
                        objectFit: "cover",
                        cursor: "pointer",
                        borderRadius: "8px",
                        transition: "transform 0.3s ease",
                      }}
                      onClick={() => {
                        setPhotoIndex(index);
                        setIsOpen(true);
                      }}
                      onMouseEnter={(e) =>
                        (e.currentTarget.style.transform = "scale(1.05)")
                      }
                      onMouseLeave={(e) =>
                        (e.currentTarget.style.transform = "scale(1)")
                      }
                    />
                  </Col>
                ))}
                {images.length > 5 && (
                  <Col span={4}>
                    <Button
                      style={{
                        width: "100%",
                        height: "100%",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        borderRadius: "8px",
                      }}
                      onClick={() => setIsOpen(true)}
                    >
                      +{images.length - 5}
                    </Button>
                  </Col>
                )}
              </Row>
            </Card>

            <Card
              style={{
                marginTop: 24,
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Title level={3}>房源描述</Title>
              {formatDescription(property.description).map(
                (paragraph, index) => (
                  <Paragraph
                    key={index}
                    style={{
                      fontSize: "16px",
                      lineHeight: "1.8",
                      marginBottom: "16px",
                      textAlign: "justify",
                    }}
                  >
                    {paragraph}
                  </Paragraph>
                )
              )}
            </Card>
            <Card
              style={{
                marginTop: 24,
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Title level={3}>房源位置</Title>
              <GoogleMap
                mapContainerStyle={{ width: "100%", height: "400px" }}
                center={{ lat: property.latitude, lng: property.longitude }}
                zoom={15}
                onLoad={onMapLoad}
              >
                <MarkerF
                  position={{
                    lat: property.latitude,
                    lng: property.longitude,
                  }}
                />
                {places.map((place, index) => (
                  <MarkerF
                    key={index}
                    position={{
                      lat: place.geometry.location.lat(),
                      lng: place.geometry.location.lng(),
                    }}
                    icon={{
                      url: place.icon,
                      scaledSize: new window.google.maps.Size(20, 20),
                    }}
                  />
                ))}
              </GoogleMap>
              <div style={{ marginTop: "20px" }}>
                <Title level={4}>周邊設施</Title>
                <Space wrap>
                  {places.map((place, index) => (
                    <Tag key={index} color="blue">
                      {place.name}
                    </Tag>
                  ))}
                </Space>
              </div>
            </Card>
          </Col>
          <Col xs={24} lg={8}>
            <Card
              style={{
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <HomeOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    坪數: {property.area} 坪
                  </Text>
                </Col>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <TeamOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    房間數: {property.propertyLayout.roomCount}
                  </Text>
                </Col>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <CalendarOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    租期：{property.rent_period} 個月起
                  </Text>
                </Col>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <ApartmentOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    樓層: {property.floor} / {property.total_floor} 樓
                  </Text>
                </Col>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <AppstoreOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    房型: {property.propertyType}
                  </Text>
                </Col>
                <Col span={12}>
                  <Text style={{ fontSize: "16px", color: "#262626" }}>
                    <DollarOutlined
                      style={{ marginRight: 8, color: "#1890ff" }}
                    />{" "}
                    管理費：
                    {property.management_fee
                      ? `NT$ ${property.management_fee}/月`
                      : "無"}
                  </Text>
                </Col>
              </Row>
            </Card>

            <Card
              style={{
                marginTop: 24,
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  marginBottom: 16,
                }}
              >
                <Avatar
                  size={64}
                  icon={<UserOutlined />}
                  style={{ backgroundColor: "#1890ff" }}
                />
                <div style={{ marginLeft: 16 }}>
                  <Title level={4} style={{ margin: 0 }}>
                    {property.landlord_info.landlord_username}
                  </Title>
                  <Text type="secondary">房東</Text>
                </div>
              </div>
              <Divider style={{ margin: "16px 0" }} />
              <div style={{ marginBottom: 16 }}>
                <Text style={{ fontSize: "16px" }}>
                  <PhoneOutlined style={{ marginRight: 8, color: "#1890ff" }} />
                  {property.landlord_info.landlord_mobile_phone}
                </Text>
              </div>
              <Button
                type="primary"
                icon={<MessageOutlined />}
                style={{ width: "100%", height: "40px", fontSize: "16px" }}
                onClick={handleContactLandlord}
              >
                聯繫房東
              </Button>
            </Card>

            <Card
              style={{
                marginTop: 24,
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Title level={4}>傢俱與設施</Title>
              {renderFacilities()}
            </Card>

            <Card
              style={{
                marginTop: 24,
                borderRadius: "16px",
                boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Title level={4}>特色</Title>
              {renderFeatures()}
            </Card>
          </Col>
        </Row>

        <Lightbox
          open={isOpen}
          close={() => setIsOpen(false)}
          index={photoIndex}
          slides={images}
          plugins={[Thumbnails, Zoom]}
          thumbnails={{
            position: "bottom",
            width: 120,
            height: 80,
            gap: 2,
            padding: 4,
            borderRadius: 4,
          }}
          zoom={{
            maxZoomPixelRatio: 3,
            scrollToZoom: true,
          }}
        />
      </Content>
    </Layout>
  );
};

export default PropertyDetail;
