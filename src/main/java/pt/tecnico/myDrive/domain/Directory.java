package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class Directory extends Directory_Base {

  public Directory(String name, Directory parent, Integer id) {
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
	public void open(){
	}

  @Override
  public <T> T accept(GenericVisitor<T> v){
	  return v.visit(this);
  }

}
