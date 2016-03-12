package pt.tecnico.myDrive.visitors;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import org.jdom2.Element;

public class XMLExporterVisitor implements GenericVisitor<Element>{
	
	@Override
	public Element visit(Directory dir){
		Element directory = new Element("dir");
		directory.setAttribute("id",String.valueOf(dir.getId()));
				
		Element dirPath = new Element("path");
		dirPath.setText(dir.getPath());

		Element dirName = new Element("name");
		dirName.setText(dir.getName());
		
		Element dirOwner = new Element("owner");
		dirOwner.setText(dir.getOwner().getUsername());

		Element dirPerm = new Element("perm");
		dirPerm.setText(dir.getUserPermission());

		directory.addContent(dirPath);
		directory.addContent(dirName);
		directory.addContent(dirOwner);
		directory.addContent(dirPerm);

		return directory;
	}

	@Override
	public Element visit(PlainFile pf){
		Element plain = new Element("plain");
		plain.setAttribute("id",String.valueOf(pf.getId()));
		
		Element plainPath = new Element("path");
		plainPath.setText(pf.getPath());

		Element plainName = new Element("name");
		plainName.setText(pf.getName());
		
		Element plainOwner = new Element("owner");
		plainOwner.setText(pf.getOwner().getUsername());
	
		Element plainPerm = new Element("perm");
		plainPerm.setText(pf.getUserPermission());
		
		Element plainContent = new Element("content");
		plainContent.setText(pf.getData());

		plain.addContent(plainPath);
		plain.addContent(plainName);
		plain.addContent(plainOwner);
		plain.addContent(plainPerm);
		plain.addContent(plainContent);
	}

	@Override
	public Element visit(App a){
		Element element = new Element("app");
		
		element.setAttribute("data", a.getData());
		return element;
	}

	@Override
	public Element visit(Link l){
		Element element = new Element("link");
		
		element.setAttribute("data", l.getData());
		return element; 
	}

	@Override
	public Element visit(User u){
		Element user = new Element("user");
		user.setAttribute("username", u.getUsername());
		
		Element userName = new Element("name");
		userName.setText(u.getName());
		
		Element userPwd = new Element("pwd");
		userPwd.setText(u.getPassword());

		Element userHomeDir = new Element("home");
		userHomeDir.setText(u.getHomeDirectory().getName());

		Element userUmask = new Element("umask");
		userUmask.setText(u.getUmask());
		
		user.addContent(userName);
		user.addContent(userPwd);
		user.addContent(userHomeDir);
		user.addContent(userUmask);

		return user;
	}
}
