package com.hako.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

  @NotEmpty(message = "First name is required")
  @NotBlank(message = "First name is required")
  private String firstName;

  @NotEmpty(message = "Last name is required")
  @NotBlank(message = "Last name is required")
  private String lastName;

  @Email(message = "Email should be valid")
  @NotEmpty(message = "Email is required")
  @NotBlank(message = "Email is required")
  private String email;

  @NotEmpty(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min = 3, message = "Password should be at least 6 characters")
  private String password;
}
