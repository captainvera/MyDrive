package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user attempts to remove the "."
 * or ".." directories.
 */
public class IllegalRemovalException extends Exception {

  public IllegalRemovalException() {
    super("Cannot remove '.' or '..' directories");
  }
}

