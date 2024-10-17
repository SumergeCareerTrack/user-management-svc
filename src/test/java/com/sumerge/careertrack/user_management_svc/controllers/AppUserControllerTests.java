package com.sumerge.careertrack.user_management_svc.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumerge.careertrack.user_management_svc.controllers.AppUserController;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserResponseDTO;
import com.sumerge.careertrack.user_management_svc.services.AppUserService;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest(controllers = AppUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = AppUserController.class)
@ComponentScan("com.sumerge.careertrack.user_management_svc.exceptions")
public class AppUserControllerTests {

        @MockBean
        private AppUserService appUserService;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        Department mockDept;
        Title mockTitle;
        AppUserRequestDTO mockUserRequest;
        AppUserResponseDTO mockUserResponse;
        private UUID existingUserId;
        private UUID nonExistingUserId;

        private AppUserRequestDTO createMockUserRequest(Department department, Title title) {
                return AppUserRequestDTO.builder()
                        .id(existingUserId)
                        .email("email@sumerge.com")
                        .firstName("FN")
                        .lastName("LN")
                        .departmentId(department.getId())
                        .titleId(title.getId())
                        .build();
        }

        private AppUserResponseDTO createMockUserResponse(AppUserRequestDTO userRequest, Department department, Title title) {
                return AppUserResponseDTO.builder()
                        .id(userRequest.getId())
                        .email(userRequest.getEmail())
                        .firstName(userRequest.getFirstName())
                        .lastName(userRequest.getLastName())
                        .department(department)
                        .title(title)
                        .build();
        }
        @BeforeEach
        public void setup() {
                existingUserId = UUID.randomUUID();
                nonExistingUserId = UUID.randomUUID();
                mockDept = new Department(UUID.randomUUID(), "SE");

                mockTitle = new Title(UUID.randomUUID(), mockDept, "ASE", false);

                mockUserRequest = createMockUserRequest(mockDept, mockTitle);
                mockUserResponse = createMockUserResponse(mockUserRequest, mockDept, mockTitle);
        }
        @Test
        public void getAllUsers_NoUsers_ReturnEmpty() throws Exception {
                when(appUserService.getAll()).thenReturn(Arrays.asList());

                ResultActions response = mockMvc.perform(get("/users/"));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));

                verify(appUserService, times(1)).getAll();
        }
        @Test
        public void getAllUsers_Users_OK() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList(mockUserResponse, mockUserResponse);

                when(appUserService.getAll()).thenReturn(responseList);

                ResultActions response = mockMvc.perform(get("/users/"));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(responseList)));

                verify(appUserService, times(1)).getAll();
        }

        @Test
        public void getBatch_ValidIds_ReturnsUserResponses() throws Exception {

                List<UUID> ids = List.of(existingUserId);
                when(appUserService.getBatch(ids)).thenReturn(List.of(mockUserResponse));

                mockMvc.perform(MockMvcRequestBuilders.post("/users/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ids)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].id").value(existingUserId.toString()))
                        .andExpect(jsonPath("$[0].email").value("email@sumerge.com"))
                        .andExpect(jsonPath("$[0].firstName").value("FN"))
                        .andExpect(jsonPath("$[0].lastName").value("LN"));


        }
        @Test
        public void getBatch_EmptyIds_ReturnsEmptyList() throws Exception {
                when(appUserService.getBatch(anyList())).thenReturn(Collections.emptyList());

                mockMvc.perform(MockMvcRequestBuilders.post("/users/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Collections.emptyList().toString()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        public void getManagersByDept_DeptExists_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList(mockUserResponse);

                when(appUserService.getManagersByDept(anyString())).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/managers?departmentName=" + mockDept.getName()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(responseList)));

                verify(appUserService, times(1)).getManagersByDept(mockDept.getName());
        }

        @Test
        public void getManagersByDept_NoManagers_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList();

                when(appUserService.getManagersByDept(anyString())).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/managers?departmentName=" + mockDept.getName()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));

                verify(appUserService, times(1)).getManagersByDept(mockDept.getName());
        }

        @Test
        public void getManagersByDept_DeptDoesNotExist_Throws() throws Exception {
                when(appUserService.getManagersByDept(anyString()))
                                .thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(
                                get("/users/managers?departmentName=" + mockDept.getName()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).getManagersByDept(mockDept.getName());
        }

        @Test
        public void getUserById_UserExists_Ok() throws Exception {
                when(appUserService.getById(any(UUID.class)))
                                .thenReturn(mockUserResponse);

                ResultActions response = mockMvc.perform(get("/users/{id}",
                                mockUserRequest.getId()));

                response.andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(mockUserResponse)));

                verify(appUserService, times(1)).getById(mockUserRequest.getId());
        }

        @Test
        public void getUserById_DoesNotExist_BadRequest() throws Exception {

                when(appUserService.getById(any(UUID.class))).thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(get("/users/{id}",
                                mockUserRequest.getId()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).getById(mockUserRequest.getId());
        }

        @Test
        public void getUserByEmail_UserExists_Ok() throws Exception {
                when(appUserService.getByEmail(anyString()))
                                .thenReturn(mockUserResponse);

                ResultActions response = mockMvc.perform(get("/users/email/{email}",
                                mockUserRequest.getEmail()));

                response.andDo(MockMvcResultHandlers.print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(mockUserResponse)));

                verify(appUserService, times(1)).getByEmail(mockUserRequest.getEmail());
        }

        @Test
        public void getUserByEmail_DoesNotExist_BadRequest() throws Exception {

                when(appUserService.getByEmail(anyString())).thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(get("/users/email/{email}",
                                mockUserRequest.getEmail()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).getByEmail(mockUserRequest.getEmail());
        }

        @Test
        public void getAllUsersByTitle_TitleExists_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList(mockUserResponse);

                when(appUserService.getAllByTitle(anyString())).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/title/{titleName}", mockTitle.getName()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(responseList)));

                verify(appUserService, times(1)).getAllByTitle(mockTitle.getName());
        }

        @Test
        public void getAllUsersByTitle_NoTitleHolders_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList();

                when(appUserService.getAllByTitle(anyString())).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/title/{titleName}", mockTitle.getName()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));

                verify(appUserService, times(1)).getAllByTitle(mockTitle.getName());
        }

        @Test
        public void getAllUsersByTitle_TitleDoesNotExist_Throws() throws Exception {
                when(appUserService.getAllByTitle(anyString()))
                                .thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(
                                get("/users/title/{titleName}", mockTitle.getName()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).getAllByTitle(mockTitle.getName());
        }

        @Test
        public void getSubordinates_SubordinatesExists_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList(mockUserResponse);

                when(appUserService.getSubordinates(any(UUID.class))).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/{userId}/subordinates", mockUserRequest.getId()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(responseList)));

                verify(appUserService, times(1)).getSubordinates(mockUserRequest.getId());
        }

        @Test
        public void getSubordinates_NoSubordinates_Ok() throws Exception {
                List<AppUserResponseDTO> responseList = Arrays.asList();

                when(appUserService.getSubordinates(any(UUID.class))).thenReturn(responseList);

                ResultActions response = mockMvc.perform(
                                get("/users/{userId}/subordinates", mockUserRequest.getId()));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("[]"));

                verify(appUserService, times(1)).getSubordinates(mockUserRequest.getId());
        }

        @Test
        public void getSubordinates_UserDoesNotExist_Throws() throws Exception {
                when(appUserService.getSubordinates(any(UUID.class)))
                                .thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(
                                get("/users/{userId}/subordinates", mockUserRequest.getId()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).getSubordinates(mockUserRequest.getId());
        }

        @Test
        public void updateUser_UserExists_Ok() throws Exception {
                when(appUserService.updateUser(any(AppUserRequestDTO.class)))
                                .thenReturn(mockUserResponse);

                ResultActions response = mockMvc.perform(put("/users/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserRequest)));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(
                                                objectMapper.writeValueAsString(mockUserResponse)));

                verify(appUserService, times(1)).updateUser(mockUserRequest);
        }

        @Test
        public void updateUser_UserDoesNotExist_Throws() throws Exception {
                when(appUserService.updateUser(any(AppUserRequestDTO.class)))
                                .thenThrow(DoesNotExistException.class);

                ResultActions response = mockMvc.perform(put("/users/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserRequest)));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).updateUser(mockUserRequest);
        }

        @Test
        public void deleteUser_UserExists_Ok() throws Exception {
                ResultActions response = mockMvc.perform(
                                delete("/users/{userId}", mockUserRequest.getId()));

                response.andExpect(status().isOk());

                verify(appUserService, times(1)).deleteUser(mockUserRequest.getId());
        }

        @Test
        public void deleteUser_UserDoesNotExist_Throws() throws Exception {
                doThrow(new DoesNotExistException())
                                .when(appUserService)
                                .deleteUser(any(UUID.class));

                ResultActions response = mockMvc.perform(
                                delete("/users/{userId}", mockUserRequest.getId()));

                response.andExpect(status().isBadRequest());

                verify(appUserService, times(1)).deleteUser(mockUserRequest.getId());
        }

}