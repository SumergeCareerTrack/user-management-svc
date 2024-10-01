package com.sumerge.careertrack.user_management_svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UserManagementSvcApplication implements CommandLineRunner {


	public static void main(String[] args) {
		SpringApplication.run(UserManagementSvcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}

}
