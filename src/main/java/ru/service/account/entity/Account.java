package ru.service.account.entity;

import java.util.UUID;

import org.apache.log4j.Logger;

public class Account {
	
	private final static Logger logger = Logger.getLogger(Account.class);

	private String ID = UUID.randomUUID().toString();
	private volatile Integer Money = 10000;
	
	

	public String getID() {
		return ID;
	}

	public Integer getMoney() {
		return Money;
	}

	public void setMoney(Integer money) {
		Money = money;
		
//		logger.debug("set money " + money + " to " + ID);
	}

	@Override
	public String toString() {
		return "Account [ID=" + ID + ", Money=" + Money + "]";
	}
	
	
}
