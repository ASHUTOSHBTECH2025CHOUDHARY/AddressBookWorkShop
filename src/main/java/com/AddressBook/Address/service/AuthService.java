package com.AddressBook.Address.service;

import com.AddressBook.Address.dto.ResetPasswordDTO;
import com.AddressBook.Address.dto.UserDTO;
import com.AddressBook.Address.dto.LoginDTO;
import com.AddressBook.Address.model.User;
import com.AddressBook.Address.repository.UserRepository;
import com.AddressBook.Address.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.user.routing.key}")
    private String userRoutingKey;
    public String registerUser(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            return "Email is already registered!";
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        // Publish user registration event
        rabbitTemplate.convertAndSend(exchange, userRoutingKey, userDTO);
        System.out.println("📨 Sent User Registration Event to RabbitMQ: " + userDTO);

        return "User registered successfully!";
    }


    public String loginUser(LoginDTO loginDTO) {
        Optional<User> user = userRepository.findByEmail(loginDTO.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            return jwtUtil.generateToken(loginDTO.getEmail());
        }
        return "Invalid email or password!";
    }

    public String forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User with this email does not exist!";
        }

        String resetToken = jwtUtil.generateToken(email);  // Generate reset token
        emailService.sendResetEmail(email, resetToken);    // Send reset email

        return "Password reset email sent successfully!";
    }

    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String email = jwtUtil.extractUsername(resetPasswordDTO.getToken());

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "Invalid or expired token!";
        }

        User existingUser = user.get();
        existingUser.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(existingUser);

        return "Password reset successful!";
    }

}
