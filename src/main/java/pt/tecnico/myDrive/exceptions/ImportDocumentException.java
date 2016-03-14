package pt.tecnico.myDrive.exceptions;
/**
 * This exception is thrown whenever the XML document to import has errors
 */

public class ImportDocumentException extends Exception {

  /** The existing XML document's name */
  private final String _filename;

  public ImportDocumentException(String filename) {
    _filename = filename;
  }

  public String getFileName() {return _filename;}

  @Override
  public String getMessage() {
    return "XML this" + getFileName() + "has errors";
  }
}
