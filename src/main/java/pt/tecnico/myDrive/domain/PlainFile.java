package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class PlainFile extends PlainFile_Base {

  public PlainFile() {
    super();
  }
  
  @Override
  public <T> T accept(GenericVisitor<T> v){
	  return v.visit(this);
  }

}
