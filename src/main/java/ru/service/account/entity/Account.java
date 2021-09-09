package ru.service.account.entity;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author User
 * модель аккаунта
 */
public class Account {

	private String ID = UUID.randomUUID().toString();
	private volatile Integer Money = 10000;
	
	/**
	 * блокировка операций работы с аккаунтом
	 */
	private volatile ReentrantLock reentrantLock = new ReentrantLock();
	

	public String getID() {
		return ID;
	}

	public Integer getMoney() {
		return Money;
	}

	public void setMoney(Integer money) {
		Money = money;
	}	
	
	public void lock() {
		reentrantLock.lock();
	}
	
	public void unlock() {
		reentrantLock.unlock();
	}	

	@Override
	public String toString() {
		return "Account [ID=" + ID + ", Money=" + Money + "]";
	}	
}
