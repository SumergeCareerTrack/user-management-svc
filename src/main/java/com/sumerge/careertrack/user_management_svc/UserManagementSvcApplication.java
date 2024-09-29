package com.sumerge.careertrack.user_management_svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;

@SpringBootApplication
public class UserManagementSvcApplication implements CommandLineRunner {
	@Autowired
	TitleMapper titleMapper;

	public static void main(String[] args) {
		SpringApplication.run(UserManagementSvcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Title t = new Title("SE", "Associate", true);
		TitleResponseDTO dto = titleMapper.toDTO(t);
		System.out.println(dto);
	}

}
