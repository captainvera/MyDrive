package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever an attempt is made to perform an operation
 * that requires a non existent user, but the user already exists.
 */
public class InvalidUsernameException extends Exception {

  /** The user's username. */
  private final String _username;

  /**
   * @param username the user's username.
   */
  public InvalidUsernameException(String username) {
    _username = username;
  }

  /**
   * @return Returns the user's username.
   */
  public String getUsername() { return _username; }

  /**
   * @return Returns the detailed message of this throwable.
   */
  @Override
  public String getMessage() {
    return "Username '" + getUsername() + "' is invalid";
  }
}

