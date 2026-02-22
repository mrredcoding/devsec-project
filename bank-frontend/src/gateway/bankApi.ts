import { ActionType } from "../types/ActionType";
import { api } from "./api";

export const createBankAccount = async (ownerEmail: string) => {
    await api.post(`/bank/accounts/create?ownerEmail=${ownerEmail}`);
};

export const getAllAccounts = async () => {
    const { data } = await api.get("/bank/accounts/all");
    return data;
};

export const getMyAccount = async () => {
    const { data } = await api.get("/bank/accounts/mine");
    return data;
}

const creditAccount = async (accountId: string, amount: number) => {
    const { data } = await api.patch(`/bank/accounts/${accountId}/credit?amount=${amount}`);
    return data;
};

const debitAccount = async (accountId: string, amount: number) => {
    const { data } = await api.patch(`/bank/accounts/${accountId}/debit?amount=${amount}`);
    return data;
};

export const updateBalance = async (accountId: string, amount: number, action: ActionType) => {
    switch (action) {
        case ActionType.CREDIT: 
            return await creditAccount(accountId, amount);
        
        case ActionType.DEBIT:
            return await debitAccount(accountId, amount);
    }
}