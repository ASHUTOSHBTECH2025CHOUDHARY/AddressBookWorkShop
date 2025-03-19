package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.ResetPasswordDTO;
import com.AddressBook.Address.dto.UserDTO;
import com.AddressBook.Address.dto.LoginDTO;
import com.AddressBook.Address.exception.UserAlreadyExistsException;
import com.AddressBook.Address.exception.UserNotFoundException;
import com.AddressBook.Address.model.User;
import com.AddressBook.Address.repository.UserRepository;
import com.AddressBook.Address.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ModelMapper modelMapper;

    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        rabbitTemplate.convertAndSend("AddressBookExchange", "userKey", userDTO.getEmail());

        return "User registered successfully!";
    }

    public String loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password!"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid email or password!");
        }

        return jwtUtil.generateToken(loginDTO.getEmail());
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with this email does not exist!"));

        String resetToken = jwtUtil.generateToken(email);
        rabbitTemplate.convertAndSend("AddressBookExchange", "userKey", email);

        return "Password reset email sent successfully!";
    }

    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String email = jwtUtil.extractUsername(resetPasswordDTO.getToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired token!"));

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        return "Password reset successful!";
    }
}
