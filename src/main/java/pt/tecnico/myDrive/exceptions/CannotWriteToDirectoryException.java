package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user attempts to write to a directory 
 */
public class CannotWriteToDirectoryException extends Exception {


  public CannotWriteToDirectoryException() {
    super("Cannot write to a Directory");
  }
}
