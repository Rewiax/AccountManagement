package ru.service.account.managment;

import org.apache.log4j.Logger;

import ru.service.account.entity.Account;

/**
 * @author maxim
 * Класс выполнения операций изменения счета
 */
public class TransferOperation {
	
	private final static Logger logger = Logger.getLogger(TransferOperation.class);
	
	private Account incomeAccount;
	private Account outcomeAccount;
	
	
	TransferOperation(Account incomeAccount, Account outcomeAccount) {
		this.incomeAccount = incomeAccount;
		this.outcomeAccount = outcomeAccount;
	}


	void transferMoney(int changeValue) {
		outcomeAccount.lock();
		
		if (outcomeAccount.getMoney() - changeValue < 0) {
			logger.debug("Not enough money from accout " + outcomeAccount.getID() + " " + outcomeAccount.getMoney());
			outcomeAccount.unlock();
			return;
		}
		
		logger.debug("change money from " + outcomeAccount.getID() + " " + changeValue);

		changeValueMoney(outcomeAccount, changeValue, false);	
		outcomeAccount.unlock();
		
		incomeAccount.lock();
		logger.debug("change money to " + incomeAccount.getID() + " " + changeValue);
		changeValueMoney(incomeAccount, changeValue, true);

		incomeAccount.unlock();		
	}
	
	private void changeValueMoney(Account account, Integer value, boolean isIncome) {
		if (isIncome) {
			account.setMoney(account.getMoney() + value);
		} else {
			account.setMoney(account.getMoney() - value);
		}
	}
}
