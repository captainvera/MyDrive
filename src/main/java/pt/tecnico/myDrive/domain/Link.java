package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class Link extends Link_Base {
	
  public Link() {
    super();
  }
  
  @Override
  public <T> T accept(GenericVisitor<T> v){
	  return v.visit(this);
  }
}
