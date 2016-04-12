package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an
 * operation with an invalid token
 */
public class InvalidTokenException extends Exception {

  public InsufficientPermissionsException() {
    super("Invalid token");
  }
}


