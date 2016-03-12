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
}
