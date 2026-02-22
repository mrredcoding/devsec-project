import { Modal, Form, Input, notification } from "antd";
import { createBankAccount } from "../../gateway/bankApi";
import { useState } from "react";

export default function CreateAccountModal({
  open,
  onClose,
  refresh,
}: any) {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    try {
      const { email } = await form.validateFields();

      setLoading(true);

      await createBankAccount(email);

      notification.success({
        title: "Account successfully created for " + email,
      });

      form.resetFields();
      refresh();
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Failed to create account.";
      notification.error({
        title: errorMessage,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Create Bank Account"
      open={open}
      onOk={submit}
      confirmLoading={loading}
      onCancel={onClose}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="email"
          label="Owner Email"
          rules={[
            { required: true, message: "Email is required." },
            { type: "email", message: "Please enter a valid email address." },
          ]}
        >
          <Input placeholder="example@mail.com" />
        </Form.Item>
      </Form>
    </Modal>
  );
}