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

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

const PropertyDetail = ({ token, setIsLoginModalVisible, showChat }) => {
  const { propertyId } = useParams();
  const [property, setProperty] = useState(null);
  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);
  const [photoIndex, setPhotoIndex] = useState(0);
  const [isFavorite, setIsFavorite] = useState(false);
  const navigate = useNavigate();

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
      } finally {
        setLoading(false);
      }
    };

    fetchPropertyDetail();
  }, [propertyId]);

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
      <Row gutter={[16, 16]}>
        {facilities
          .filter((facility) =>
            property.facility.includes(facility.facilityName)
          )
          .map((facility) => (
            <Col key={facility.id} xs={24} sm={12} md={8} lg={4}>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  padding: "2px",
                  height: "100%",
                }}
              >
                <img
                  src={facility.iconUrl}
                  alt={facility.featureName}
                  style={{
                    width: 32,
                    height: 32,
                    marginRight: 12,
                    flexShrink: 0,
                  }}
                />
                <Text
                  style={{
                    marginLeft: "4px",
                    color: "#000",
                    fontSize: "16px",
                  }}
                >
                  {facility.facilityName}
                </Text>
              </div>
            </Col>
          ))}
      </Row>
    );
  }, [facilities, property]);

  const renderFeatures = useCallback(() => {
    return (
      <Row gutter={[16, 16]}>
        {features
          .filter((feature) => property.features.includes(feature.featureName))
          .map((feature) => (
            <Col key={feature.id} xs={24} sm={12} md={8} lg={4}>
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  padding: "2px",
                  height: "100%",
                }}
              >
                <img
                  src={feature.iconUrl}
                  alt={feature.featureName}
                  style={{
                    width: 32,
                    height: 32,
                    marginRight: 12,
                    flexShrink: 0,
                  }}
                />
                <Text
                  style={{
                    color: "#000",
                    fontSize: "16px",
                    lineHeight: 1.4,
                  }}
                >
                  {feature.featureName}
                </Text>
              </div>
            </Col>
          ))}
      </Row>
    );
  }, [features, property]);

  if (loading) {
    return (
      <div style={{ textAlign: "center", padding: "50px" }}>加載中...</div>
    );
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
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error("操作失敗");
      }

      setIsFavorite(!isFavorite); // 切換收藏狀態
      message.success(isFavorite ? "已取消收藏" : "成功加入收藏");
    } catch (error) {
      console.error("Error updating favorite status:", error);
      message.error("操作失敗，請稍後再試");
    }
  };

  const formatDescription = (description) => {
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
    <Layout>
      <Content
        style={{
          padding: "30px 50px",
          background: "#fff",
          maxWidth: "1300px",
          margin: "20px auto",
        }}
      >
        <Button
          icon={<LeftOutlined />}
          onClick={handleGoBack}
          style={{ marginBottom: "20px" }}
        >
          返回
        </Button>
        <Row gutter={[32, 32]}>
          <Col xs={24} lg={12}>
            <div
              style={{
                backgroundColor: "#f0f0f0",
                padding: "20px",
                borderRadius: "8px",
                height: "100%",
              }}
            >
              <div
                style={{
                  width: "100%",
                  marginTop: "100px",
                  paddingTop: "66.67%", // 3:2 aspect ratio
                  backgroundImage: `url(${property.mainImage})`,
                  backgroundSize: "cover",
                  backgroundPosition: "center",
                  cursor: "pointer",
                  borderRadius: "8px",
                  marginBottom: "50px",
                }}
                onClick={() => setIsOpen(true)}
              />
              <Row gutter={[8, 8]} justify="center">
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
                        borderRadius: "4px",
                      }}
                      onClick={() => {
                        setPhotoIndex(index);
                        setIsOpen(true);
                      }}
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
                      }}
                      onClick={() => setIsOpen(true)}
                    >
                      +{images.length - 5}
                    </Button>
                  </Col>
                )}
              </Row>
            </div>
          </Col>
          <Col xs={24} lg={12}>
            <Card
              style={{
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
              }}
            >
              <Button
                type="text"
                icon={
                  isFavorite ? (
                    <HeartFilled style={{ color: "red" }} />
                  ) : (
                    <HeartOutlined />
                  )
                }
                onClick={handleFavoriteClick}
              >
                {isFavorite ? "已加入收藏夾" : "加入收藏夾"}
              </Button>
              <Title level={3}>{property.title}</Title>
              <Text style={{ fontSize: "18px" }}>
                <EnvironmentOutlined />{" "}
                {`${property.cityName}, ${property.districtName}, ${property.roadName}`}
                {property.address && `, ${property.address}`}
              </Text>
              <div style={{ marginTop: 16 }}>
                <Title level={2} style={{ marginBottom: 0 }}>
                  NT$ {property.price}/月
                </Title>
                <Text style={{ fontSize: 18 }}>
                  押金：NT$ {property.deposit}
                </Text>
              </div>
              <div style={{ marginTop: 16 }}>
                <Text
                  style={{
                    display: "block",
                    marginBottom: 8,
                    fontSize: "16px",
                  }}
                >
                  <HomeOutlined /> 坪數: {property.area} 坪
                </Text>
                <Text
                  style={{
                    display: "block",
                    marginBottom: 8,
                    fontSize: "16px",
                  }}
                >
                  <TeamOutlined /> 房間數: {property.propertyLayout.roomCount}
                </Text>
                <Text
                  style={{
                    display: "block",
                    marginBottom: 8,
                    fontSize: "16px",
                  }}
                >
                  <CalendarOutlined /> 租期：{property.rent_period} 個月起
                </Text>
                <Text
                  style={{
                    display: "block",
                    marginBottom: 8,
                    fontSize: "16px",
                  }}
                >
                  <ApartmentOutlined /> 樓層: {property.floor} /{" "}
                  {property.total_floor} 樓
                </Text>
                <Text
                  style={{
                    display: "block",
                    marginBottom: 8,
                    fontSize: "16px",
                  }}
                >
                  <AppstoreOutlined /> 房型: {property.propertyType}
                </Text>
                <Text style={{ display: "block", fontSize: "16px" }}>
                  <DollarOutlined /> 管理費：
                  {property.management_fee
                    ? `NT$ ${property.management_fee}/月`
                    : "無"}
                </Text>
              </div>
            </Card>

            <Card
              style={{
                marginTop: 24,
                padding: 16,
                borderRadius: 8,
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
              }}
            >
              <div style={{ textAlign: "center" }}>
                <UserOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <Title level={4}>
                  {property.landlord_info.landlord_username}
                </Title>
                <Text>
                  <PhoneOutlined />{" "}
                  {property.landlord_info.landlord_mobile_phone}
                </Text>
                <div style={{ marginTop: 16 }}>
                  <Button
                    type="primary"
                    icon={<MessageOutlined />}
                    style={{ fontFamily: "system-ui" }}
                    onClick={handleContactLandlord}
                  >
                    聯繫房東
                  </Button>
                </div>
              </div>
            </Card>
          </Col>
        </Row>
        <Divider style={{ borderWidth: "6px" }}></Divider>

        <div style={{ marginTop: 24 }}>
          <Title level={3}>房源描述</Title>
          {formatDescription(property.description).map((paragraph, index) => (
            <Paragraph
              key={index}
              strong={true}
              style={{
                fontSize: "18px",
                lineHeight: "1.6",
                marginBottom: "16px",
                textAlign: "justify",
              }}
            >
              {paragraph}
            </Paragraph>
          ))}
        </div>

        <Divider style={{ borderWidth: "6px" }}></Divider>

        <div style={{ marginTop: 24 }}>
          <Title level={3}>設施與傢俱</Title>
          {renderFacilities()}
        </div>

        <Divider style={{ borderWidth: "6px" }}></Divider>

        <div style={{ marginTop: 24 }}>
          <Title level={3}>特色</Title>
          {renderFeatures()}
        </div>

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
