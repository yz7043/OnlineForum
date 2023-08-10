package org.example.controller;

import org.example.domain.User;
import org.example.dto.response.GeneralResponse;
import org.example.dto.response.StatusResponse;
import org.example.security.JwtProvider;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @WithMockUser(username = "test", authorities = { "super_admin_user" })
    void testActivateUserByUserEmail() throws  Exception{
        String email = "test@gmail.com";
        Mockito.when(userService.activateUserByUserEmail(Mockito.eq(email))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/email")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.statusCode").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.message").value("User status is changed successfully."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
        Mockito.verify(userService, Mockito.times(1)).activateUserByUserEmail(Mockito.eq(email));
        String existedEmail = "test1@gmail.com";
        Mockito.when(userService.activateUserByUserEmail(Mockito.eq(email))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/email")
                        .param("email", existedEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.message").value("User status is changed successfully."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(false));
        Mockito.verify(userService, Mockito.times(1)).activateUserByUserEmail(Mockito.eq(email));

    }

    @Test
    @WithMockUser(username = "test@gmail.com", authorities = {"user"})
    void testGetUserByEmail() throws Exception {
        String email = "test@gmail.com";
        int userId = 1;
        String firstName = "John";
        String lastName = "Doe";
        String password = "testPassword";
        int active = 1;
        String dateJoined = "2023-07-23";
        String type = "normal";
        String profileImageUrl = "http://example.com/profile.jpg";
        // Mock the returned user
        User mockUser = createTestUser();

        Mockito.when(userService.getUserUsingUsername(email)).thenReturn(mockUser);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/email/" + email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.statusCode").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.message").value("User is obtained by email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user_id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstname").value(firstName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastname").value(lastName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.password").value(password))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.active").value(active))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateJoined").value(dateJoined))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type").value(type))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.profile_image_url").value(profileImageUrl))
                .andReturn();
        Mockito.verify(userService, Mockito.times(1)).getUserUsingUsername(Mockito.eq(email));
    }
    @Test
    @WithMockUser(username = "test")
    void testGetUserByEmail_Unauthorized() throws Exception {
        String email = "test@gmail.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/user/email/" + email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // expect 403 Forbidden status
    }

    @Test
    @WithMockUser(username = "test@gmail.com", authorities = {"user"})
    void testGetUserByUserId() throws Exception {
        User user = createTestUser();
        Mockito.when(userService.getUserByUserID(user.getUser_id())).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + user.getUser_id())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.OK.value()));


    }

    private User createTestUser(){
        String email = "test@gmail.com";
        int userId = 1;
        String firstName = "John";
        String lastName = "Doe";
        String password = "testPassword";
        int active = 1;
        String dateJoined = "2023-07-23";
        String type = "normal";
        String profileImageUrl = "http://example.com/profile.jpg";
        // Mock the returned user
        User mockUser = new User();
        mockUser.setUser_id(userId);
        mockUser.setFirstname(firstName);
        mockUser.setLastname(lastName);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setActive(active);
        mockUser.setDateJoined(dateJoined);
        mockUser.setType(type);
        mockUser.setProfile_image_url(profileImageUrl);
        return mockUser;
    }

}
