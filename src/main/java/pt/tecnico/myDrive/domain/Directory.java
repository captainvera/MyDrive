package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class Directory extends Directory_Base {

  public Directory() {
    super();
  }

  @Override
  public <T> T accept(GenericVisitor<T> v){
	  return v.visit(this);
  }

}
