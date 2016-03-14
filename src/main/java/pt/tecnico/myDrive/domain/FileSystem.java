package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;

import org.jdom2.Element;
import org.jdom2.Document;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.visitors.XMLExporterVisitor;
import pt.tecnico.myDrive.visitors.DirectoryVisitor;

import pt.tecnico.myDrive.exceptions.ImportDocumentException;

public class FileSystem extends FileSystem_Base {

	public FileSystem() {
		super();
	}

	public Document xmlExport(){
		DirectoryVisitor visitor = new DirectoryVisitor();
		XMLExporterVisitor xml = new XMLExporterVisitor();
		Element mydrive = new Element("myDrive");
		Document doc = new Document(mydrive);

		for(User u: getUsersSet()){
			if(!u.getUsername().equals("root"))
				mydrive.addContent(u.xmlExport());
		}

		for (File f: getFilesSet()){
			if(f.getOwner().getUsername().equals("root")){
				Directory dir = f.accept(visitor);
				if(dir != null){
					if(dir.getFileSet() == null)
						mydrive.addContent(dir.accept(xml));
				}
			}
			else
				mydrive.addContent(f.accept(xml));
		}
		return doc;
	}

	public void xmlImport(Element firstElement){
		try{
			for(Element userElement: firstElement.getChildren("user")){
				String path = new String(userElement.getChild("home").getText().getBytes("UTF-8"));
				Directory homedir = getRootDirectory().createDirByPath(path);
				User u = new User(homedir);
				u.xmlImport(userElement);
			}

			for(Element dirElement: firstElement.getChildren("dir")){
				String name = dirElement.getChild("name").getText();
				String path = dirElement.getChild("path").getText();
				path = path + "/" + name;
				Directory dir = getRootDirectory().createDirByPath(path);

				Element owner = dirElement.getChild("owner");
				User u = getUserbyUsername(new String(owner.getText().getBytes("UTF-8")));
				dir.setOwner(u);

				dir.xmlImport(dirElement);
			}

			for(Element plainElement: firstElement.getChildren("plain")){
				String name = plainElement.getChild("name").getText();
				String path = plainElement.getChild("path").getText();
				path = path + "/" + name;
				PlainFile plain = getRootDirectory().createPlainByPath(path);

				Element owner = plainElement.getChild("owner");
				User u = getUserbyUsername(new String(owner.getText().getBytes("UTF-8")));
				plain.setOwner(u);

				plain.xmlImport(plainElement);
			}

			for(Element linkElement: firstElement.getChildren("link")){
				String name = linkElement.getChild("name").getText();
				String path = linkElement.getChild("path").getText();
				path = path + "/" + name;
				Link link = getRootDirectory().createLinkByPath(path);

				Element owner = linkElement.getChild("owner");
				User u = getUserbyUsername(new String(owner.getText().getBytes("UTF-8")));
				link.setOwner(u);

				link.xmlImport(linkElement);
			}

			for(Element appElement: firstElement.getChildren("app")){
				String name = appElement.getChild("name").getText();
				String path = appElement.getChild("path").getText();
				path = path + "/" + name;
				App app = getRootDirectory().createAppByPath(path);

				Element owner = appElement.getChild("owner");
				User u = getUserbyUsername(new String(owner.getText().getBytes("UTF-8")));
				app.setOwner(u);

				app.xmlImport(appElement);
			}
		} catch(Exception e){
			throw new ImportDocumentException("in fs");
		}
	}
}
