package com.jonamiller.flactoaac.exceptions;

public class WrongTypeException extends Exception {
  private static final long serialVersionUID = 1L;

  public WrongTypeException(String message) {
    super(message);
  }
}