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

		return plain;
	}

	@Override
	public Element visit(App a){
		Element app = new Element("app");
		app.setAttribute("id",String.valueOf(a.getId()));
		
		Element appPath = new Element("path");
		appPath.setText(a.getPath());

		Element appName = new Element("name");
		appName.setText(a.getName());
		
		Element appOwner = new Element("owner");
		appOwner.setText(a.getOwner().getUsername());
	
		Element appPerm = new Element("perm");
		appPerm.setText(a.getUserPermission());
		
		Element appContent = new Element("method");
		appContent.setText(a.getData());

		app.addContent(appPath);
		app.addContent(appName);
		app.addContent(appOwner);
		app.addContent(appPerm);
		app.addContent(appContent);

		return app;
	}
	@Override
	public Element visit(Link l){ 
		Element app = new Element("link");
		link.setAttribute("id",String.valueOf(l.getId()));
		
		Element linkPath = new Element("path");
		linkPath.setText(l.getPath());

		Element linkName = new Element("name");
		linkName.setText(l.getName());
		
		Element linkOwner = new Element("owner");
		linkOwner.setText(l.getOwner().getUsername());
	
		Element linkPerm = new Element("perm");
		linkPerm.setText(l.getUserPermission());
		
		Element linkContent = new Element("method");
		linkContent.setText(l.getData());

		link.addContent(linkPath);
		link.addContent(linkName);
		link.addContent(linkOwner);
		link.addContent(linkPerm);
		link.addContent(linkContent);

		return link;
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
