package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.NotADirectoryException;

/**
 * Simple directory visitor. Allows throws and exception if the element being visited is not a Directory
 */

public class DirectoryVisitor implements GenericVisitor<Directory> {

  @Override
  public Directory visit(Directory dir) {
    return dir;
  }

  @Override
  public Directory visit(PlainFile pf) {
    return null;
  }

  @Override
  public Directory visit(Link l) {
    return null;
  }

  @Override
  public Directory visit(App a) {
    return null;
  }

	@Override	
	public Directory visit(User u){
		return null;
	}
}
