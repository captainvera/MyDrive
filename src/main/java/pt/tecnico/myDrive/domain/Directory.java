package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;
import pt.tecnico.myDrive.visitors.DirectoryVisitor;

import pt.tecnico.myDrive.exceptions.FileExistsException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.IllegalRemovalException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;

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
   * Get a file by its path.
   *
   * @param path
   * @return The file at the end of the path.
   * @throws FileUnknownException
   */
  public File getFileByPath(String path) throws FileUnknownException {
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];
    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    Directory currentDir = getFileByPathHelper(tokensList);

    return currentDir.getFileByName(target);
  }

  /**
   * Simple helper function to call when the path needs to be processed
   */
  private Directory getFileByPathHelper(ArrayList<String> tokens) throws FileUnknownException {
    DirectoryVisitor visitor = new DirectoryVisitor();
    Directory currentDir = getFileByName(tokens.get(0)).accept(visitor);
    tokens.remove(0);
    return (tokens.size() == 0) ? this : currentDir.getFileByPathHelper(tokens);
  }

  /**
   * remove a file by its path.
   *
   * @param path
   * @throws FileUnknownException
   */
  public void removeFileByPath(String path) throws FileUnknownException {
    File file = getFileByPath(path);
    Directory parent = file.getParent();
    parent.removeFile(file);
    file.remove();
  }

  /**
   * List file content from a given path
   *
   */
  public void listFileByPath(String path) throws FileUnknownException {
    /*TO DO*/
  }

  /**
   * Helper function to call when the directories in the path need to be processed/created
   */
  private Directory createFileByPathHelper(ArrayList<String> tokens) {
    try {
      DirectoryVisitor visitor = new DirectoryVisitor();
      Directory currentDir = getFileByName(tokens.get(0)).accept(visitor);
      tokens.remove(0);
      return (tokens.size() == 0) ? this : currentDir.createFileByPathHelper(tokens);
    } catch (FileUnknownException e){
        Directory currentDir = this;
        for (String token : tokens) {
          currentDir = new Directory(token, currentDir, 2 /*TO DO*/);
        }
        return currentDir;
    }
  }

  /**
   * Create a PlainFile by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public PlainFile createPlainFileByPath(String path) throws FileExistsException {
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];
    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    Directory currentDir = createFileByPathHelper(tokensList);

    return new PlainFile(target, currentDir, 2 /*TO DO*/);
  }

  /**
   * Create a Directory by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public Directory createDirectoryByPath(String path) throws FileExistsException {
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];
    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    Directory currentDir = createFileByPathHelper(tokensList);

    return new Directory(target, currentDir, 2 /*TO DO*/);
  }

  /**
   * Create an App by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public void createAppByPath(String path) throws FileExistsException {
    /* Copy code from create'File'ByPath */
  }

  /**
   * Create a Link by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public void createLinkByPath(String path) throws FileExistsException {
  /* Copy code from create'File'ByPath */
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
    super.remove();
  }

  @Override
  public void execute(){
  }

  public void checkIllegalRemoval(String filename)
    throws IllegalRemovalException {
    if (filename.equals(".") || filename.equals(".."))
      throw new IllegalRemovalException();
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

}
