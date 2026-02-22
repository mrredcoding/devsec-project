import { Card } from "antd";

export default function PageCard({ title, children }: any) {
  return (
    <Card
      title={title}
      style={{
        margin: 24,
        borderRadius: 12,
      }}
      bodyStyle={{ padding: 24 }}
    >
      {children}
    </Card>
  );
}