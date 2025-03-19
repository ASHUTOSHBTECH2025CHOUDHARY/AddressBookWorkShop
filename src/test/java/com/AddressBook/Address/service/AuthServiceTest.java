package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.LoginDTO;
import com.AddressBook.Address.dto.ResetPasswordDTO;
import com.AddressBook.Address.dto.UserDTO;
import com.AddressBook.Address.exception.UserAlreadyExistsException;
import com.AddressBook.Address.exception.UserNotFoundException;
import com.AddressBook.Address.model.User;
import com.AddressBook.Address.repository.UserRepository;
import com.AddressBook.Address.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        String result = authService.registerUser(userDTO);

        assertTrue(result.contains("User registered successfully!"));
        verify(userRepository, times(1)).save(user);
        verify(rabbitTemplate, times(1)).convertAndSend("AddressBookExchange", "userKey", userDTO.getEmail());
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(userDTO));
    }

    @Test
    void testLoginUser_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(loginDTO.getEmail())).thenReturn("jwtToken");

        String result = authService.loginUser(loginDTO);

        assertTrue(result.equals("jwtToken"));
    }

    @Test
    void testLoginUser_InvalidCredentials() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> authService.loginUser(loginDTO));
    }

    @Test
    void testForgotPassword_Success() {

        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(email)).thenReturn("resetToken");

        String result = authService.forgotPassword(email);


        assertTrue(result.contains("Password reset email sent successfully!"));
        verify(rabbitTemplate, times(1)).convertAndSend("AddressBookExchange", "userKey", email);
    }

    @Test
    void testForgotPassword_UserNotFound() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.forgotPassword(email));
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken("validToken");
        resetPasswordDTO.setNewPassword("newPassword");

        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(jwtUtil.extractUsername(resetPasswordDTO.getToken())).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetPasswordDTO.getNewPassword())).thenReturn("encodedNewPassword");

        String result = authService.resetPassword(resetPasswordDTO);

        assertTrue(result.contains("Password reset successful!"));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testResetPassword_InvalidToken() {

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken("invalidToken");

        when(jwtUtil.extractUsername(resetPasswordDTO.getToken())).thenReturn("invalidEmail");
        when(userRepository.findByEmail("invalidEmail")).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> authService.resetPassword(resetPasswordDTO));
    }
}