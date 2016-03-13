package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {

  public File() {
    super();
	}

	protected void init(String name, Directory parent, Integer id){
		setName(name);
		setParent(parent);
		setId(id);
	}

	public String toString(){
		return this.getName();
	}

	/**
	 * Basic remove implementation for FIle objects
	 * Specific sublcass behaviour might be implemented 
	 */
	public void remove(){
		setFileSystem(null);
		setParent(null);	
		
	}
	
	/**
	 * Opens the file. Should be implemented with proper behaviour in subclasses
	 */
	public abstract void open();
	
	public abstract int getSize();

  public abstract <T> T accept(GenericVisitor<T> v);
}
