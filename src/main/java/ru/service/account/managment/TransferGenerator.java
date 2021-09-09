package ru.service.account.managment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import ru.service.account.entity.Account;

@Service
public class TransferGenerator {

	private final static Logger logger = Logger.getLogger(TransferGenerator.class);


	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private int accoutsCount = 4;
	private int threadsCount = 2;
	private volatile Integer transactionCount = 30;

	private List<Account> accountList;

	public void execute(ApplicationContext context) {
		accountList = generateAccounts();
		logger.debug(accountList);

		List<Thread> threadList = generateThreads();
		threadList.stream().forEach(thread -> thread.start());


		executorService.execute(() -> {
			while (transactionCount > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(getRandomNumberUsingInts(1000, 2000));
				} catch (InterruptedException e) {
					logger.error("interrupt watcher", e);
				}
				threadList.stream().forEach(thread -> thread.interrupt());
			}

			logger.debug(accountList.size() + " " + accountList);

			executorService.shutdown();
			SpringApplication.exit(context, () -> 0);
		});



	}

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

	private void getAccountsAndExecute() {		
		synchronized (transactionCount) {
			if (transactionCount == 0) {
				return;
			}
		}

		int changeValue = getRandomNumberUsingInts(1, 10000);
		Account incomeAcc = null, outcomeAcc = null;
		try {
			incomeAcc = getRandomAccountFromList();
			outcomeAcc = getRandomAccountFromList();			
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("No free accounts to execute");
			if (incomeAcc != null) {
				synchronized (accountList) {
					accountList.add(incomeAcc);
				}
			}
			return;
		}

		TransferOperation transferOperation = new TransferOperation(incomeAcc, outcomeAcc, changeValue);
		synchronized (accountList) {
			accountList.add(incomeAcc);
			accountList.add(outcomeAcc);
		}

		transactionCount--;
//		logger.debug("transaction end " + transactionCount + " " + Thread.currentThread().getName());
	}

	private Account getRandomAccountFromList() throws ArrayIndexOutOfBoundsException {
		Random rand = new Random();
		synchronized (accountList) {
			Account account = accountList.remove(rand.nextInt(accountList.size()));
			return account;
		}		
	}


	private List<Account> generateAccounts() {
		List<Account> accountList = new CopyOnWriteArrayList<Account>();
		for (int i = 0; i < accoutsCount; i++) {
			accountList.add(new Account());
		}
		return accountList;
	}

	private int getRandomNumberUsingInts(int min, int max) {
		Random random = new Random();
		return random.ints(min, max)
				.findFirst()
				.getAsInt();
	}

}
