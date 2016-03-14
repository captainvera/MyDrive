package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {

  
	public File() {
    super();
  }

	public File(String name, Directory parent, Integer id, User owner){
		init(name,parent,id,owner);
	}
	
	protected void init(String name, Directory parent, Integer id, User owner){
		setName(name);
		setParent(parent);
		setId(id);
		setOwner(owner);
	}

  public String toString(){
    return this.getName();
  }

	/**
	 * Basic remove implementation for File objects
	 */
	public void remove(){
		setFileSystem(null);
		setParent(null);
		setOwner(null);
		deleteDomainObject();
	}

	/**
	 * Executes the file with diferent behaviour depending on the file type
	 */
	public abstract void execute();

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
  public String getPath() { return getParent().getPath() + getName(); }

}

