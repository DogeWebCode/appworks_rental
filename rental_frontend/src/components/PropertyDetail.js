import React, { useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Layout, Typography, Button, Row, Col, Card, Modal } from "antd";
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
} from "@ant-design/icons";
import Lightbox from "yet-another-react-lightbox";
import Thumbnails from "yet-another-react-lightbox/plugins/thumbnails";
import Zoom from "yet-another-react-lightbox/plugins/zoom";
import "yet-another-react-lightbox/styles.css";
import "yet-another-react-lightbox/plugins/thumbnails.css";
import ChatRoom from "./ChatRoom";

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

const PropertyDetail = ({ token, currentUserId, setIsLoginModalVisible }) => {
  const { propertyId } = useParams();
  const [property, setProperty] = useState(null);
  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);
  const [photoIndex, setPhotoIndex] = useState(0);
  const [showChatModal, setShowChatModal] = useState(false);
  const navigate = useNavigate();

  React.useEffect(() => {
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

  const renderFacilities = useCallback(() => {
    return (
      <Row gutter={[16, 16]}>
        {facilities
          .filter((facility) =>
            property.facility.includes(facility.facilityName)
          )
          .map((facility) => (
            <Col key={facility.id} span={6}>
              <div style={{ textAlign: "center" }}>
                <img
                  src={facility.iconUrl}
                  alt={facility.facilityName}
                  style={{ width: 40, height: 40 }}
                />
                <Text style={{ display: "block", marginTop: 8, color: "#000" }}>
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
            <Col key={feature.id} span={6}>
              <div style={{ textAlign: "center" }}>
                <img
                  src={feature.iconUrl}
                  alt={feature.featureName}
                  style={{ width: 40, height: 40 }}
                />
                <Text style={{ display: "block", marginTop: 8, color: "#000" }}>
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
      setShowChatModal(true); // 有登入，跳聊天室
    } else {
      setIsLoginModalVisible(true); // 沒登入，跳登入表單
    }
  };

  return (
    <Layout>
      <Content style={{ padding: "0 50px", background: "#fff" }}>
        <Button
          icon={<LeftOutlined />}
          onClick={handleGoBack}
          style={{ margin: "20px 0" }}
        >
          返回
        </Button>
        <Row gutter={[32, 32]}>
          <Col xs={24} md={14}>
            <div
              style={{
                textAlign: "center",
                marginBottom: "24px",
                backgroundColor: "#f0f0f0", // 灰色背景
                padding: "20px",
                borderRadius: "8px",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <div
                style={{
                  width: "100%",
                  height: "400px",
                  backgroundImage: `url(${property.mainImage})`,
                  backgroundSize: "cover",
                  backgroundPosition: "center",
                  cursor: "pointer",
                  borderRadius: "8px",
                  marginBottom: "10px",
                }}
                onClick={() => setIsOpen(true)}
              />
              <Row
                gutter={[8, 8]}
                justify="center"
                style={{ maxWidth: "600px", width: "100%" }}
              >
                {images.slice(0, 5).map((image, index) => (
                  <Col span={4} key={index}>
                    <img
                      src={image.src}
                      alt={`Thumbnail ${index}`}
                      style={{
                        width: "100%",
                        height: "60px",
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
                        height: "60px",
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
          <Col span={8}>
            <Title level={3}>{property.title}</Title>
            <Text>
              <EnvironmentOutlined />{" "}
              {`${property.cityName}, ${property.districtName}, ${property.roadName}`}
              {property.address && `, ${property.address}`}
            </Text>
            <div style={{ marginTop: 16 }}>
              <Title
                level={3}
                style={{ display: "inline-block", marginRight: 16 }}
              >
                NT$ {property.price}/月
              </Title>
              <Text style={{ fontSize: 16 }}>押金：NT$ {property.deposit}</Text>
            </div>
            <div style={{ marginTop: 16 }}>
              <Text style={{ marginRight: 16 }}>
                <HomeOutlined /> 坪數: {property.area} 坪
              </Text>
              <Text style={{ marginRight: 16 }}>
                <TeamOutlined /> 房間數: {property.propertyLayout.roomCount}
              </Text>
              <Text style={{ marginRight: 16 }}>
                <CalendarOutlined /> 租期：{property.rent_period} 個月起
              </Text>
            </div>
            <div style={{ marginTop: 16 }}>
              <Text style={{ marginRight: 16 }}>
                <ApartmentOutlined /> 樓層: {property.floor} /{" "}
                {property.total_floor} 樓
              </Text>
              <Text style={{ marginRight: 16 }}>
                <AppstoreOutlined /> 類型: {property.propertyType}
              </Text>
            </div>
            <div style={{ marginTop: 16 }}>
              <Text>
                <DollarOutlined /> 管理費：
                {property.management_fee
                  ? `NT ${property.management_fee}/月`
                  : "無"}
              </Text>
            </div>

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
                    onClick={handleContactLandlord}
                  >
                    聯繫房東
                  </Button>
                </div>
              </div>
            </Card>
          </Col>
        </Row>

        <div style={{ marginTop: 24 }}>
          <Title level={4}>房源描述</Title>
          <Paragraph>{property.description}</Paragraph>
        </div>

        <div style={{ marginTop: 24 }}>
          <Title level={4}>設施</Title>
          {renderFacilities()}
        </div>

        <div style={{ marginTop: 24 }}>
          <Title level={4}>特色</Title>
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

        <Modal
          open={showChatModal}
          onCancel={() => setShowChatModal(false)}
          footer={null}
          width="70%"
        >
          <ChatRoom
            token={token}
            currentUserId={currentUserId}
            targetUserId={property.landlord_info.landlord_username}
          />
        </Modal>
      </Content>
    </Layout>
  );
};

export default PropertyDetail;
