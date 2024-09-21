import React, { useState, useEffect, useCallback } from "react";
import {
  Layout,
  InputNumber,
  Input,
  Button,
  Card,
  Tag,
  Tooltip,
  Row,
  Col,
  Typography,
  message,
  Pagination,
  Avatar,
} from "antd";
import { SearchOutlined } from "@ant-design/icons";

const { Header, Content, Footer } = Layout;
const { Search } = Input;
const { Title, Text } = Typography;

const HomePage = () => {
  const [properties, setProperties] = useState([]);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchParams, setSearchParams] = useState({
    area: "",
    minPrice: null,
    maxPrice: null,
    propertyType: "all",
  });

  const fetchProperties = useCallback(async () => {
    try {
      const response = await fetch(`/api/property/search?page=${page}`);
      const data = await response.json();
      setProperties(data.data);
      setTotalElements(data.totalElements);
    } catch (error) {
      console.error("Error fetching properties:", error);
    }
  }, [page]);

  useEffect(() => {
    fetchProperties();
  }, [fetchProperties]);

  const handleSearch = () => {
    if (validateSearchParams()) {
      setPage(0);
      fetchProperties();
    }
  };

  const validateSearchParams = () => {
    if (
      searchParams.minPrice &&
      searchParams.maxPrice &&
      searchParams.minPrice > searchParams.maxPrice
    ) {
      message.error("最低價格不能高於最高價格");
      return false;
    }
    return true;
  };

  const handlePriceChange = (type, value) => {
    setSearchParams((prevParams) => ({
      ...prevParams,
      [type === "min" ? "minPrice" : "maxPrice"]: value,
    }));
  };

  const PropertyCard = ({ property }) => (
    <Card
      hoverable
      cover={
        <img
          alt={property.title}
          src={property.mainImage}
          style={{ height: 200, objectFit: "cover" }}
        />
      }
    >
      <Card.Meta
        title={
          <Tooltip title={property.title}>
            {property.title.length > 20
              ? `${property.title.substring(0, 20)}...`
              : property.title}
          </Tooltip>
        }
        description={
          <>
            <Text type="secondary">{`${property.cityName} ${property.districtName} ${property.roadName}`}</Text>
            <div style={{ marginTop: 8 }}>
              <Text
                strong
                style={{ fontSize: 18, color: "#fa8c16" }}
              >{`NT$${property.price}/月`}</Text>
              <Text type="secondary" style={{ marginLeft: 10 }}>
                {`${property.area}坪`}
              </Text>
              <Text type="secondary" style={{ marginLeft: 10 }}>
                {`位於${property.floor}樓`}
              </Text>
            </div>
            {property.propertyLayout ? (
              <div style={{ marginTop: 8, marginBottom: 8 }}>
                <Tag color="blue">{`${
                  property.propertyLayout.roomCount || 0
                }房`}</Tag>
                <Tag color="green">{`${
                  property.propertyLayout.bathroomCount || 0
                }衛`}</Tag>
                <Tag color="#FF521B">{`${
                  property.propertyLayout.livingRoomCount || 0
                }廳`}</Tag>
                <Tag color="#FC9E4F" style={{ marginBottom: 8 }}>
                  {`${property.propertyLayout.kitchenCount || 0}廚房`}
                </Tag>
                <Tag color="red">{`${
                  property.propertyLayout.balconyCount || 0
                }陽台`}</Tag>
              </div>
            ) : (
              <div>Layout info not available</div>
            )}
            <Tag color="orange">{property.propertyType}</Tag>
            <div style={{ marginTop: 8 }}>
              {property.features.slice(0, 3).map((feature, index) => (
                <Tag key={index} style={{ marginBottom: 4 }}>
                  {feature}
                </Tag>
              ))}
              {property.features.length > 3 && (
                <Tag style={{ marginBottom: 4 }}>
                  +{property.features.length - 3}
                </Tag>
              )}
            </div>
          </>
        }
      />
    </Card>
  );

  return (
    <Layout className="layout">
      <Header style={{ background: "#fff", padding: 0 }}>
        <div
          style={{
            maxWidth: 1200,
            margin: "0 auto",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <div style={{ display: "flex", alignItems: "center" }}>
            <Avatar
              src="/shiba-logo.png"
              size="large"
              style={{ marginRight: 8 }}
            />
            <Title level={3} style={{ margin: 0 }}>
              柴好租
            </Title>
          </div>
          <div>
            <Button type="link">個人資料</Button>
            <Button type="link">房源收藏夾</Button>
            <Button type="primary">登入</Button>
          </div>
        </div>
      </Header>

      <Content style={{ padding: "0", marginTop: 0 }}>
        {/* 大圖背景區塊 */}
        <div
          style={{
            position: "relative",
            height: "400px",
            backgroundImage: 'url("/path/to/your/image.jpg")', // 這裡替換成你的大圖路徑
            backgroundSize: "cover",
            backgroundPosition: "center",
          }}
        >
          {/* 搜尋表單懸浮在大圖上 */}
          <div
            style={{
              position: "absolute",
              top: "50%",
              left: "50%",
              transform: "translate(-50%, -50%)",
              backgroundColor: "rgba(255, 255, 255, 0.9)",
              padding: "20px",
              borderRadius: "10px",
              width: "80%",
              maxWidth: "600px",
            }}
          >
            <Title level={2}>尋找租屋</Title>
            <div style={{ marginTop: 24 }}>
              <Row gutter={16}>
                <Col span={8}>
                  <Search
                    placeholder="輸入縣市或地區"
                    onSearch={(value) =>
                      setSearchParams({ ...searchParams, area: value })
                    }
                    style={{ width: "100%" }}
                  />
                </Col>
                <Col span={5}>
                  <InputNumber
                    style={{ width: "100%" }}
                    placeholder="最低價格"
                    min={0}
                    step={1000}
                    onChange={(value) => handlePriceChange("min", value)}
                    formatter={(value) =>
                      `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                    }
                    parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                  />
                </Col>
                <Col span={5}>
                  <InputNumber
                    style={{ width: "100%" }}
                    placeholder="最高價格"
                    min={0}
                    step={1000}
                    onChange={(value) => handlePriceChange("max", value)}
                    formatter={(value) =>
                      `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                    }
                    parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                  />
                </Col>
                <Col span={6}>
                  <Button
                    type="primary"
                    icon={<SearchOutlined />}
                    style={{ width: "100%" }}
                    onClick={handleSearch}
                  >
                    搜尋房源
                  </Button>
                </Col>
              </Row>
            </div>
          </div>
        </div>

        {/* 房源顯示區域 */}
        <div
          style={{
            background: "#fff",
            padding: 24,
            maxWidth: 1300,
            margin: "0 auto",
          }}
        >
          <Row gutter={[16, 16]}>
            {properties.map((property) => (
              <Col xs={24} sm={12} md={8} lg={8} xl={6} key={property.id}>
                <PropertyCard property={property} />
              </Col>
            ))}
          </Row>
          <div style={{ marginTop: 24, textAlign: "center" }}>
            <Pagination
              current={page + 1}
              pageSize={12}
              total={totalElements}
              align="center"
              onChange={(newPage) => {
                setPage(newPage - 1);
                window.scrollTo({
                  top: 0,
                  behavior: "smooth",
                });
              }}
              showSizeChanger={false}
            />
          </div>
        </div>
      </Content>

      <Footer style={{ textAlign: "center" }}>
        柴好租 ©2024 Created by Shiba
      </Footer>
    </Layout>
  );
};

export default HomePage;
