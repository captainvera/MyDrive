package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class App extends App_Base {

  public App() {
    super();
  }

	/**
	 * TEMPORARY
	 */
	public int getSize(){
		return 0;
	}

  public <T> T accept(GenericVisitor<T> v){
		return v.visit(this);
	}
}
