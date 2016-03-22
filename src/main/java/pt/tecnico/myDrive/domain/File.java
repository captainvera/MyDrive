package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {


  /** Placeholder for FenixFramework */
  protected File () {
    super();
  }

  private File(GenericFileBuilder gfb) {
    init(gfb);
  }

  protected void init(GenericFileBuilder gfb){
    setId(gfb._id);
    setOwner(gfb._owner);
    setName(gfb._name);
    setParent(gfb._parent);
    setFileSystem(gfb._fileSystem);
    setUserPermission(gfb._userPermission);
    setOthersPermission(gfb._othersPermission);
  }

  /**
   * Generic abstract file builder.
   * All File's direct subclasses will inherit from this builder.
   */
  public abstract static class GenericFileBuilder
    <P extends File, T extends GenericFileBuilder<P,T>> {
      private int _id;
      private User _owner;
      private String _name;
      private Directory _parent;
      private FileSystem _fileSystem;
      private String _userPermission;
      private String _othersPermission;

      /**
       * @return A new file
       */
      public abstract P build();

      /**
       * Argument validation.
       */
      protected abstract void validate();

      /**
       * Allow the recovery of the type of 'this' on a self-referenced sub-type
       */
      protected abstract T getThis();

      public T withId(int id) {
        _id = id;
        return getThis();
      }

      public T withOwner(User owner) {
        _owner = owner;
        return getThis();
      }

      public T withName(String name) {
        _name = name;
        return getThis();
      }

      public T withParent(Directory parent) {
        _parent = parent;
        return getThis();
      }

      public T withFileSystem(FileSystem fileSystem) {
        _fileSystem = fileSystem;
        return getThis();
      }

      public T withUserPermission(String userPermission) {
        _userPermission = userPermission;
        return getThis();
      }

      public T withOthersPermission(String othersPermission) {
        _othersPermission = othersPermission;
        return getThis();
      }

    }

  public String toString(){
    return this.getName();
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

}

