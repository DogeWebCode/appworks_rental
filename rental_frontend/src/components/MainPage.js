import React, { useState, useEffect } from "react";
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
} from "antd";
import { ReloadOutlined, SmileOutlined } from "@ant-design/icons";
import MainLayout from "./layout/MainLayout";
import { useNavigate } from "react-router-dom";

const { Option } = Select;
const { Title, Text } = Typography;

const HomePage = () => {
  const [properties, setProperties] = useState([]);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [, setSearchTriggered] = useState(false);

  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [roads, setRoads] = useState([]);

  const [selectedCity, setSelectedCity] = useState(null);
  const [selectedDistrict, setSelectedDistrict] = useState(null);
  const [selectedRoad, setSelectedRoad] = useState(null);

  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);

  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);
  const [selectedFacilities, setSelectedFacilities] = useState([]);
  const [selectedFeatures, setSelectedFeatures] = useState([]);

  useEffect(() => {
    const fetchProperties = async () => {
      try {
        const queryParams = new URLSearchParams();
        if (selectedCity) queryParams.append("city", selectedCity);
        if (selectedDistrict) queryParams.append("district", selectedDistrict);
        if (selectedRoad) queryParams.append("road", selectedRoad);
        if (minPrice) queryParams.append("minPrice", minPrice);
        if (maxPrice) queryParams.append("maxPrice", maxPrice);
        if (selectedFeatures.length > 0) {
          queryParams.append("feature", selectedFeatures.join(","));
        }
        if (selectedFacilities.length > 0) {
          queryParams.append("facility", selectedFacilities.join(","));
        }
        queryParams.append("page", page);

        const response = await fetch(
          `/api/property/search?${queryParams.toString()}`
        );
        const data = await response.json();
        setProperties(data.data);
        setTotalElements(data.totalElements);
      } catch (error) {
        console.error("Error fetching properties:", error);
      }
    };

    fetchProperties();
  }, [
    page,
    selectedCity,
    selectedDistrict,
    selectedRoad,
    minPrice,
    maxPrice,
    selectedFeatures,
    selectedFacilities,
  ]);

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
          setSelectedDistrict(null); // 清空區域選項
          setRoads([]); // 清空道路選項
          setSelectedRoad(null); // 清空選定的道路
        })
        .catch((err) => console.error(err));
    } else {
      setDistricts([]);
      setRoads([]);
    }
  }, [selectedCity]);

  // 當選擇區域後讀相對應的道路
  useEffect(() => {
    if (selectedDistrict) {
      fetch(
        `/api/geo/road?districtName=${selectedDistrict}&cityName=${selectedCity}`
      )
        .then((res) => res.json())
        .then((data) => {
          setRoads(data.data || []);
          setSelectedRoad(null); // 清空選定的道路
        })
        .catch((err) => console.error(err));
    } else {
      setRoads([]);
    }
  }, [selectedDistrict, selectedCity]);

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

  //   // 當用戶點擊搜索按鈕
  //   const handleSearch = () => {
  //     setPage(0); // 重置頁數
  //     setSearchTriggered(true); // 標記搜索已觸發
  //   };

  // 當用戶點擊清除搜索條件
  const handleClearFilters = () => {
    setSelectedCity(null);
    setSelectedDistrict(null);
    setSelectedRoad(null);
    setMinPrice(null);
    setMaxPrice(null);
    setSelectedFacilities([]);
    setSelectedFeatures([]);
    setSearchTriggered(false); // 重置搜索觸發狀態
    setPage(0); // 重置頁數
  };

  //   const validateSearchParams = () => {
  //     if (
  //       searchParams.minPrice &&
  //       searchParams.maxPrice &&
  //       searchParams.minPrice > searchParams.maxPrice
  //     ) {
  //       message.error("最低價格不能高於最高價格");
  //       return false;
  //     }
  //     return true;
  //   };

  //   const handlePriceChange = (type, value) => {
  //     setSearchParams((prevParams) => ({
  //       ...prevParams,
  //       [type === "min" ? "minPrice" : "maxPrice"]: value,
  //     }));
  //   };

  const PropertyCard = ({ property }) => {
    const navigate = useNavigate(); // 使用 useNavigate 來處理導航

    const handleCardClick = () => {
      navigate(`/property/${property.id}`); // 點擊後導航到詳細頁面，並傳遞 propertyId
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
        onClick={handleCardClick} // 點擊卡片時導航到詳細頁面
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
              <Tag color="orange">{property.buildingType}</Tag>
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
  };

  return (
    <MainLayout>
      <div
        style={{
          position: "relative",
          height: "450px",
          backgroundImage: 'url("/image/house.jpg")',
          backgroundSize: "cover",
          backgroundPosition: "center",
          marginBottom: "20px",
        }}
      >
        {/* 搜尋表單懸浮在大圖上 */}
        <div
          style={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            backgroundColor: "rgba(255, 255, 255, 0.95)",
            padding: "20px",
            borderRadius: "10px",
            width: "100%",
            maxWidth: "900px",
          }}
        >
          <Title level={2} style={{ fontWeight: 600, color: "#333" }}>
            探索理想的租屋選擇
          </Title>
          <Title level={5} style={{ color: "#666" }}>
            輕鬆篩選符合您需求的房源，開始您的租屋旅程
          </Title>
          <div style={{ marginTop: 24 }}>
            <Row gutter={16} align="middle">
              {/* 縣市選單 */}
              <Col span={5}>
                <Select
                  placeholder="選擇縣市"
                  style={{ width: "100%" }}
                  onChange={(value) => setSelectedCity(value)}
                  value={selectedCity}
                >
                  {cities.map((city) => (
                    <Option key={city.id} value={city.cityName}>
                      {city.cityName}
                    </Option>
                  ))}
                </Select>
              </Col>

              {/* 區域選單 */}
              <Col span={5}>
                <Select
                  placeholder="選擇區域"
                  style={{ width: "100%" }}
                  onChange={(value) => setSelectedDistrict(value)}
                  value={selectedDistrict} // 保留當前選中的區域
                  disabled={!selectedCity}
                >
                  {districts.map((district) => (
                    <Option key={district.id} value={district.districtName}>
                      {district.districtName}
                    </Option>
                  ))}
                </Select>
              </Col>

              {/* 道路選單 */}
              <Col span={5}>
                <Select
                  placeholder="選擇道路"
                  style={{ width: "100%" }}
                  onChange={(value) => setSelectedRoad(value)}
                  value={selectedRoad} // 保留當前選中的道路
                  disabled={!selectedDistrict}
                >
                  {roads.map((road) => (
                    <Option key={road.id} value={road.roadName}>
                      {road.roadName}
                    </Option>
                  ))}
                </Select>
              </Col>

              {/* 價格輸入 */}
              <Col span={4}>
                <InputNumber
                  style={{ width: "100%" }}
                  placeholder="最低價格"
                  min={0}
                  step={1000}
                  onChange={(value) => setMinPrice(value)}
                  formatter={(value) =>
                    `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                  }
                  parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                  value={minPrice}
                />
              </Col>

              <Col span={4}>
                <InputNumber
                  style={{ width: "100%" }}
                  placeholder="最高價格"
                  min={0}
                  step={1000}
                  onChange={(value) => setMaxPrice(value)}
                  formatter={(value) =>
                    `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                  }
                  parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                  value={maxPrice}
                />
              </Col>
            </Row>

            {/* 設備多選 */}
            <Row style={{ marginTop: 24 }}>
              <Col span={24}>
                <Select
                  mode="multiple"
                  placeholder="選擇設備"
                  style={{ width: "100%" }}
                  onChange={(value) => setSelectedFacilities(value)}
                  value={selectedFacilities}
                >
                  {facilities.map((facility) => (
                    <Option key={facility.id} value={facility.facilityName}>
                      {facility.facilityName}
                    </Option>
                  ))}
                </Select>
              </Col>
            </Row>

            {/* 特色多選 */}
            <Row style={{ marginTop: 24 }}>
              <Col span={24}>
                <Select
                  mode="multiple"
                  placeholder="選擇特色"
                  style={{ width: "100%", marginTop: 10 }}
                  onChange={(value) => setSelectedFeatures(value)}
                  value={selectedFeatures}
                >
                  {features.map((feature) => (
                    <Option key={feature.id} value={feature.featureName}>
                      {feature.featureName}
                    </Option>
                  ))}
                </Select>
              </Col>
            </Row>

            {/* 清除搜尋條件按鈕 */}
            <Row style={{ marginTop: 16 }}>
              <Col span={24}>
                <Button
                  type="primary"
                  icon={<ReloadOutlined />}
                  style={{ width: "100%" }}
                  onClick={handleClearFilters}
                >
                  重置搜尋條件
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
                  setSearchTriggered(true);
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
