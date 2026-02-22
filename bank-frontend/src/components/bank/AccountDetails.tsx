import { Descriptions } from "antd";
import AccountActions from "./AccountActions";
import type { BankAccount } from "../../types/BankAccount";

interface Props {
  account: BankAccount;
  setAccount: (account: BankAccount) => void;
}

export default function AccountDetails({ account, setAccount }: Props) {
  return (
    <>
      <Descriptions bordered column={1}>
        <Descriptions.Item label="Owner">
          {account.ownerEmail}
        </Descriptions.Item>

        <Descriptions.Item label="Balance">
          {account.balance} €
        </Descriptions.Item>
      </Descriptions>

      <AccountActions account={account} setAccount={setAccount} />
    </>
  );
}