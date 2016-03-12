package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public abstract class File extends File_Base {

  public File() {
    super();
	}

  public abstract <T> T accept(GenericVisitor<T> v);

  public abstract void remove();
}
