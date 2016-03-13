package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;

import pt.tecnico.myDrive.exceptions.ImportDocumentException; 

public class User extends User_Base {

	public User() {
		super();
	}

	public void remove() { }

	public Element xmlExport(){
		Element user = new Element("user");
		user.setAttribute("username", getUsername());

		Element userName = new Element("name");
		userName.setText(getName());

		Element userPwd = new Element("pwd");
		userPwd.setText(getPassword());

		Element userHomeDir = new Element("home");
		userHomeDir.setText(getHomeDirectory().getName());

		Element userUmask = new Element("umask");
		userUmask.setText(getUmask());

		user.addContent(userName);
		user.addContent(userPwd);
		user.addContent(userHomeDir);
		user.addContent(userUmask);

		return user;
	}


	public void xmlImport(Element userElement) throws ImportDocumentException{
		try{
			for(Element name: userElement.getChildren("name")){
				if(name != null)
					setName(new String(name.getText().getBytes("UTF-8")));
			}

			for(Element pwd: userElement.getChildren("password")){
				if (pwd != null)
					setPassword(new String(pwd.getText().getBytes("UTF-8"))); 			
			}

			for(Element home: userElement.getChildren("home")){
				if (home != null){
					String path = new String(home.getText().getBytes("UTF-8"));
					//setHomeDirectory(createDirbyPath(path)); mudar para a funcao do vicente
				}
			}	

			for(Element umask: userElement.getChildren("mask")){
				if(umask != null)
					setUmask(new String(umask.getText().getBytes("UTF-8")));
			}
		} catch(UnsupportedEncodingException e){
			throw new ImportDocumentException(getUsername());
		}
	}
}
