package com.example.bankaccountday5;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import android.test.InstrumentationTestCase;

import com.example.bankaccountday5.BankAccount;
import com.example.bankaccountday5.BankAccountDAO;
import com.example.bankaccountday5.BankAccountDTO;
import com.example.bankaccountday5.Transaction;
import com.example.bankaccountday5.TransactionDAO;
import com.example.bankaccountday5.TransactionDTO;

public class BankAccountTest extends InstrumentationTestCase {

	private BankAccount bankAcc;
	private Transaction trans;
	private BankAccountDAO bankAccDAO;
	private TransactionDAO transDAO;
	private String accNo;
	private String depositeDesc;
	private String withdrawDesc;
	private long timeStart;
	private long timeEnd;
	private Calendar mockCal;

	protected void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);
		bankAccDAO = mock(BankAccountDAO.class);
		transDAO = mock(TransactionDAO.class);
		bankAcc = new BankAccount();
		trans = new Transaction();
		bankAcc.setBaDAO(bankAccDAO);
		bankAcc.setTDAO(transDAO);
		trans.setTransactionDao(transDAO);
		mockCal = mock(Calendar.class);
		bankAcc.setCal(mockCal);
		trans.setCal(mockCal);
		accNo = "1234567890";
		depositeDesc = "Deposite 100.000$";
		withdrawDesc = "Withdraw 100,0$";
		timeStart = 000001l;
		timeEnd = 999999l;
	}

	@Test
	public void testOpenAccount() {
		bankAcc.openAccount(accNo);
		ArgumentCaptor<BankAccountDTO> doCreateAC = ArgumentCaptor
				.forClass(BankAccountDTO.class);
		verify(bankAccDAO).doCreate(doCreateAC.capture());
		assertEquals(0, doCreateAC.getValue().getBalance(), 0.01);
	}

	@Test
	public void testGetAccount() {
		BankAccountDTO accountOpened = bankAcc.openAccount(accNo);
		when(bankAccDAO.doRead(accNo)).thenReturn(accountOpened);

		BankAccountDTO accountRead = bankAcc.getAccount(accNo);
		assertEquals(accNo, accountRead.getAccountNumber());

		verify(bankAccDAO, times(1)).doCreate(accountOpened);
		assertEquals(accountOpened, accountRead);
	}

	@Test
	public void testDepositSaveAccountToDatabase() {
		ArgumentCaptor<BankAccountDTO> argOpen = ArgumentCaptor
				.forClass(BankAccountDTO.class);
		ArgumentCaptor<BankAccountDTO> argDep = ArgumentCaptor
				.forClass(BankAccountDTO.class);
		BankAccountDTO accountOpened = bankAcc.openAccount(accNo);

		verify(bankAccDAO, times(1)).doCreate(argOpen.capture());
		assertEquals(accNo, argOpen.getValue().getAccountNumber());
		assertEquals(0, argOpen.getValue().getBalance(), 0.01);

		when(bankAccDAO.doRead(accNo)).thenReturn(accountOpened);

		bankAcc.deposit(accountOpened.getAccountNumber(), 100l, depositeDesc);

		verify(bankAccDAO, times(1)).doUpdate(argDep.capture());
		assertEquals(accNo, argDep.getValue().getAccountNumber());
		assertEquals(100, argDep.getValue().getBalance(), 0.01);

	}

	@Test
	public void testDepositSaveAccountToDatabaseIncludeTimeStamp() {
		trans.createTransaction(accNo, 100, depositeDesc);
		long timeStamp = System.currentTimeMillis();
		when(mockCal.getTimeInMillis()).thenReturn(timeStamp);
		ArgumentCaptor<TransactionDTO> argumentTransaction = ArgumentCaptor
				.forClass(TransactionDTO.class);
		verify(transDAO, times(1)).doUpdate(argumentTransaction.capture());

		assertEquals(accNo, argumentTransaction.getValue().getAccountNumber());
		assertEquals(100, argumentTransaction.getValue().getAmount(), 0.01);
		assertEquals(depositeDesc, argumentTransaction.getValue()
				.getDescription());
		assertTrue(argumentTransaction.getValue().getTimestamp() != 0);

		assertEquals(timeStamp, argumentTransaction.getValue().getTimestamp());
	}

	@Test
	public void testWithdraw() {
		BankAccountDTO accountDTO = bankAcc.openAccount(accNo);
		ArgumentCaptor<BankAccountDTO> argument = ArgumentCaptor
				.forClass(BankAccountDTO.class);
		bankAcc.withDraw(accountDTO, 50, withdrawDesc);
		verify(bankAccDAO, times(1)).doUpdate(argument.capture());
		assertEquals(-50, argument.getValue().getBalance(), 0.01);
	}

	@Test
	public void testWithdrawTransaction() {
		trans.createTransaction(accNo, 50, withdrawDesc);
		ArgumentCaptor<TransactionDTO> argumentTransaction = ArgumentCaptor
				.forClass(TransactionDTO.class);
		verify(transDAO).doUpdate(argumentTransaction.capture());
		assertEquals(accNo, argumentTransaction.getValue().getAccountNumber());
		assertEquals(50, argumentTransaction.getValue().getAmount(), 0.01);
		assertEquals(withdrawDesc, argumentTransaction.getValue()
				.getDescription());
		assertTrue(argumentTransaction.getValue().getTimestamp() != 0);
	}

	@Test
	public void testGetHistory() {
		ArgumentCaptor<String> argumentTransaction = ArgumentCaptor
				.forClass(String.class);
		trans.getTransactionsOccurred(accNo);
		verify(transDAO, times(1)).getTransactionsOccurredDao(
				argumentTransaction.capture());
		assertEquals(accNo, argumentTransaction.getValue());
	}

	@Test
	public void testGetHistoryWithPeriodTime() {
		trans.getTransactionsOccurred(accNo, timeStart, timeEnd);
		verify(transDAO, times(1)).getTransactionsOccurredDao(accNo, timeStart,
				timeEnd);
	}

	@Test
	public void testGetHistoryWithLimitedNumber() {
		trans.getLimitTransactions(accNo, 5);
		verify(transDAO, times(1)).getLimitTransactionDao(accNo, 5);
	}

	@Test
	public void testSaveTimestampWhenOpenAccount() {
		Long timeStamp = System.currentTimeMillis();
		when(mockCal.getTimeInMillis()).thenReturn(timeStamp);
		ArgumentCaptor<BankAccountDTO> argBADTO = ArgumentCaptor
				.forClass(BankAccountDTO.class);
		bankAcc.openAccount(accNo);
		verify(bankAccDAO, times(1)).doCreate(argBADTO.capture());
		assertTrue(argBADTO.getValue().getTimeStamp() != null);
		assertEquals(timeStamp, argBADTO.getValue().getTimeStamp());
	}
}
