package booking.service.impl.user;

import booking.dto.request.ChangePasswordDto;
import booking.dto.request.ForgotPasswordDto;
import booking.dto.request.LoginDto;
import booking.dto.request.RegisterDto;
import booking.dto.response.UserResponse;
import booking.dto.response.UserResponseDto;
import booking.entity.Role;
import booking.entity.User;
import booking.entity.Verification;
import booking.exception.*;
import booking.repository.RoleRepository;
import booking.repository.UserRepository;
import booking.security.jwt.JwtProvider;
import booking.security.user_principle.UserPrincipal;
import booking.service.impl.mail.MailService;
import booking.service.impl.user.IUserService;
import booking.service.impl.verification.VerificationService;
import booking.service.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationProvider authenticationProvider;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final VerificationService verificationService;

    @Override
    public Page<UserResponseDto> findAll(String username, int page, int size, String field, String by) {
        Sort sort = Sort.by(field);

        if (by.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Role role = roleRepository.findByRoleName("ADMIN");
        Page<User>  users = userRepository.findAllByRoles(username,role, PageRequest.of(page, size).withSort(sort));
        return users.map(userMapper::toResponse);
    }

    @Override
    public String register(RegisterDto registerDto) throws RegisterException {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RegisterException("User is exits");
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RegisterException("Email is exits");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByRoleName("USER"));
        User user = User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .status(true)
                .roles(roles).build();
        userRepository.save(user);
        String emailContent = "<p style=\"color: red; font-size: 18px;\">\n" +
                "Registered successfully</p>";
        mailService.sendMail(registerDto.getEmail(), "RegisterSuccess", emailContent);
        return "Register successfully";
    }

    @Override
    public UserResponse login(LoginDto loginDto) throws LoginException {
        Authentication authentication;
        try {
            authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        } catch (AuthenticationException ex) {
            throw new LoginException("username or password invalid");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal.getUser().isStatus() == false){
            throw new LoginException("Account is locked");
        }
        String token = jwtProvider.generateToken(userPrincipal);
        UserResponse userResponse = UserResponse.builder()
                .id(userPrincipal.getUser().getId())
                .username(userPrincipal.getUser().getUsername())
                .email(userPrincipal.getUser().getEmail())
                .token(token)
                .roles(userPrincipal.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toSet()))
                .build();
        return userResponse;
    }


    @Override
    public String changePassword(ChangePasswordDto changePasswordDto,Authentication authentication) throws LoginException, ExistsException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal== null){
            throw new LoginException("You need to log in to use the service");
        }
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword())){
            throw new ExistsException("The new password cannot be the same as the old password");
        }
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())){
            throw new ExistsException("Confirm password was wrong");
        }
        User user = userPrincipal.getUser();
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        return "Change Password Successfully";
    }


    @Override
    public String forgotPassword(ForgotPasswordDto forgotPasswordDto) throws LoginException, InvalidException, OutOfDateException {
        User user = userRepository.findByEmail(forgotPasswordDto.getEmail());
        if (user == null) {
            throw new LoginException("Email address is incorrect");
        }
        List<Verification> verifications = verificationService.getVerifications(user);
        if (verifications.isEmpty()) {
            throw new InvalidException("Verification code not found");
        }
        if (!verifications.get(verifications.size() - 1).getVerificationCode().equals(forgotPasswordDto.getVerificationCode())) {
            throw new InvalidException("Verification code incorrect");
        }
        if (verificationService.isExpired(verifications.get(verifications.size()-1).getCreatAt())) {
            throw new OutOfDateException("Verification code expired");
        }
        if(!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmNewPassword())){
            throw new InvalidException("Confirm new password incorrect");
        }
        user.setPassword(passwordEncoder.encode(forgotPasswordDto.getNewPassword()));
        userRepository.save(user);
        return "Password recovery successful";
    }

    @Override
    public String sendVerification(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String verification = verificationService.create(user).getVerificationCode();
            String content = "Hello " + email + ",\n\n" +
                    "For security reasons, you are required to use the following One Time Password to log in:\n" +
                    "\n" + verification +
                    "\n\nNote: This OTP is set to expire in 5 minutes.\n\n" +
                    "If you did not request this password reset, please contact us immediately at support@example.com.";
            mailService.sendMail(email, "Verification", content);

            return "Log in to gmail to get the confirmation code";
        }
        return "Email address incorrect";
    }

    public  boolean hasAdminRole(User user) {
        if (user != null && user.getRoles() != null) {
            return user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getRoleName()));
        }
        return false;
    }

    public User getUser(Authentication authentication) throws LoginException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal==null){
            throw new LoginException("You need to login to use service");
        }
       return  userPrincipal.getUser();
    }

    @Override
    public String changStatus(Long id) throws NotFoundException {
        User user = userRepository.findById(id).get();
        if (user == null) {
            throw new NotFoundException("User " + id + " not found");
        }
        user.setStatus(!user.isStatus());
        userRepository.save(user);
        return "Successfully";
    }
}
