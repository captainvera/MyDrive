package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;
import pt.tecnico.myDrive.exceptions.FileUnknownException;

public class Directory extends Directory_Base {

  public Directory() {
    super();
  }

  /**
   * A top-level directory is a directory which its parent is itself.
   * An example of a top-level directory would be the root directory.
   *
   * @return true if the directory is a top-level directory.
   */
  public boolean isTopLevelDirectory() { return getParent() == this; }

  /**
   * The path of a directory is a string that specifies how to reach itself by
   * going through other directories in a filesystem.
   *
   * @return The string corresponding to the path the directory.
   */
  public String getPath() { return isTopLevelDirectory() ? getName() : getPathHelper(); }

  /**
   * Simple helper function to call when the path needs to be processed
   */
  private String getPathHelper() {
    return (isTopLevelDirectory()) ? "" : getParent().getPath();
  }

  /**
   * Get a file inside the directory by its name.
   *
   * @param filename
   * @return The file which name is filename.
   * @throws FileUnknownException
   */
  public File getFileByName(String filename) throws FileUnknownException {
    for (File file : getFileSet())
      if (filename.equals(file.getName()))
        return file;
    throw new FileUnknownException(filename);
  }

  @Override
  public void remove() { }
	/**
	 * TEMPORARY
	 */
	public int getSize(){
		return 0;
	}

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

}
