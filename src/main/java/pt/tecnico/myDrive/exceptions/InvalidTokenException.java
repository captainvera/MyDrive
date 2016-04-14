package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an
 * operation with an invalid token
 */
public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException() {
    super("Invalid token");
  }
}
