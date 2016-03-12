package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class App extends App_Base {

  public App() {
    super();
  }

  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }
  @Override
  public void remove() { }

}
