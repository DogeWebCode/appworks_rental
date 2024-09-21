import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Layout, Spin, Typography, Button, Row, Col } from "antd";

const { Content } = Layout;
const { Title, Text } = Typography;

const PropertyDetail = () => {
  const { propertyId } = useParams(); // 從路由參數獲取 propertyId
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPropertyDetail = async () => {
      try {
        const response = await fetch(`/api/property/detail/${propertyId}`);
        const data = await response.json();
        setProperty(data);
      } catch (error) {
        console.error("Error fetching property details:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchPropertyDetail();
  }, [propertyId]);

  if (loading) {
    return (
      <div style={{ textAlign: "center", padding: "50px" }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!property) {
    return <div>房源信息加載失敗。</div>;
  }

  return (
    <Layout>
      <Content style={{ padding: "0 50px" }}>
        <div style={{ background: "#fff", padding: 24, minHeight: 380 }}>
          <Row gutter={[16, 16]}>
            <Col span={12}>
              <img
                src={property.mainImage}
                alt={property.title}
                style={{ width: "100%", height: "auto" }}
              />
            </Col>
            <Col span={12}>
              <Title>{property.title}</Title>
              <Text>{`${property.cityName}, ${property.districtName}, ${property.roadName}`}</Text>
              <div style={{ marginTop: 16 }}>
                <Text strong>NT$ {property.price}/月</Text>
                <Text style={{ marginLeft: 16 }}>坪數: {property.area}</Text>
              </div>
              <div style={{ marginTop: 16 }}>
                <Text>房間數: {property.propertyLayout.roomCount}</Text>
                <Text style={{ marginLeft: 16 }}>
                  衛浴數: {property.propertyLayout.bathroomCount}
                </Text>
              </div>
              <Button
                type="primary"
                style={{ marginTop: 20 }}
                onClick={() => console.log("開始聊天")}
              >
                聯繫房東
              </Button>
            </Col>
          </Row>
        </div>
      </Content>
    </Layout>
  );
};

export default PropertyDetail;
