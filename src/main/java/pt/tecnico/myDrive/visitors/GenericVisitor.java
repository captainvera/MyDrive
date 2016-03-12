package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;

public interface GenericVisitor<T> {
	public T visit(Directory dir);
	public T visit(PlainFile pf);
	public T visit(Link l);
	public T visit(App a);
	public T visit(User u);	
}
