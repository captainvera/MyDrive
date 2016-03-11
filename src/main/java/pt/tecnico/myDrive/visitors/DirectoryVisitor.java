package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.exceptions.NotADirectoryException;

/**
 * Simple directory visitor. Allows throws and exception if the element being visited is not a Directory
 */

public class DirectoryVisitor implements GenericVisitor<Boolean> {

	@Override
	public Boolean visit(Directory dir) {
		return true;
	}

	@Override
	public Boolean visit(PlainFile pf) {
		return false;
	}

	@Override
	public Boolean visit(Link l) {
		return false;
	}

	@Override
	public Boolean visit(App a) {	
		return false;
	}
}
