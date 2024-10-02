import React, { useState, useEffect } from "react";
import { Select, Space, Typography } from "antd";
import {
  SortAscendingOutlined,
  SortDescendingOutlined,
} from "@ant-design/icons";

const { Option } = Select;
const { Text } = Typography;

const SortControls = ({ sortBy, sortDirection, handleSort }) => {
  const [localValue, setLocalValue] = useState(`${sortBy}-${sortDirection}`);

  useEffect(() => {
    setLocalValue(`${sortBy}-${sortDirection}`);
  }, [sortBy, sortDirection]);

  const handleChange = (value) => {
    setLocalValue(value);
    const [newSortBy, newSortDirection] = value.split("-");
    handleSort(newSortBy, newSortDirection);
  };

  return (
    <div className="bg-white p-4 mb-4 rounded-lg shadow-md">
      <Space size="large" align="center">
        <Text
          strong
          style={{
            fontSize: "16px",
            color: "#262626",
            borderBottom: "2px solid #1890ff",
            paddingBottom: "4px",
          }}
        >
          排序方式
        </Text>
        <Select
          style={{ width: 160 }}
          value={localValue}
          onChange={handleChange}
        >
          <Option value="price-asc">
            租金 - 由低到高 <SortAscendingOutlined />
          </Option>
          <Option value="price-desc">
            租金 - 由高到低 <SortDescendingOutlined />
          </Option>
          <Option value="createdAt-desc">
            上架時間 - 最新 <SortDescendingOutlined />
          </Option>
          <Option value="createdAt-asc">
            上架時間 - 最早 <SortAscendingOutlined />
          </Option>
        </Select>
      </Space>
    </div>
  );
};

export default SortControls;
