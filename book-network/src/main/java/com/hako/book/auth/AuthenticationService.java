package com.hako.book.auth;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface AuthenticationService {
  void register(RegistrationRequest request) throws MessagingException;
  AuthenticaionResponse authenticate(@Valid AuthenticationRequest request);
  void activateAccount(String token) throws MessagingException;
}
