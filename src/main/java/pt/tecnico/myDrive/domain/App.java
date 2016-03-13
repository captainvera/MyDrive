package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class App extends App_Base {

  public App(String name, Directory parent, Integer id) {
    super();
		init(name, parent, id);
  }

	/**
	 * TEMPORARY
	 */
	@Override
	public int getSize(){
		return 0;
	}

	@Override
	public void execute(){
	}

  @Override
	public <T> T accept(GenericVisitor<T> v){
		return v.visit(this);
	}
  
	@Override
  public void remove() { 
		super.remove();
	}
}
