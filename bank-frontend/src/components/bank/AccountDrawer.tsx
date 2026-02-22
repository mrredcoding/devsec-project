import { Drawer, Typography } from "antd";
import AccountDetails from "./AccountDetails";
import type { BankAccount } from "../../types/BankAccount";

interface Props {
  account: BankAccount | null;
  setAccount: (account: BankAccount) => void;
  close: () => void;
}

export default function AccountDrawer({ account, setAccount, close }: Props) {
  if (!account) return null;

  return (
    <Drawer
      open={!!account}
      onClose={close}
      width={450}
      title={
          <Typography.Text
              copyable={{ text: account.id }}
              style={{ fontSize: 14 }}>
          {account.id}
        </Typography.Text>
      }
    >
      <AccountDetails account={account} setAccount={setAccount} />
    </Drawer>
  );
}