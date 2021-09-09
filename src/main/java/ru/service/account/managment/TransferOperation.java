package ru.service.account.managment;

import org.apache.log4j.Logger;

import ru.service.account.entity.Account;

public class TransferOperation {
	
	private final static Logger logger = Logger.getLogger(TransferOperation.class);
	
	private Account incomeAccount;
	private Account outcomeAccount;
	
	
	public TransferOperation(Account incomeAccount, Account outcomeAccount, int changeValue) {
		this.incomeAccount = incomeAccount;
		this.outcomeAccount = outcomeAccount;
		
		transferMoney(changeValue);
	}


	private void transferMoney(int changeValue) {
		if (outcomeAccount.getMoney() - changeValue < 0) {
			logger.debug("Not enough money from accout " + outcomeAccount.getID());
			return;
		}
		
		logger.debug("change money from " + outcomeAccount.getID() + " to " + incomeAccount.getID() + " " + changeValue);
		
		changeValueMoney(incomeAccount, changeValue, true);
		changeValueMoney(outcomeAccount, changeValue, false);				
	}
	
	private void changeValueMoney(Account account, Integer value, boolean isIncome) {
		if (isIncome) {
			account.setMoney(account.getMoney() + value);
		} else {
			account.setMoney(account.getMoney() - value);
		}
	}
}
