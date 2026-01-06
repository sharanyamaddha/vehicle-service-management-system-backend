package com.userservice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.userservice.exceptions.EmailAlreadyExistsException;
import com.userservice.exceptions.InvalidCredentialsException;
import com.userservice.model.User;
import com.userservice.model.enums.Role;
import com.userservice.repository.UserRepository;
import com.userservice.requestdto.ChangePasswordRequest;
import com.userservice.requestdto.LoginRequest;
import com.userservice.requestdto.RegisterRequest;
import com.userservice.requestdto.UpdateProfileRequest;
import com.userservice.responsedto.LoginResponse;
import com.userservice.responsedto.UserResponse;
import com.userservice.security.JwtUtil;
import com.userservice.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.userservice.config.RabbitConfig;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public LoginResponse registerCustomer(RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("Email already exists");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setPasswordSet(true); // Self-registered

        userRepository.save(user);

        // Auto-login logic
        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getId());
    }

    @Override
    public String createInternalUser(RegisterRequest req) {

        if (req.getRole() == Role.CUSTOMER)
            throw new RuntimeException("Use public registration for customers");

        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("Email already exists");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());

        // Invite logic
        String token = UUID.randomUUID().toString();
        user.setInviteToken(token);
        user.setInviteExpiry(LocalDateTime.now().plusHours(48));
        user.setPasswordSet(false);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // placeholder

        user.setRole(req.getRole());
        user.setActive(true);

        userRepository.save(user);

        // Send Invite Mail
        String inviteLink = "http://localhost:4200/activate?token=" + token;

        Map<String, Object> emailData = Map.of(
                "username", user.getUsername(),
                "link", inviteLink,
                "role", user.getRole().toString());

        Map<String, Object> payload = Map.of(
                "type", "USER_INVITE",
                "email", user.getEmail(),
                "data", emailData);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, payload);

        return "Internal user invited. Email sent.";
    }

    @Override
    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Invalid credentials");

        if (!user.isActive())
            throw new RuntimeException("Account disabled");

        if ((user.getRole() == Role.MANAGER || user.getRole() == Role.TECHNICIAN) && !user.isPasswordSet())
            throw new RuntimeException("Account not activated. Please check your email for invite.");

        String token = jwtUtil.generateToken(user);
        return new LoginResponse(token, user.getId()
        // user.getUsername(),user.getEmail(),user.getRole().name()
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    public UserResponse getUserById(String id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return map(user);
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role))
                .stream().map(this::map).toList();
    }

    @Override
    public void disableUser(String id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setActive(false);
        userRepository.save(u);
    }

    @Override
    public void enableUser(String id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setActive(true);
        userRepository.save(u);
    }

    @Override
    public List<UserResponse> getDisabledUsers() {
        return userRepository.findByActiveFalse()
                .stream().map(this::map).toList();
    }

    @Override
    public UserResponse getMyProfile(String token) {
        User user = extractUserFromToken(token);
        return map(user);
    }

    @Override
    public void updateMyProfile(String token, UpdateProfileRequest req) {
        User user = extractUserFromToken(token);
        user.setUsername(req.getUsername());
        userRepository.save(user);
    }

    @Override
    public void changeMyPassword(String token, ChangePasswordRequest req) {
        User user = extractUserFromToken(token);
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword()))
            throw new RuntimeException("Old password wrong");
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode("Welcome@123"));
        user.setPasswordSet(true);
        userRepository.save(user);
    }

    @Override
    public String activateAccount(String token, String password) {
        User user = userRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired invite token"));

        if (user.getInviteExpiry().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Invite token expired");

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordSet(true);
        user.setInviteToken(null);
        user.setInviteExpiry(null);

        userRepository.save(user);
        return "Account activated successfully";
    }

    @Override
    public List<UserResponse> getInvitedUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isPasswordSet() && u.getInviteToken() != null)
                .map(this::map)
                .toList();
    }

    @Override
    public void resendInvite(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isPasswordSet() && user.getInviteToken() == null) {
            throw new RuntimeException("User is already active. Cannot resend invite.");
        }

        // Regenerate Token
        String token = UUID.randomUUID().toString();
        user.setInviteToken(token);
        user.setInviteExpiry(LocalDateTime.now().plusHours(48));

        userRepository.save(user);

        // Send Email
        String inviteLink = "http://localhost:4200/activate?token=" + token;

        Map<String, Object> emailData = Map.of(
                "username", user.getUsername(),
                "link", inviteLink,
                "role", user.getRole().toString());

        Map<String, Object> payload = Map.of(
                "type", "USER_INVITE",
                "email", user.getEmail(),
                "data", emailData);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, payload);
    }

    @Autowired
    private com.userservice.repository.TechnicianRepository technicianRepo;

    private UserResponse map(User u) {
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        r.setActive(u.isActive());
        // If inviteToken is null, it means user is either legacy or fully activated
        r.setPasswordSet(u.isPasswordSet() || u.getInviteToken() == null);

        if (u.getRole() == Role.TECHNICIAN) {
            technicianRepo.findByUserId(u.getId())
                    .ifPresent(t -> {
                        r.setSpecialization(t.getSpecialization().toString());
                        r.setWorkload(t.getCurrentJobs());
                        r.setMaxCapacity(t.getMaxDailyCapacity());
                        r.setAvailable(t.isAvailable());
                    });
        }

        return r;
    }

    private User extractUserFromToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        String token = authHeader.substring(7);

        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive())
            throw new RuntimeException("User account is disabled");

        return user;
    }

}
