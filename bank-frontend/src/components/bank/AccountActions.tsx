import { Button, InputNumber, Space, notification } from "antd";
import { useState } from "react";
import { updateBalance } from "../../gateway/bankApi";
import type { BankAccount } from "../../types/BankAccount";
import { ActionType } from "../../types/ActionType.ts";

interface Props {
  account: BankAccount;
  setAccount: (account: BankAccount) => void;
}

export default function AccountActions({ account, setAccount }: Props) {
  const [amount, setAmount] = useState<number>(0);
  const [loading, setLoading] = useState(false);

  const action = async (type: ActionType) => {
    if (!amount || amount <= 0) return;

    try {
      setLoading(true);

      const updatedAccount = await updateBalance(account.id, amount, type);

      const updated = {
          ...account,
          balance: updatedAccount.balance
      };

      setAccount(updated);

      notification.success({
        title: `${amount} € ${type}ed successfully ${type === ActionType.CREDIT ? "to" : "from"} ${account.ownerEmail}'s account.`,
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || `The ${type} failed.`;
      notification.error({ title: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Space style={{ marginTop: 16 }}>
      <InputNumber
        min={1}
        value={amount}
        onChange={(v) => setAmount(v || 0)}
        placeholder="Amount"
      />

      <Button type="primary" loading={loading} onClick={() => action(ActionType.CREDIT)}>
        Credit
      </Button>

      <Button danger loading={loading} onClick={() => action(ActionType.DEBIT)}>
        Debit
      </Button>
    </Space>
  );
}