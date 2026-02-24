import { Layout, Menu, Typography, Space } from "antd";
import {  BankOutlined, LogoutOutlined, UserOutlined } from "@ant-design/icons";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuthentication } from "../../context/AuthenticationContext";

const { Header, Content } = Layout;
const { Text } = Typography;

export default function AppLayout() {
  const { user, logout } = useAuthentication();
  const navigate = useNavigate();

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "0 24px"
        }}
      >
        {/* Left side logo */}
        <Space style={{ color: "white", fontSize: 18, fontWeight: 600 }}>
          <BankOutlined />
            DevSec BANK & Co.
        </Space>

        {/* Right side user info */}
        <Space size="large">
          <Text style={{ color: "white" }}>
            <UserOutlined style={{ marginRight: 6 }} />
            Welcome {user?.name}
          </Text>

          <Menu
            theme="dark"
            mode="horizontal"
            selectable={false}
            items={[
              {
                key: "logout",
                label: "Logout",
                icon: <LogoutOutlined />,
                onClick: () => {
                  logout();
                  navigate("/login");
                }
              }
            ]}
          />
        </Space>
      </Header>

      <Content style={{ padding: 32 }}>
        <Outlet />
      </Content>
    </Layout>
  );
}