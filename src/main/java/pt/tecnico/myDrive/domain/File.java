package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.InvalidFilenameException;
import pt.tecnico.myDrive.exceptions.InvalidFilepathSizeException;
import pt.tecnico.myDrive.exceptions.NoExtensionException;
import pt.tecnico.myDrive.exceptions.NoAssociatedAppException;

import java.util.ArrayList;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import java.security.acl.Owner;

import org.jdom2.DataConversionException;

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

    checkFilepathSize(parent, name);
    checkFilename(name);

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
   * Verifies if filename only contains letters and digits and doesn't start
   * with a '$'
   * @param filename
   */
  private void checkFilename(String filename) {

    /**
     * File names can't have '$' as first character, since that's how an
     * environment variable is recognized
     */
    if(filename.length() == 0 || filename.charAt(0) == '$')
      throw new InvalidFilenameException(filename);

    char[] characters = filename.toCharArray();

    for (char c: characters) {
      if (c == 0 || c == '\\') {
        throw new InvalidFilenameException(filename);
      }
    }
  }

  /**
   * Verifies if filepath has atmost 1024 characters
   * @param filepath
   */
  private void checkFilepathSize(Directory parent, String filename) {
    String filepath = filename + (parent == null ? "" : parent.getPath());
    if(filepath.length() >= 1024) throw new InvalidFilepathSizeException(1024);
  }


  /**
   * Basic remove implementation for File objects
   */
  protected void remove() {
    nullifyRelations();
    deleteDomainObject();
  }

  public void remove(User user) {
    user.checkDeletionPermissions(this);
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
  public abstract void execute(User user, String[] arguments);


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

  public File getFileObject(User user) {
    return this;
  }

  protected String getPermissions(User user) {
    return user.getPermissions(this);
  }

  public void xmlImport(Element dirElement) throws UnsupportedEncodingException, DataConversionException {
    super.setId(dirElement.getAttribute("id").getIntValue());

    Element perm = dirElement.getChild("perm");
    if (perm != null){
      String userPermission = new String(perm.getText().getBytes("UTF-8"));
      super.setUserPermission(userPermission.substring(0,4));
      super.setOthersPermission(userPermission.substring(4,8));
    }
  }


  /**
   * Fenix framework stuff
   */

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
  @Override
  public void setFileSystem(FileSystem fs) {
    throw new MethodDeniedException();
  }

  @Override
  public FileSystem getFileSystem() {
    throw new MethodDeniedException();
  }

  @Override
  public void setOwner(User owner) {
    // TODO|FIXME
    // Shouldn't be public
    super.setOwner(owner);
  }

  @Override
  public void setId(Integer id) {
    throw new MethodDeniedException();
  }

  public String parseExtension(){
      String fileName = getName();
      if(fileName.lastIndexOf(".") == -1) return null;
      String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
      return extension;
  }

  /**
   * Protected methods for subclass access
   */
  protected final FileSystem getFileSystem$6p() {
    return super.getFileSystem();
  }

}

