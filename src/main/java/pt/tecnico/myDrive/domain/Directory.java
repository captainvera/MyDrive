package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.FileUnknownException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class Directory extends Directory_Base {

  public Directory(String name, Directory parent, Integer id) {
    super();
		init(name, parent, id);
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
  @Override
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

  /**
   * @return Lists the files inside the directory using only their name.
   */
  public String listFilesSimple()
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return listFilesGeneric(this.getClass().getMethod("getName"));
  }

  /**
   * @return List of the files inside the directory using their toString method.
   */
  public String listFilesAll()
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return listFilesGeneric (this.getClass().getMethod("toString"));
  }

  /**
   * List files in a generic way.
   * The way the listing is done is by applying method to the files, hence,
   * the list will be of the form:
   *
   * apply(method, file1)
   * apply(method, file2)
   * ...
   * apply(method, fileN)
   *
   * @param method
   * @return A list containing the description given my method
   * of the files inside the directory.
   */
  private String listFilesGeneric (Method method)
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    /**
     * Replaces the occurence of this directory and parent directories' names
     * respectively for "." and ".."
     */
    String self = ((String) method.invoke(this)).replaceAll(getName(), ".") + "\n";
    String parent = ((String) method.invoke(getParent())).replaceAll(getParent().getName(), "..") + "\n";
    String list = self + parent;
    for (File file: getFileSet())
      list += method.invoke(file) + "\n";
    return list;
  }

  /**
   * The size of a directory is given by the number of files inside it.
   *
   * @return The size of a directory.
   */
  @Override
  public int getSize() { return 2 + getFileSet().size(); }

  @Override
  public void remove() {
    for (File file : getFileSet())
      file.remove();
    setUser(null);
    setOwner(null);
    setParent(null);
    deleteDomainObject();
  }

	@Override
	public void execute(){
	}

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

}
