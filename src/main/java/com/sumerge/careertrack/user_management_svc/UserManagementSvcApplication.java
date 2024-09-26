package com.sumerge.careertrack.user_management_svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@SpringBootApplication
public class UserManagementSvcApplication implements CommandLineRunner {
	@Autowired
	TitleRepository titleRepo;

	@Autowired
	AppUserRepository userRepo;

	public static void main(String[] args) {
		SpringApplication.run(UserManagementSvcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}

}
