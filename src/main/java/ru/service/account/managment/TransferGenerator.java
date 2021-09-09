package ru.service.account.managment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import ru.service.account.entity.Account;

/**
 * @author maxim
 * сервис создания потоков и процессов изменения счетов аккаунтов
 */
@Service
public class TransferGenerator {

	private final static Logger logger = Logger.getLogger(TransferGenerator.class);

	/**
	 * количество аккаунтов для инициализации
	 */
	private int accoutsCount = 4;
	
	/**
	 * количество потоков
	 */
	private int threadsCount = 2;
	
	/**
	 * количество транзакций
	 */
	private volatile Integer transactionCount = 30;

	/**
	 * список аккаунтов
	 */
	private List<Account> accountMap;
	
	private Object mutex = new Object();

	/**
	 * начать выполнение всех транзакций
	 * @param context
	 */
	public void execute(ApplicationContext context) {
		accountMap = generateAccounts();
		logger.debug(accountMap);

		List<Thread> threadList = generateThreads();
		threadList.stream().forEach(thread -> thread.start());

		new Thread(() -> {
			while (transactionCount > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(getRandomNumberUsingInts(1000, 2000));
				} catch (InterruptedException e) {
					logger.error("interrupt watcher", e);
				}
				threadList.stream().forEach(thread -> thread.interrupt());
			}

			logger.debug(accountMap);

			SpringApplication.exit(context, () -> 0);
		}).start();
	}

	/**
	 * создание пула потоков
	 * @return
	 */
	private List<Thread> generateThreads() {
		List<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < threadsCount; i++) {
			threadList.add(new Thread(() -> {
				while (transactionCount > 0) {
					try {
						TimeUnit.SECONDS.sleep((long) (Math.random() * 10));
					} catch (InterruptedException e) {
						//						logger.debug("awake thread " + Thread.currentThread().getName());
					}			
					getAccountsAndExecute();
				}
			}));
		}
		return threadList;
	}

	/**
	 * получение 2 аккаунтов и выполнение операций с ними в рамках транзакции
	 */
	private void getAccountsAndExecute() {		
		synchronized (mutex) {
			if (transactionCount == 0) {
				return;
			}
			transactionCount--;
			logger.debug("transaction " + transactionCount);
		}

		int changeValue = getRandomNumberUsingInts(1, 10000);
		Account incomeAcc = getRandomAccountFromList();
		Account outcomeAcc = getRandomAccountFromList();

		TransferOperation transferOperation = new TransferOperation(incomeAcc, outcomeAcc);
		transferOperation.transferMoney(changeValue);
	}

	/**
	 * получить случайный аккаунт из списка
	 * @return
	 */
	private Account getRandomAccountFromList() {
		synchronized (accountMap) {
			Account account = accountMap.get(new Random().nextInt(accountMap.size()));
			return account;
		}		
	}


	/**
	 * создание списка аккаунтов
	 * @return
	 */
	private List<Account> generateAccounts() {
		List<Account> accountList = new ArrayList<Account>();
		for (int i = 0; i < accoutsCount; i++) {
			Account account = new Account();
			accountList.add(account);
		}
		return accountList;
	}

	/**
	 * генератор рандомных чисел
	 * @param min
	 * @param max
	 * @return
	 */
	private int getRandomNumberUsingInts(int min, int max) {
		Random random = new Random();
		return random.ints(min, max)
				.findFirst()
				.getAsInt();
	}

}
