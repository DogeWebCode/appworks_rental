import { useState, useEffect } from "react";
import {
  Empty,
  InputNumber,
  Button,
  Card,
  Tag,
  Tooltip,
  Row,
  Col,
  Typography,
  Pagination,
  Select,
  Space,
} from "antd";
import { ReloadOutlined, SmileOutlined, HomeOutlined } from "@ant-design/icons";
import MainLayout from "./layout/MainLayout";
import { useNavigate, useSearchParams } from "react-router-dom";

const { Option } = Select;
const { Title, Text } = Typography;

const HomePage = ({ token, setIsLoginModalVisible }) => {
  const [properties, setProperties] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [roads, setRoads] = useState([]);
  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);

  const [searchParams, setSearchParams] = useSearchParams();

  const isRecommendationFromParams =
    searchParams.get("isRecommendation") === "true";
  const [isRecommendation, setIsRecommendation] = useState(
    isRecommendationFromParams
  );

  const handleSort = (newSortBy) => {
    let newSortDirection = sortDirection;
    if (newSortBy === sortBy) {
      // 如果點擊同一個排序選項，切換排序方向
      newSortDirection = sortDirection === "asc" ? "desc" : "asc";
    } else {
      // 如果是新的排序選項，默認為升序
      newSortDirection = "asc";
    }

    setSortBy(newSortBy);
    setSortDirection(newSortDirection);
    searchParams.set("sortBy", newSortBy);
    searchParams.set("sortDirection", newSortDirection);
    searchParams.set("page", 0);
    setSearchParams(searchParams);
  };

  const [selectedCity, setSelectedCity] = useState(
    () => searchParams.get("city") || null
  );
  const [selectedDistrict, setSelectedDistrict] = useState(
    () => searchParams.get("district") || null
  );
  const [selectedRoad, setSelectedRoad] = useState(
    () => searchParams.get("road") || null
  );
  const [minPrice, setMinPrice] = useState(() =>
    searchParams.get("minPrice") ? parseInt(searchParams.get("minPrice")) : null
  );
  const [maxPrice, setMaxPrice] = useState(() =>
    searchParams.get("maxPrice") ? parseInt(searchParams.get("maxPrice")) : null
  );
  const [selectedFacilities, setSelectedFacilities] = useState(() =>
    searchParams.get("facility") ? searchParams.get("facility").split(",") : []
  );
  const [selectedFeatures, setSelectedFeatures] = useState(() =>
    searchParams.get("feature") ? searchParams.get("feature").split(",") : []
  );
  const [page, setPage] = useState(() =>
    searchParams.get("page") ? parseInt(searchParams.get("page")) : 0
  );
  const [sortBy, setSortBy] = useState(
    () => searchParams.get("sortBy") || "createdAt"
  );
  const [sortDirection, setSortDirection] = useState(
    () => searchParams.get("sortDirection") || "desc"
  );

  useEffect(() => {
    const fetchProperties = async () => {
      try {
        const queryParams = new URLSearchParams(searchParams);
        if (isRecommendation) {
          const response = await fetch("/api/property/recommendation", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          const data = await response.json();
          setProperties(data.data);
          setTotalElements(data.totalElements);
        } else {
          queryParams.delete("sortBy"); // 先刪除之前的值
          queryParams.append("sortBy", sortBy);
          queryParams.delete("sortDirection"); // 先刪除之前的值
          queryParams.append("sortDirection", sortDirection);
          const response = await fetch(
            `/api/property/search?${queryParams.toString()}`
          );
          const data = await response.json();
          setProperties(data.data);
          setTotalElements(data.totalElements);
        }
      } catch (error) {
        console.error("Error fetching properties:", error);
      }
    };

    fetchProperties();
  }, [searchParams, isRecommendation, token, sortBy, sortDirection]);

  // 把縣市讀進來
  useEffect(() => {
    fetch("/api/geo/city")
      .then((res) => res.json())
      .then((data) => {
        setCities(data.data);
      })
      .catch((err) => console.error(err));
  }, []);

  // 當選擇縣市後讀相對應的區域
  useEffect(() => {
    if (selectedCity) {
      fetch(`/api/geo/district?cityName=${selectedCity}`)
        .then((res) => res.json())
        .then((data) => {
          setDistricts(data.data);
          const districtFromParams = searchParams.get("district");
          setSelectedDistrict(districtFromParams || null);
        })
        .catch((err) => console.error(err));
    } else {
      setDistricts([]);
      setSelectedDistrict(null);
    }
  }, [selectedCity, searchParams]);

  // 當選擇區域後讀相對應的道路
  useEffect(() => {
    if (selectedDistrict) {
      fetch(
        `/api/geo/road?districtName=${selectedDistrict}&cityName=${selectedCity}`
      )
        .then((res) => res.json())
        .then((data) => {
          setRoads(data.data || []);
          const roadFromParams = searchParams.get("road");
          setSelectedRoad(roadFromParams || null);
        })
        .catch((err) => console.error(err));
    } else {
      setRoads([]);
      setSelectedRoad(null);
    }
  }, [selectedDistrict, selectedCity, searchParams]);

  // 加載設備數據
  useEffect(() => {
    fetch("/api/facility")
      .then((res) => res.json())
      .then((data) => {
        setFacilities(data.data); // 設置設備
      })
      .catch((err) => console.error(err));

    fetch("/api/feature")
      .then((res) => res.json())
      .then((data) => {
        setFeatures(data.data); // 設置特色
      })
      .catch((err) => console.error(err));
  }, []);

  const handleCityChange = (value) => {
    setSelectedCity(value);
    setSelectedDistrict(null);
    setSelectedRoad(null);

    if (value) {
      searchParams.set("city", value);
    } else {
      searchParams.delete("city");
    }
    searchParams.delete("district");
    searchParams.delete("road");
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleDistrictChange = (value) => {
    setSelectedDistrict(value);
    setSelectedRoad(null);

    if (value) {
      searchParams.set("district", value);
    } else {
      searchParams.delete("district");
    }
    searchParams.delete("road");
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleRoadChange = (value) => {
    setSelectedRoad(value);

    if (value) {
      searchParams.set("road", value);
    } else {
      searchParams.delete("road");
    }
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleMinPriceChange = (value) => {
    setMinPrice(value);

    if (value !== null && value !== undefined) {
      searchParams.set("minPrice", value);
    } else {
      searchParams.delete("minPrice");
    }
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleMaxPriceChange = (value) => {
    setMaxPrice(value);

    if (value !== null && value !== undefined) {
      searchParams.set("maxPrice", value);
    } else {
      searchParams.delete("maxPrice");
    }
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleFacilitiesChange = (value) => {
    setSelectedFacilities(value);

    if (value.length > 0) {
      searchParams.set("facility", value.join(","));
    } else {
      searchParams.delete("facility");
    }
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleFeaturesChange = (value) => {
    setSelectedFeatures(value);

    if (value.length > 0) {
      searchParams.set("feature", value.join(","));
    } else {
      searchParams.delete("feature");
    }
    searchParams.set("page", 0); // 重置頁數
    setSearchParams(searchParams);
  };

  const handleRecommendation = async () => {
    if (!token) {
      // 如果未登入，彈出登入表單
      setIsLoginModalVisible(true);
      return;
    }

    try {
      const response = await fetch("/api/property/recommendation", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        // 更新房源列表為推薦結果
        setProperties(data.data);
        setTotalElements(data.totalElements);
        setIsRecommendation(true);
        searchParams.set("isRecommendation", "true");
        setSearchParams(searchParams);
        // 滾動到頂部
        window.scrollTo({
          top: 0,
          behavior: "smooth",
        });
      } else {
        console.error("Failed to fetch recommendations");
      }
    } catch (error) {
      console.error("Error fetching recommendations:", error);
    }
  };

  const handleClearFilters = () => {
    setSelectedCity(null);
    setSelectedDistrict(null);
    setSelectedRoad(null);
    setMinPrice(null);
    setMaxPrice(null);
    setSelectedFacilities([]);
    setSelectedFeatures([]);
    setPage(0);
    setSearchParams({});

    setIsRecommendation(false);
    searchParams.delete("isRecommendation");
  };

  const PropertyCard = ({ property }) => {
    const navigate = useNavigate();

    const handleCardClick = () => {
      navigate(`/property/${property.id}`);
    };

    return (
      <Card
        hoverable
        cover={
          <img
            alt={property.title}
            src={property.mainImage}
            style={{ height: 200, objectFit: "cover" }}
          />
        }
        onClick={handleCardClick}
      >
        <Card.Meta
          title={
            <Tooltip title={property.title}>
              {property.title && property.title.length > 20
                ? `${property.title.substring(0, 20)}...`
                : property.title || "未提供標題"}
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
              <Tag color="orange">{property.buildingType}</Tag>
              <div style={{ marginTop: 8 }}>
                {property.features &&
                  property.features.slice(0, 3).map((feature, index) => (
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
  };

  return (
    <MainLayout>
      <Row
        justify="center"
        align="middle"
        style={{
          minHeight: "500px",
          background: 'url("/image/house.jpg") center/cover no-repeat',
        }}
      >
        <Col xs={24} sm={22} md={20} lg={18} xl={16}>
          <Card
            style={{
              background: "rgba(255, 255, 255, 0.95)",
              boxShadow: "0 5px 15px rgba(0,0,0,0.1)",
            }}
          >
            <Space direction="vertical" size="small" style={{ width: "100%" }}>
              <Title
                level={3}
                style={{
                  fontWeight: 700,
                  color: "#333",
                  marginBottom: 0,
                  marginTop: 12,
                }}
              >
                探索理想的租屋選擇
              </Title>
              <Title
                level={5}
                style={{ fontWeight: 700, color: "#666", marginTop: 8 }}
              >
                輕鬆篩選符合您需求的房源，開始您的租屋旅程
              </Title>
              <Row gutter={[16, 16]}>
                <Col xs={24} md={8}>
                  <Select
                    placeholder="選擇縣市"
                    style={{ width: "100%" }}
                    onChange={handleCityChange}
                    value={selectedCity}
                  >
                    {cities.map((city) => (
                      <Option key={city.id} value={city.cityName}>
                        {city.cityName}
                      </Option>
                    ))}
                  </Select>
                </Col>
                <Col xs={24} md={8}>
                  <Select
                    placeholder="選擇區域"
                    style={{ width: "100%" }}
                    onChange={handleDistrictChange}
                    value={selectedDistrict}
                    disabled={!selectedCity}
                  >
                    {districts.map((district) => (
                      <Option key={district.id} value={district.districtName}>
                        {district.districtName}
                      </Option>
                    ))}
                  </Select>
                </Col>
                <Col xs={24} md={8}>
                  <Select
                    placeholder="選擇道路"
                    style={{ width: "100%" }}
                    onChange={handleRoadChange}
                    value={selectedRoad}
                    disabled={!selectedDistrict}
                  >
                    {roads.map((road) => (
                      <Option key={road.id} value={road.roadName}>
                        {road.roadName}
                      </Option>
                    ))}
                  </Select>
                </Col>
                <Col xs={24} sm={12}>
                  <InputNumber
                    style={{ width: "100%" }}
                    placeholder="最低價格"
                    min={0}
                    step={1000}
                    onChange={handleMinPriceChange}
                    formatter={(value) =>
                      `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                    }
                    parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                    value={minPrice}
                  />
                </Col>
                <Col xs={24} sm={12}>
                  <InputNumber
                    style={{ width: "100%" }}
                    placeholder="最高價格"
                    min={0}
                    step={1000}
                    onChange={handleMaxPriceChange}
                    formatter={(value) =>
                      `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                    }
                    parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                    value={maxPrice}
                  />
                </Col>
                <Col xs={24}>
                  <Select
                    mode="multiple"
                    placeholder="選擇設備"
                    style={{ width: "100%" }}
                    onChange={handleFacilitiesChange}
                    value={selectedFacilities}
                  >
                    {facilities.map((facility) => (
                      <Option key={facility.id} value={facility.facilityName}>
                        {facility.facilityName}
                      </Option>
                    ))}
                  </Select>
                </Col>
                <Col xs={24}>
                  <Select
                    mode="multiple"
                    placeholder="選擇特色"
                    style={{ width: "100%" }}
                    onChange={handleFeaturesChange}
                    value={selectedFeatures}
                  >
                    {features.map((feature) => (
                      <Option key={feature.id} value={feature.featureName}>
                        {feature.featureName}
                      </Option>
                    ))}
                  </Select>
                </Col>

                <Col xs={24} sm={12}>
                  <Button
                    icon={<ReloadOutlined />}
                    style={{
                      width: "100%",
                      background: "linear-gradient(to right, #f5a623, #f7b733)",
                      borderColor: "#f5a623",
                      color: "white",
                    }}
                    onClick={handleClearFilters}
                  >
                    重置搜尋條件
                  </Button>
                </Col>
                <Col xs={24} sm={12}>
                  <Button
                    icon={<HomeOutlined />}
                    style={{
                      width: "100%",
                      background: "linear-gradient(to right, #4a90e2, #5ca0f2)",
                      borderColor: "#4a90e2",
                      color: "white",
                    }}
                    onClick={handleRecommendation}
                  >
                    推薦房源
                  </Button>
                </Col>
              </Row>
            </Space>
          </Card>
        </Col>
      </Row>

      {/* 房源顯示區域 */}
      <div
        style={{
          background: "#f0f2f5",
          padding: 24,
          maxWidth: 1400,
          margin: "0 auto",
        }}
      >
        {properties.length === 0 ? (
          <div style={{ textAlign: "center", marginTop: 50 }}>
            <Empty
              image={
                <SmileOutlined style={{ fontSize: 64, color: "#fa8c16" }} />
              }
              description={
                <Title level={4} style={{ color: "#595959" }}>
                  很抱歉，根據目前的搜尋條件，沒有找到符合的房源。
                </Title>
              }
            />
            <div style={{ marginTop: 20 }}>
              <Button
                type="primary"
                icon={<ReloadOutlined />}
                onClick={handleClearFilters}
              >
                重置搜尋條件
              </Button>
            </div>
          </div>
        ) : (
          <>
            <Row
              gutter={[16, 16]}
              style={{
                padding: 8,
              }}
            >
              <Col>
                <Button onClick={() => handleSort("price")}>
                  租金排序{" "}
                  {sortBy === "price" &&
                    (sortDirection === "asc" ? "低" : "高")}
                </Button>
              </Col>
              <Col>
                <Button onClick={() => handleSort("createdAt")}>
                  上架時間{" "}
                  {sortBy === "createdAt" &&
                    (sortDirection === "asc" ? "早" : "晚")}
                </Button>
              </Col>
            </Row>

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
                align="center"
                pageSize={12}
                total={totalElements}
                onChange={(newPage) => {
                  setPage(newPage - 1);
                  searchParams.set("page", newPage - 1);
                  setSearchParams(searchParams);
                  window.scrollTo({
                    top: 0,
                    behavior: "smooth",
                  });
                }}
                showSizeChanger={false}
              />
            </div>
          </>
        )}
      </div>
    </MainLayout>
  );
};

export default HomePage;
