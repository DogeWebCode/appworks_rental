import React from "react";
import { Card, Tag, Typography, Tooltip, Row, Col } from "antd";
import {
  HomeOutlined,
  EnvironmentOutlined,
  AreaChartOutlined,
} from "@ant-design/icons";

const { Text, Title } = Typography;

const PropertyCard = ({ property }) => {
  const features = property.features || [];
  const propertyLayout = property.propertyLayout || {
    roomCount: 0,
    livingRoomCount: 0,
    bathroomCount: 0,
    balconyCount: 0,
    kitchenCount: 0,
  };

  return (
    <Card
      hoverable
      cover={
        property.mainImage ? (
          <img
            alt={property.title}
            src={property.mainImage}
            style={{ height: 200, objectFit: "cover" }}
          />
        ) : (
          <div
            style={{
              height: 200,
              background: "#f0f0f0",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text type="secondary">沒有圖片可預覽</Text>
          </div>
        )
      }
    >
      <Card.Meta
        title={
          <Tooltip title={property.title}>
            <Title level={4} ellipsis={{ rows: 2 }}>
              {property.title || "未提供標題"}
            </Title>
          </Tooltip>
        }
        description={
          <>
            <Text type="secondary">
              <EnvironmentOutlined />{" "}
              {`${property.cityName || "未知城市"} ${
                property.districtName || "未知區域"
              } ${property.roadName || "未知道路"}`}
            </Text>
            <div style={{ marginTop: 16, marginBottom: 16 }}>
              <Text strong style={{ fontSize: 20, color: "#fa8c16" }}>{`NT$${
                property.price || "價格未提供"
              }/月`}</Text>
              <Text type="secondary" style={{ marginLeft: 16 }}>
                <AreaChartOutlined /> {`${property.area || 0}坪`}
              </Text>
              <Text type="secondary" style={{ marginLeft: 16 }}>
                <HomeOutlined /> {`${property.floor || "未知"}樓`}
              </Text>
            </div>
            <Row gutter={[8, 8]} style={{ marginBottom: 16 }}>
              <Col>
                <Tag color="blue">{`${propertyLayout.roomCount || 0}房`}</Tag>
              </Col>
              <Col>
                <Tag color="green">{`${
                  propertyLayout.bathroomCount || 0
                }衛`}</Tag>
              </Col>
              <Col>
                <Tag color="#FF521B">{`${
                  propertyLayout.livingRoomCount || 0
                }廳`}</Tag>
              </Col>
              <Col>
                <Tag color="#FC9E4F">{`${
                  propertyLayout.kitchenCount || 0
                }廚房`}</Tag>
              </Col>
              <Col>
                <Tag color="red">{`${
                  propertyLayout.balconyCount || 0
                }陽台`}</Tag>
              </Col>
            </Row>
            <Row gutter={[8, 8]} style={{ marginBottom: 16 }}>
              <Col>
                <Tag color="orange">{property.propertyType || "未提供"}</Tag>
              </Col>
              <Col>
                <Tag color="orange">{property.buildingType || "未提供"}</Tag>
              </Col>
            </Row>
            <Row gutter={[8, 8]}>
              {features.slice(0, 3).map((feature, index) => (
                <Col key={index}>
                  <Tag>{feature}</Tag>
                </Col>
              ))}
              {features.length > 3 && (
                <Col>
                  <Tag>+{features.length - 3}</Tag>
                </Col>
              )}
            </Row>
          </>
        }
      />
    </Card>
  );
};

export default PropertyCard;
