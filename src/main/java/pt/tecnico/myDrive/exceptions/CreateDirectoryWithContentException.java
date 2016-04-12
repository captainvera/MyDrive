package pt.tecnico.myDrive.exceptions;

/**
 * This exception is thrown whenever the logged user attempts to create a directory with
 * content
 */
public class CreateDirectoryWithContentException extends Exception {

  public CreateDirectoryWithContentException() {
    super("Cannot create directory with content");
  }
}
