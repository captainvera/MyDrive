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

import pt.ist.fenixframework.FenixFramework;
import org.joda.time.DateTime;

import pt.tecnico.myDrive.exceptions.*;
import java.lang.reflect.InvocationTargetException;

import pt.tecnico.myDrive.visitors.DirectoryVisitor;

public class FileSystem extends FileSystem_Base {

	private Directory _rootDirectory;
	private User _rootUser;

	/**
	 * FileSystem temporary state variables
	 * _loggedUser: keeps track of the user logged in
	 * _currentDirectory: keeps track of the current navigation directory
	 */

	private User _loggedUser;
	private Directory _currentDirectory;

	private FileSystem() {
		System.out.println("FileSystem constructor");
		setRoot(FenixFramework.getDomainRoot());
		init();
	}

	/**
	 * @return current instance of FileSystem if stored, or a new FileSystem otherwise
	 */
	public static FileSystem getInstance(){
		System.out.println("Fetching File System");
		FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();
		System.out.println("Got fs");
		if(fs != null){
			System.out.println("ITs not null");
			return fs;
		}

		System.out.println("New FileSystem");
		return new FileSystem();
	}

	/**
	 * Does basic FileSystem initialization
	 */
	public void init() {
		/**
		 * Creation of root directory and home folder if there are no files,
		 * means we're initializing a new filesystem
		 * If the filesystem is initialized we search for the root user and 
		 * root directory
		 */

		System.out.println("Initializing FileSystem");  

		if(this.getFilesSet().size() == 0){
			setIdCounter(0);
			System.out.println("Creating root user");
			_rootUser = createRootUser();

			System.out.println("Creating root directory");
			_rootDirectory = createDirectory("/",null,_rootUser);
			_rootDirectory.setParent(_rootDirectory);

			System.out.println("Creating home directory");
			Directory homeDir = createDirectory("home",_rootDirectory,_rootUser); 

			_rootUser.setHomeDirectory(createDirectory("root", homeDir, _rootUser));  

			_currentDirectory = _rootDirectory;
			System.out.println("End init");
		}else{
			_rootUser = getUserByUsername("root");
			_rootDirectory = getRootDirectory();
			if(_rootDirectory == null){
				System.out.println("FileSystem has files but no root directory");
			}
			_currentDirectory = _rootDirectory;
		}
		System.out.println("Finished FileSystem initialization");
	}

	/**
	 * Verifies if the file f is a directory and gets the corresponding directory
	 * @return Directory corresponding to f argument, or null if its not a Directory
	 */
	public Directory assertDirectory(File f) throws NotADirectoryException {
		DirectoryVisitor dv = new DirectoryVisitor();
		Directory dir = f.accept(dv);
		if(dir == null){
			throw new NotADirectoryException(f.getName());
		}
		return dir;
	}


	/**
	 * Logins a user into the filesystem, changing current directory to home directory
	 */
	private void login(User user, String password){
		if(user.getPassword().equals(password)){
			_loggedUser = user;
			_currentDirectory = _loggedUser.getHomeDirectory();
		}else{
			/**
			 * TODO: Create wrong password exception
			 */
			System.out.println("COuldn't login! Wrong password.");
		}
	}

	public void login(String username, String password) throws UserUnknownException {
		System.out.println("Logging in");
		if(!userExists(username)){
			throw new UserUnknownException(username);
		}
		login(getUserByUsername(username), password);

	}

