package com.hako.book.handler;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public enum BusinessErrorCodes {

  NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code was set"),
  INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Incorrect current password"),
  NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "New password does not match"),
  ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"),
  ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "User account is disabled"),
  BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login and / or password are incorrect"),

  ;
  @Getter
  private final Integer errorCode;

  @Getter
  private final String description;
  @Getter
  private final HttpStatus httpStatus;

  private BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
      this.errorCode = code;
      this.description = description;
      this.httpStatus = httpStatus;
  }
}
