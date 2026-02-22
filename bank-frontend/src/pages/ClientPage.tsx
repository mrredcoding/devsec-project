import { useEffect, useState } from "react";
import { getMyAccount } from "../gateway/bankApi";
import PageCard from "../components/common/PageCard";
import AccountDetails from "../components/bank/AccountDetails";

export default function ClientPage() {
  const [account, setAccount] = useState<any>(null);

  const load = async () => {
    const data = await getMyAccount();
    setAccount(data);
  };

  useEffect(() => {
    load();
  }, []);

  if (!account) return null;

  return (
    <PageCard title="My Account">
      <AccountDetails account={account} setAccount={setAccount} />
    </PageCard>
  );
}