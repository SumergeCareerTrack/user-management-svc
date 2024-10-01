package com.sumerge.careertrack.user_management_svc.service;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.entities.responses.AuthenticationResponse;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.exceptions.InvalidCredentialsException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final TitleRepository titleRepository;
	private final DepartmentRepository deptRepository;

	public AuthenticationResponse register(RegisterRequest request) {
		if (appUserRepository.existsByEmail(request.getEmail())) {
			throw new AlreadyExistsException(
					AlreadyExistsException.APP_USER_EMAIL, request.getEmail());
		}

		Department userDept = deptRepository.findById(request.getDepartment())
				.orElseThrow(() -> new DoesNotExistException(
						DoesNotExistException.DEPARTMENT, request.getDepartment()));

		Title userTitle = titleRepository.findById(request.getTitle())
				.orElseThrow(() -> new DoesNotExistException(
						DoesNotExistException.TITLE, request.getTitle(), userDept.getName()));

		AppUser newUser = AppUser.builder()
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.title(userTitle)
				.department(titleRepository.findById(request.getTitle()).get().getDepartment())
				.build();

		if (request.getManagerId() != null) {
			UUID managerId = request.getManagerId();
			AppUser manager = appUserRepository.findById(managerId)
					.orElseThrow(() -> new DoesNotExistException(
							DoesNotExistException.APP_USER_ID, managerId));

			newUser.setManager(manager);
		}

		appUserRepository.save(newUser);
		String jwtToken = jwtService.generateToken(newUser);
		jwtService.saveTokenInRedis(request.getEmail(), jwtToken);
		return new AuthenticationResponse(jwtToken);
	}

	// TODO: Review Exception
	public AuthenticationResponse login(AuthenticationRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()));
		} catch (AuthenticationException e) {
			throw new InvalidCredentialsException();
		}

		AppUser user = appUserRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new DoesNotExistException(
						DoesNotExistException.APP_USER_EMAIL, request.getEmail()));
		String jwtToken = jwtService.generateToken(user);
		jwtService.saveTokenInRedis(request.getEmail(), jwtToken);
		return new AuthenticationResponse(jwtToken);
	}

}
