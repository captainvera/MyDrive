package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever a password entered for a user is incorrect.
 */
public class WrongPasswordException extends Exception {

  public WrongPasswordException() {
    super("Wrong password");
  }
}
