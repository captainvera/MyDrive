package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.InvalidFilenameException;
import pt.tecnico.myDrive.exceptions.InvalidFilepathSizeException;

import java.util.ArrayList;


import org.joda.time.DateTime;

public abstract class File extends File_Base {


  /** Placeholder for FenixFramework */
  protected File () {
    super();
  }

  public File(FileSystem fs, String name, Directory parent, User owner) {
    init(fs, fs.requestId(), name, parent, owner);
  }

  protected void init(FileSystem fs, Integer id, String name, Directory parent, User owner) {

    if(!fs.isRoot(owner)){
      checkFilename(name);
      checkFilepathSize(parent.getPath(), name);
    }

    super.setFileSystem(fs);
    super.setId(id);
    super.setName(name);
    setParent(parent);
    super.setOwner(owner);
    super.setLastModified(new DateTime());

    // File's initial permissions are the one's defined in the user's umask.
    super.setUserPermission(owner.getUmask().substring(0,4));
    super.setOthersPermission(owner.getUmask().substring(4,8));
  }

  /**
   * Verifies if filename only contains letters and digits
   * @param filename
   */
  private void checkFilename(String filename) {

    if(filename.length() == 0)
        throw new InvalidFilenameException(filename);

    char[] characters = filename.toCharArray();

    for (char c: characters) {
      if ((!Character.isLetter(c) && !Character.isDigit(c))

          || c == 0 || c == '\\') {
        throw new InvalidFilenameException(filename);
          }
    }
  }

  /**
   * Verifies if filepath has atmost 1024 characters
   * @param filepath
   */
  private void checkFilepathSize(String filepath, String filename) {
    if((filepath.length() + filename.length() + 1) >= 1024) throw new InvalidFilepathSizeException(1024);
  }


  /**
   * Basic remove implementation for File objects
   */
  protected void remove() {
    nullifyRelations();
    deleteDomainObject();
  }

  public void remove(User user) {
    checkDeletionPermissions(user);
    remove();
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    super.setOwner(null);
    super.setParent(null);
    super.setFileSystem(null);
  }

  /**
   * Executes the file with diferent behaviour depending on the file type
   */
  public abstract String execute(User user); 

  /**
   * The calculation of the size of the file will vary depending on subclass implementation
   *
   * @return Size of the file
   */
  public abstract int getSize();

  /**
   * Generic method
   */
  public abstract <T> T accept(GenericVisitor<T> v);

  /**
   * The path of a file is the concatenation of the path of the enclosing
   * directory and its name.
   *
   * @return The path corresponding to its location in the filesystem.
   */
  public String getPath() { return getParent().getPath() + "/" + getName(); }

  public String toString() {
    return getUserPermission() + getOthersPermission() + " " + getName();
  }

  public abstract File getFile(ArrayList<String> tokens, User user);

  /**
   * Two files are equal if they belong to the same file system, have the same
   * id in the file system and the same path.
   *
   * @param file
   * @return True if two files are equal
   */
  boolean equals(File file) {
    return getFileSystem() == file.getFileSystem() &&
      getId() == file.getId() &&
      getPath().equals(file.getPath());
  }

  @Override
  public void setUserPermission(String perm) {
    throw new MethodDeniedException();
  }

  @Override
  public void setOthersPermission(String perm) {
    throw new MethodDeniedException();
  }

  @Override
  public void setLastModified(DateTime lastModified) {
    throw new MethodDeniedException();
  }

  protected final void touch(){
    super.setLastModified(new DateTime());
  }

  public File getFileObject(User user) {
    return this;
  }

  // TODO
  /** @Override */
  /** public void setParent(Directory parent) { */
  /**   throw new MethodDeniedException(); */
  /** } */


  @Override
  public void setOwner(User owner) {
    throw new MethodDeniedException();
  }

  @Override
  public void setId(Integer id) {
    throw new MethodDeniedException();
  }


  /**
   * Verifies if user has permission to perform some operation on file
   *
   * @param user
   * @param index
   * @param c
   */
  protected void checkPermissions(User user, int index, char c) {
    String permissions = getPermissions(user);
    if(permissions.charAt(index) != c)
      throw new InsufficientPermissionsException();
  }

  protected void checkReadPermissions(User user) {
    checkPermissions(user, 0, 'r');
  }

  protected void checkWritePermissions(User user) {
    checkPermissions(user, 1, 'w');
  }

  protected void checkExecutionPermissions(User user) {
    checkPermissions(user, 2, 'x');
  }

  protected void checkDeletionPermissions(User user) {
    checkPermissions(user, 3, 'd');
  }

  protected String getPermissions(User user) {
    if (getFileSystem().getRootUser().equals(user))
      return "rwxd";
    else if (getOwner().equals(user))
      return getUserPermission();
    else
      return getOthersPermission();
  }


}

