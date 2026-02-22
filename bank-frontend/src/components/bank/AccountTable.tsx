import { Table } from "antd";

export default function AccountsTable({ accounts, onClick }: any) {
    return (
        <Table
            rowKey="id"
            dataSource={accounts}
            onRow={(account) => ({ onClick: () => onClick(account) })}
            columns={[
                { title: "AccountId", dataIndex: "id" },
                { title: "Owner Email", dataIndex: "ownerEmail" },
                { 
                    title: "Balance", 
                    dataIndex: "balance",
                    render: (balance: number) => `${balance} €`
                },
            ]}
        />
    );
}