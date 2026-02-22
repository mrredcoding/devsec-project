import { useEffect, useMemo, useState } from "react";
import { Button, Space } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { getAllAccounts } from "../gateway/bankApi";

import PageCard from "../components/common/PageCard";
import SearchBar from "../components/common/SearchBar";
import AccountTable from "../components/bank/AccountTable";
import CreateAccountModal from "../components/bank/CreateAccountModal";
import AccountDrawer from "../components/bank/AccountDrawer";
import type { BankAccount } from "../types/BankAccount";

export default function AdminPage() {
  const [accounts, setAccounts] = useState<BankAccount[]>([]);
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);

  const [selected, setSelected] = useState<BankAccount | null>(null);

  const load = async () => {
    const data = await getAllAccounts();
    setAccounts(data);
  };

  useEffect(() => {
    load();
  }, []);

  const filteredAccounts = useMemo(() => {
    return accounts.filter((a) =>
      a.ownerEmail.toLowerCase().includes(search.toLowerCase())
    );
  }, [accounts, search]);

  const updateSelected = (updated: BankAccount) => {
    setSelected(updated);

    setAccounts((prev) =>
      prev.map((account) => (account.id === updated.id ? updated : account))
    );
  };

  return (
    <PageCard title="All Bank Accounts">
      <Space
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: 16,
        }}
      >
        <SearchBar value={search} onChange={setSearch} />

        <Button
          icon={<PlusOutlined />}
          type="primary"
          onClick={() => setModalOpen(true)}
        >
          Create Account
        </Button>
      </Space>

      <AccountTable accounts={filteredAccounts} onClick={setSelected} />

      <CreateAccountModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        refresh={load}
      />

      <AccountDrawer
        account={selected}
        setAccount={updateSelected}
        close={() => setSelected(null)}
      />
    </PageCard>
  );
}