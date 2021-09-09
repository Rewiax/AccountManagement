package ru.service.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.service.account.managment.TransferGenerator;

@SpringBootApplication
public class AccountManagmentProjectApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AccountManagmentProjectApplication.class, args);
		
		TransferGenerator generator = context.getBean(TransferGenerator.class);
		generator.execute(context);
	}

}
