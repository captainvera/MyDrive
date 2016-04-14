package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user hasn't sufficient
 * permissions to perform the desired operation.
 */
public class InsufficientPermissionsException extends RuntimeException {

  public InsufficientPermissionsException() {
    super("Permission denied");
  }
}


