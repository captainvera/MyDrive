package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user hasn't sufficient
 * permissions to perform the desired operation.
 */
public class MethodDeniedException extends RuntimeException {

  public MethodDeniedException() {
    super("You don't have permission to use this method!");
  }
}


