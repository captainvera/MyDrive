package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {


  /** Placeholder for FenixFramework */
  protected File () {
    super();
  }

  public File(Integer id, String name, Directory parent, User owner) {
    init(id, name, parent, owner);
  }

  protected void init(Integer id, String name, Directory parent, User owner) {
    setId(id);
    setName(name);
    setParent(parent);
    setOwner(owner);
    // File's initial permissions are the one's defined in the user's umask.
    setUserPermission(owner.getUmask().substring(0,3));
    setOthersPermission(owner.getUmask().substring(4,7));
  }

  /**
   * Basic remove implementation for File objects
   */
  public void remove(){
    nullifyRelations();
    deleteDomainObject();
  }

  /**
   * Nullifies relations, that is, deletes/cancels any relation between this
   * object and eventual others.
   */
  protected void nullifyRelations() {
    setOwner(null);
    setParent(null);
    setFileSystem(null);
  }

  /**
   * Executes the file with diferent behaviour depending on the file type
   */
  public abstract String execute();

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
    return this.getName();
  }
}

