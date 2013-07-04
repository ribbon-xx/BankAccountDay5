package com.example.bankaccountday5;

import java.util.ArrayList;
import java.util.Calendar;

public class Transaction {
	private TransactionDAO tDAO;
	private Calendar cal;

	public void setTransactionDao(TransactionDAO tDAO) {
		this.tDAO = tDAO;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public TransactionDTO createTransaction(String accountNumber, long amount,
			String description) {
		TransactionDTO transactionDTO = new TransactionDTO(accountNumber,
				amount, description, System.currentTimeMillis());
		tDAO.doUpdate(transactionDTO);
		return transactionDTO;
	}

	public ArrayList<TransactionDTO> getTransactionsOccurred(
			String accountNumber) {
		return tDAO.getTransactionsOccurredDao(accountNumber);
	}

	public ArrayList<TransactionDTO> getTransactionsOccurred(
			String accountNumber, long startTime, long stopTime) {
		return tDAO.getTransactionsOccurredDao(accountNumber, startTime,
				stopTime);
	}

	public ArrayList<TransactionDTO> getLimitTransactions(String accountNumber,
			int limit) {
		return tDAO.getLimitTransactionDao(accountNumber, limit);
	}
}