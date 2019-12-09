package com.jonamiller.flactoaac.exceptions;

public class NonExistenceException extends Exception {
  private static final long serialVersionUID = 1L;

  public NonExistenceException(String message) {
    super(message);
  }
}
