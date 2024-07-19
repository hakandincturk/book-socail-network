package com.hako.book.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hako.book.email.EmailService;
import com.hako.book.email.EmailTemplateName;
import com.hako.book.role.Role;
import com.hako.book.role.RoleRepository;
import com.hako.book.security.JwtService;
import com.hako.book.user.Token;
import com.hako.book.user.TokenRepository;
import com.hako.book.user.User;
import com.hako.book.user.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Value("${application.mailing.activation-url}")
  private String activationUrl;

  public void register(RegistrationRequest request) throws MessagingException {
    Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new IllegalStateException("Role user was not set"));
    User user = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .password(this.passwordEncoder.encode(request.getPassword()))
        .accountLocked(false)
        .enabled(false)
        .roles(List.of(userRole))
        .build();

    userRepository.save(user);
    sendValidationEmail(user);
  }

  private void sendValidationEmail(User user) throws MessagingException {
    String newToken = generateAndSaveActivationToken(user);
    // send email
    emailService.sendEmail(
      user.getEmail(),
      user.getFullName(), 
      EmailTemplateName.ACTIVATE_ACCOUNT,
      activationUrl, 
      newToken, 
      "Account Activation"
    );
  }

  private String generateAndSaveActivationToken(User user) {
    String generatedToken = generateActivationCode(6);
    Token token = Token.builder()
        .token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusMinutes(15))
        .user(user)
        .build();

    tokenRepository.save(token);
    return generatedToken;
  }

  private String generateActivationCode(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int randomIndex = secureRandom.nextInt(characters.length()); // 0..9
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }

  @Override
  public AuthenticaionResponse authenticate(@Valid AuthenticationRequest request) {
    Authentication auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    User user = ((User)auth.getPrincipal());

    HashMap<String, Object> claims = new HashMap<>();
    claims.put("fullName", user.getFullName());
    
    String jwtToken = jwtService.generateToken(claims, user);
    return AuthenticaionResponse.builder().token(jwtToken).build();
  }

  @Override
  // @Transactional
  public void activateAccount(String token) throws MessagingException {
    Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

    if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      sendValidationEmail(savedToken.getUser());
      throw new RuntimeException("Token expired, new token sent to your email");
    }

    User user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));
    user.setEnabled(true);
    userRepository.save(user);

    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }
}
