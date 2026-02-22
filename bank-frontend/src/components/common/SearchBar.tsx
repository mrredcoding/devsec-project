import { Input } from "antd";
import { SearchOutlined } from "@ant-design/icons";

export default function SearchBar({ value, onChange }: any) {
  return (
    <Input
      allowClear
      placeholder="Search by email..."
      prefix={<SearchOutlined />}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      style={{ width: 300, marginBottom: 16 }}
    />
  );
}