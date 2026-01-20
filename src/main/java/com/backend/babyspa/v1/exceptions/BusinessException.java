package com.backend.babyspa.v1.exceptions;

public class BusinessException extends RuntimeException {
  public BusinessException() {
    super();
  }

  public BusinessException(String message) {
    super(message);
  }
}
