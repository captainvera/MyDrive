package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {

  public File() {
    super();
  }

  public String toString(){
    return this.getName();
  }

  public abstract int getSize();

  public abstract <T> T accept(GenericVisitor<T> v);

  public abstract void remove();

  /**
   * The path of a file is the concatenation of the path of the enclosing
   * directory and its name.
   *
   * @return The path corresponding to its location in the filesystem.
   */
  public String getPath() { return getParent().getPath() + getName(); }

}

