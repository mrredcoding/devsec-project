import { Card, Form, Input, Button, notification } from "antd";
import { useAuthentication } from "../context/AuthenticationContext";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

export default function LoginPage() {
  const { login } = useAuthentication();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const submit = async (values: any) => {
    try {
      setLoading(true);

      await login(values.email, values.password);

      notification.success({
        title: "Welcome back!",
      });

      navigate("/");
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to login.";
      notification.error({
        title: errorMessage,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "#f5f5f5",
      }}
    >
      <Card title="Bank Login" style={{ width: 350 }}>
        <Form layout="vertical" onFinish={submit}>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Email is required." },
              { type: "email", message: "Please enter a valid email address." },
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="password"
            label="Password"
            rules={[{ required: true, message: "Password is required." }]}
          >
            <Input.Password />
          </Form.Item>

          <Button
            type="primary"
            htmlType="submit"
            block
            loading={loading}
          >
            Login
          </Button>
        </Form>
      </Card>
    </div>
  );
}