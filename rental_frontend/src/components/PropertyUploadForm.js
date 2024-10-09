import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  InputNumber,
  Button,
  Select,
  Upload,
  message,
  Checkbox,
  Row,
  Col,
  Typography,
  Card,
  Spin,
} from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import PropertyCard from "./PropertyCard";

const { Title } = Typography;
const { Option } = Select;

const PropertyUploadForm = ({ token, setIsLoginModalVisible }) => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const defaultPropertyLayout = {
    roomCount: 0,
    livingRoomCount: 0,
    bathroomCount: 0,
    balconyCount: 0,
    kitchenCount: 0,
  };

  // 用於即時預覽的狀態
  const [previewData, setPreviewData] = useState({
    title: "",
    cityName: "",
    districtName: "",
    roadName: "",
    price: null,
    mainImage: null,
    area: null,
    propertyType: "",
    buildingType: "",
    propertyLayout: defaultPropertyLayout,
    features: [],
  });

  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [roads, setRoads] = useState([]);
  const [facilities, setFacilities] = useState([]);
  const [features, setFeatures] = useState([]);
  const [selectedCity, setSelectedCity] = useState(null);
  const [selectedDistrict, setSelectedDistrict] = useState(null);
  const [mainImageUrl, setMainImageUrl] = useState(null);
  const [fileList, setFileList] = useState([]);

  useEffect(() => {
    if (!token) {
      // 如果未登入，跳轉到登入頁或顯示登入彈窗
      message.warning("請先登入才能上傳房源");
      setIsLoginModalVisible(true);
      navigate("/");
    }
  }, [token, setIsLoginModalVisible, navigate]);

  useEffect(() => {
    return () => {
      if (previewData.mainImage) {
        URL.revokeObjectURL(previewData.mainImage);
      }
    };
  }, [previewData.mainImage]);

  // 獲取縣市列表
  useEffect(() => {
    fetch("/api/geo/city")
      .then((res) => res.json())
      .then((data) => {
        setCities(data.data);
      })
      .catch((err) => console.error(err));
  }, []);

  // 當選擇縣市後，獲取區域列表
  useEffect(() => {
    if (selectedCity) {
      fetch(`/api/geo/district?cityName=${selectedCity}`)
        .then((res) => res.json())
        .then((data) => {
          setDistricts(data.data);
        })
        .catch((err) => console.error(err));
    } else {
      setDistricts([]);
      setSelectedDistrict(null);
    }
  }, [selectedCity]);

  // 當選擇區域後，獲取道路列表
  useEffect(() => {
    if (selectedDistrict) {
      fetch(
        `/api/geo/road?cityName=${selectedCity}&districtName=${selectedDistrict}`
      )
        .then((res) => res.json())
        .then((data) => {
          setRoads(data.data);
        })
        .catch((err) => console.error(err));
    } else {
      setRoads([]);
    }
  }, [selectedDistrict, selectedCity]);

  // 獲取設備和特色
  useEffect(() => {
    fetch("/api/facility")
      .then((res) => res.json())
      .then((data) => {
        setFacilities(data.data);
      })
      .catch((err) => console.error(err));

    fetch("/api/feature")
      .then((res) => res.json())
      .then((data) => {
        setFeatures(data.data);
      })
      .catch((err) => console.error(err));
  }, []);

  const handleCityChange = (value) => {
    setSelectedCity(value);
    form.resetFields(["districtName", "roadName"]);
  };

  const handleDistrictChange = (value) => {
    setSelectedDistrict(value);
    form.resetFields(["roadName"]);
  };

  const handleFinish = (values) => {
    setLoading(true);
    const formData = new FormData();

    // 主圖片處理
    if (fileList.length > 0) {
      formData.append("mainImage", fileList[0].originFileObj);
    }

    // 多張圖片處理
    if (values.images) {
      values.images.fileList.forEach((file) => {
        formData.append("images", file.originFileObj);
      });
    }

    // 其他表單資料
    for (const key in values) {
      if (key !== "mainImage" && key !== "images") {
        formData.append(key, values[key]);
      }
    }

    // 發送請求
    fetch("/api/property/create", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    })
      .then((res) => {
        if (!res.ok) {
          return res.json().then((data) => {
            message.error(`上傳失敗：${data.message}`);
            throw new Error(data.message);
          });
        }
        return res.json();
      })
      .then((data) => {
        message.success(data.data);
        navigate("/");
      })
      .catch((err) => {
        console.error(err);
        message.error("上傳失敗，請稍後再試");
      })
      .finally(() => {
        setLoading(false); // 完成後停止 loading
      });
  };

  const handleFormChange = (changedValues, allValues) => {
    if ("mainImage" in changedValues) {
      const fileList = changedValues.mainImage?.fileList;
      if (fileList && fileList.length > 0) {
        const file = fileList[0].originFileObj;
        if (file) {
          const imageUrl = URL.createObjectURL(file);
          setMainImageUrl(imageUrl);
        }
      } else {
        // 圖片被刪除
        setMainImageUrl(null);
      }
    }

    setPreviewData((prev) => ({
      ...prev,
      ...allValues,
      mainImage: mainImageUrl, // 使用單獨的 state 來管理圖片 URL
      propertyLayout: {
        ...prev.propertyLayout,
        roomCount: allValues.roomCount ?? prev.propertyLayout.roomCount,
        livingRoomCount:
          allValues.livingRoomCount ?? prev.propertyLayout.livingRoomCount,
        bathroomCount:
          allValues.bathroomCount ?? prev.propertyLayout.bathroomCount,
        balconyCount:
          allValues.balconyCount ?? prev.propertyLayout.balconyCount,
        kitchenCount:
          allValues.kitchenCount ?? prev.propertyLayout.kitchenCount,
      },
    }));
  };

  useEffect(() => {
    return () => {
      // 清理 URL 對象
      if (mainImageUrl) {
        URL.revokeObjectURL(mainImageUrl);
      }
    };
  }, [mainImageUrl]);

  // 更新 previewData 中的 mainImage
  useEffect(() => {
    setPreviewData((prev) => ({
      ...prev,
      mainImage: mainImageUrl,
    }));
  }, [mainImageUrl]);

  const handleFileListChange = (info) => {
    setFileList(info.fileList);
  };

  return (
    <Card className="property-upload-form" style={{ margin: "24px" }}>
      <Spin spinning={loading}>
        <Row gutter={32}>
          <Col span={14}>
            <Title level={2}>上傳房源</Title>
            <Form
              form={form}
              layout="vertical"
              onFinish={handleFinish}
              onValuesChange={handleFormChange}
              scrollToFirstError
              initialValues={{
                ...defaultPropertyLayout,
              }}
            >
              <Form.Item
                name="title"
                label="標題"
                rules={[{ required: true, message: "請輸入房源標題" }]}
              >
                <Input />
              </Form.Item>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    name="cityName"
                    label="縣市"
                    rules={[{ required: true, message: "請選擇縣市" }]}
                  >
                    <Select onChange={handleCityChange}>
                      {cities.map((city) => (
                        <Option key={city.id} value={city.cityName}>
                          {city.cityName}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="districtName"
                    label="區域"
                    rules={[{ required: true, message: "請選擇區域" }]}
                  >
                    <Select onChange={handleDistrictChange}>
                      {districts.map((district) => (
                        <Option key={district.id} value={district.districtName}>
                          {district.districtName}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="roadName"
                    label="道路"
                    rules={[{ required: true, message: "請選擇道路" }]}
                  >
                    <Select>
                      {roads.map((road) => (
                        <Option key={road.id} value={road.roadName}>
                          {road.roadName}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                name="address"
                label="地址"
                rules={[{ required: true, message: "請輸入詳細地址" }]}
              >
                <Input />
              </Form.Item>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    name="price"
                    label="價格 (每月)"
                    rules={[{ required: true, message: "請輸入價格" }]}
                  >
                    <InputNumber
                      style={{ width: "100%" }}
                      min={0}
                      formatter={(value) =>
                        `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                      }
                      parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item name="deposit" label="押金">
                    <InputNumber
                      style={{ width: "100%" }}
                      min={0}
                      formatter={(value) =>
                        `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                      }
                      parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                    />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item
                name="propertyType"
                label="房屋類型"
                rules={[{ required: true, message: "請選擇房屋類型" }]}
              >
                <Select>
                  <Option value="整層">整層</Option>
                  <Option value="獨立套房">獨立套房</Option>
                  <Option value="分租套房">分租套房</Option>
                  <Option value="雅房">雅房</Option>
                </Select>
              </Form.Item>

              <Form.Item
                name="buildingType"
                label="建築型態"
                rules={[{ required: true, message: "請選擇建築型態" }]}
              >
                <Select>
                  <Option value="電梯大樓">電梯大樓</Option>
                  <Option value="公寓">公寓</Option>
                  <Option value="透天">透天</Option>
                  <Option value="別墅">別墅</Option>
                  <Option value="其他">其他</Option>
                </Select>
              </Form.Item>

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item
                    name="area"
                    label="坪數"
                    rules={[{ required: true, message: "請輸入坪數" }]}
                  >
                    <InputNumber style={{ width: "100%" }} min={0} step={0.1} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="floor"
                    label="所在樓層"
                    rules={[
                      { required: true, message: "請輸入此房源所在樓層" },
                    ]}
                  >
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    name="totalFloor"
                    label="總樓層數"
                    rules={[{ required: true, message: "請輸入總樓層數" }]}
                  >
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
              </Row>

              {/* 圖片上傳 */}
              <Form.Item
                name="mainImage"
                label="主圖片"
                rules={[{ required: true, message: "請上傳主圖片" }]}
              >
                <Upload
                  fileList={fileList}
                  onChange={handleFileListChange}
                  maxCount={1}
                  beforeUpload={() => false}
                  listType="picture-card"
                  onRemove={() => {
                    setMainImageUrl(null);
                    return true;
                  }}
                >
                  <div>
                    <UploadOutlined />
                    <div style={{ marginTop: 8 }}>上傳</div>
                  </div>
                </Upload>
              </Form.Item>

              <Form.Item name="images" label="房源圖片">
                <Upload
                  beforeUpload={() => false}
                  listType="picture-card"
                  multiple
                >
                  <div>
                    <UploadOutlined />
                    <div style={{ marginTop: 8 }}>上傳</div>
                  </div>
                </Upload>
              </Form.Item>

              <Form.Item name="description" label="描述">
                <Input.TextArea rows={4} />
              </Form.Item>

              <Form.Item name="managementFee" label="管理費">
                <InputNumber
                  style={{ width: "100%" }}
                  min={0}
                  formatter={(value) =>
                    `NT$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                  }
                  parser={(value) => value.replace(/NT\$\s?|(,*)/g, "")}
                />
              </Form.Item>

              <Form.Item name="rentPeriod" label="租期">
                <Input />
              </Form.Item>

              {/* 特色 */}
              <Form.Item name="features" label="特色">
                <Checkbox.Group>
                  <Row>
                    {features.map((feature) => (
                      <Col span={8} key={feature.id}>
                        <Checkbox value={feature.featureName}>
                          {feature.featureName}
                        </Checkbox>
                      </Col>
                    ))}
                  </Row>
                </Checkbox.Group>
              </Form.Item>

              {/* 設備 */}
              <Form.Item name="facilities" label="設備">
                <Checkbox.Group>
                  <Row>
                    {facilities.map((facility) => (
                      <Col span={8} key={facility.id}>
                        <Checkbox value={facility.facilityName}>
                          {facility.facilityName}
                        </Checkbox>
                      </Col>
                    ))}
                  </Row>
                </Checkbox.Group>
              </Form.Item>

              {/* 房屋格局 */}

              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item name="roomCount" label="房間數">
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="livingRoomCount" label="客廳數">
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item name="bathroomCount" label="衛浴數">
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item name="balconyCount" label="陽台數">
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item name="kitchenCount" label="廚房數">
                    <InputNumber style={{ width: "100%" }} min={0} />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  size="large"
                  block
                  loading={loading}
                >
                  提交房源
                </Button>
              </Form.Item>
            </Form>
          </Col>
          <Col span={10}>
            <Card>
              <Title level={4}>預覽卡片</Title>
              <PropertyCard property={previewData} />
            </Card>
          </Col>
        </Row>
      </Spin>
    </Card>
  );
};

export default PropertyUploadForm;
