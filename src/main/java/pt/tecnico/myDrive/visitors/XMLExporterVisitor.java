package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import org.jdom2.Element;

public class XMLExporter implements GenericVisitor<Element>{
	
	public Element xmlfile(Element element, File f){
		element.setAttribute("name", f.getName());
		element.setAttribute("id", Integer.toString(f.getId()));
		element.setAttribute("userPermissions", f.getUserPermission());
		element.setAttribute("otherPermissions", f.getOthersPermission());
		return element;		
	}

	@Override
	public Element visit(Directory dir){
		Element element = new Element("directory");
		element = xmlfile(element, dir);
		
		Element files = new Elemens("files");
		element.addContent(files);
		for(File f: dir.getFiles())
			files.addContent(f.accept());

		return element;
	}

	@Override
	public Element visit(PlainFile pf){
		Element element = new Element("plainfile");
		element = xmlfile(element, pf);
		element.setAttribute("data", pf.getData());
		return element;
	}

	@Override
	public Element visit(App a){
		Element element = new Element("app");
		element = xmlfile(element,a);
		element.setAttribute("data", a.getData());
		return element;
	}

	@Override
	public Element visit(Link l){
		Element element = new Element("link");
		element = xmlfile(element, l);
		element.setAttribute("data", l.getData());
		return element; 
	}

	@Override
	public Element visit(User u){
		Element element = new Element ("user");
		element.setAttribute("username", u.getUsername());
		element.setAttribute("password", u.getPassword());
		element.setAttribute("name", u.getName());
		element.setAttribute("umask", u.getUmask());
		return element;
	}
}