	/**
	 * Verifies if a username only contains letters and digits
	 */
	private Boolean isValidUsername(String username){
		char[] characters = username.toCharArray();

		for(char c: characters){
			if(!Character.isLetter(c) && !Character.isDigit(c)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Searches Users Set by username (since username is unique) to find a specified user.
	 * Returns null if no user is found. Does not throw exception here
	 */
	private User getUserByUsername(String username){
		for(User user: this.getUsersSet()){
			if(user.getUsername().equalsIgnoreCase(username)){
				return user;
			}
		}
		return null;
	} 

	/**
	 * Verifies if a user exists by its username. Usernames are unique
	 */
	private Boolean userExists(String username){
		return (getUserByUsername(username)!=null);
	}

	private Boolean userExists(User user){
		return userExists(user.getName());
	}

	/**
	 * Creates a new user, checking for username constraints
	 * Also creates its home directory
	 * @return the created User
	 */
	public User createUser(String username, String name, String password) throws UserExistsException, InvalidUsernameException {
		User user = new User();

		/**
		 * We must check if the username is a valid one: only letters and decimals
		 * Must also heck if it already exists, throw exception in such case
		 */

		if(!isValidUsername(username))
			throw new InvalidUsernameException(username);  

		if(userExists(username))
			throw new UserExistsException(username);

		user.setFileSystem(this);
		user.setUsername(username);
		user.setPassword(password);
		user.setUmask("");
		user.setName(name);

		/**
		 * TODO: Solve error throwing here. Exceptions shouldn't happen;
		 * Should be handled elsewhere
		 */
		try{
			Directory home = assertDirectory(_rootDirectory.getFileByName("home"));
			createDirectory(username, home, user);
		}catch(FileUnknownException e){
			System.out.println(e.getMessage());
		}catch(NotADirectoryException e){
			System.out.println(e.getMessage());
		}

		System.out.println("Added user");
		addUsers(user);

		return user;
	}

	/**
	 * Creates Root User 
	 * It's home directory isn't created here to avoid conflicts in FileSystem init
	 */

	public User createRootUser() { 
		User user = new User();
		user.setFileSystem(this);
		user.setUsername("root");
		user.setPassword("***");
		user.setUmask("");
		user.setName("Super User");

		System.out.println("Added root user");
		addUsers(user);

		return user;
	}

	/**
	 * ------------------------------------------------------------
	 * File creation and deletion methods. Permissions should be checked here
	 */

	private Directory createDirectory(String name, Directory parent, User owner){
		Directory dir;
		setIdCounter(getIdCounter()+1);
		dir = new Directory(name,parent,getIdCounter(),owner);
		addFiles(dir);
		return dir; 
	}

	private PlainFile createPlainFile(String name, Directory parent, User owner){
		PlainFile pf;
		setIdCounter(getIdCounter()+1);
		pf = new PlainFile(name,parent,getIdCounter(),owner);
		addFiles(pf);
		return pf;
	}

	private App createApp(String name, Directory parent, User owner){
		App app;
		setIdCounter(getIdCounter()+1);
		app = new App(name,parent,getIdCounter(),owner);
		addFiles(app);
		return app; 
	}

	private Link createLink(String name, Directory parent, User owner){
		Link link;
		setIdCounter(getIdCounter()+1);
		link = new Link(name,parent,getIdCounter(),owner);
		addFiles(link);
		return link; 
	} 

	private void removeFile(File f){
		f.remove();
		removeFiles(f);
	}

	/**
	 * Public file creation methods
	 */

	public Directory createDirectory(String name){
		return createDirectory(name,_currentDirectory,_loggedUser);
	}

	public PlainFile createPlainFile(String name){
		return createPlainFile(name,_currentDirectory,_loggedUser);
	}

	public App createApp(String name){
		return createApp(name,_currentDirectory,_loggedUser); 
	}

	public Link createLink(String name){
		return createLink(name,_currentDirectory,_loggedUser); 
	}



	/**
	 * Finds Root Directory
	 * Does not throw exception if Root is not found
	 * TODO: Should throw exception 
	 */

	public Directory getRootDirectory() {
		Directory dir;
		DirectoryVisitor dv = new DirectoryVisitor();
		for(File f: getFilesSet()){
			dir = f.accept(dv);
			if(dir != null && dir.isTopLevelDirectory())
				return dir; 
		}

		/**
		 * No root directory found, means either corrupted or
		 * empty filesystem
		 */

		return null;
	}
	/**
	 * Changes current working directory
	 */
	public void changeDirectory(String dirName) throws FileUnknownException, NotADirectoryException {
		_currentDirectory = assertDirectory(_currentDirectory.getFileByName(dirName)); 

	}

	/**
	 * ----------------------------------------------------
	 * Public FileSystem usage methods
	 * Allows execution of several commands on the current FileSystem
	 *
	 */

	/**
	 * @return current working directory path
	 */
	public String listPath(){
		return _currentDirectory.getPath(); 
	}

	/**
	 * @return current working directory name
	 */
	public String currentDirectory(){
		return _currentDirectory.getName();
	}

	/**
	 * @return current working directory listing (files)
	 */
	public String listDirectory() 
		throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
			return _currentDirectory.listFilesAll();
		}

	public Document xmlExport(){
		DirectoryVisitor visitor = new DirectoryVisitor();
		XMLExporterVisitor xml = new XMLExporterVisitor();
		Element mydrive = new Element("myDrive");
		Document doc = new Document(mydrive);

		for(User u: getUsersSet())
			mydrive.addContent(u.xmlExport());

		for (File f: getFilesSet())
				mydrive.addContent(f.accept(xml));
		
		return doc;
	}

	/*public void xmlImport(Element firstElement){
		try{
			for(Element userElement: firstElement.getChildren("user")){
				String path = new String(userElement.getChild("home").getText().getBytes("UTF-8"));
				Directory homedir = createDirByPath(path);
				User u = new User(homedir);
				u.xmlImport(userElement);
			}

			for(Element dirElement: firstElement.getChildren("dir")){
				String name = dirElement.getChild("name").getText();
				String path = dirElement.getChild("path").getText();
				path = path + "/" + name;
				Directory dir = createDirByPath(path);

				Element owner = dirElement.getChild("owner");
				User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
				dir.setOwner(u);

				dir.xmlImport(dirElement);
			}

			for(Element plainElement: firstElement.getChildren("plain")){
				String name = plainElement.getChild("name").getText();
				String path = plainElement.getChild("path").getText();
				path = path + "/" + name;
				PlainFile plain = createPlainByPath(path);

				Element owner = plainElement.getChild("owner");
				User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
				plain.setOwner(u);

				plain.xmlImport(plainElement);
			}

			for(Element linkElement: firstElement.getChildren("link")){
				String name = linkElement.getChild("name").getText();
				String path = linkElement.getChild("path").getText();
				path = path + "/" + name;
				Link link = createLinkByPath(path);

				Element owner = linkElement.getChild("owner");
				User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
				link.setOwner(u);

				link.xmlImport(linkElement);
			}

			for(Element appElement: firstElement.getChildren("app")){
				String name = appElement.getChild("name").getText();
				String path = appElement.getChild("path").getText();
				path = path + "/" + name;
				App app = createAppByPath(path);

				Element owner = appElement.getChild("owner");
				User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
				app.setOwner(u);
				app.xmlImport(appElement);
			}
		} catch(Exception e){
			throw new ImportDocumentException("in fs");
		}
	}*/
}
