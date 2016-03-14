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

import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.*;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.visitors.DirectoryVisitor;

import java.util.*;

public class FileSystem extends FileSystem_Base {

  private static final Logger log = LogManager.getRootLogger();

  private  Directory _rootDirectory;
  private User _rootUser;

  /**
   * FileSystem temporary state variables
   * _loggedUser: keeps track of the user logged in
   * _currentDirectory: keeps track of the current navigation directory
   */

  private User _loggedUser;
  private Directory _currentDirectory;

  private FileSystem() {
    log.trace("Constructing new FileSystem");
    setRoot(FenixFramework.getDomainRoot());
    try {
      init();
    } catch(RootDirectoryNotFoundException e) {
      System.out.println("-- Couldn't find Root Directory. Rebuilding File System");
      reset();
    }
  }

  /**
   * @return current instance of FileSystem if stored, or a new FileSystem otherwise
   */
  public static FileSystem getInstance() {
    log.trace("FileSystem instance requested");
    FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();

    if (fs != null) {
      log.trace("Returning existing FileSystem instance");
      try {
        fs.init();
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
      return fs;
    }

    log.trace("Returning new FileSystem instance");
    return new FileSystem();
  }

  private void cleanup() {
    for (File f: getFilesSet())
      f.remove();
    for (User u: getUsersSet())
      u.remove();
  }

  /**
   * Resets the filesystem. Wipes all data stored in it and creates its initial
   * data.
   */
  public void reset() {
    cleanup();
    cleanInit();
  }

  /**
   * Does basic FileSystem initialization
   */
  public void init() throws RootDirectoryNotFoundException {
    /**
     * Creation of root directory and home folder if there are no files,
     * means we're initializing a new filesystem
     * If the filesystem is initialized we search for the root user and
     * root directory
     */

    if (this.getFilesSet().size() == 0) {
      cleanInit();
    } else{
      log.trace("Initializing existing FileSystem");
      _rootUser = getUserByUsername("root");
      _rootDirectory = getRootDirectory();
      if (_rootDirectory == null) {
        throw new RootDirectoryNotFoundException();

      }
      _currentDirectory = _rootDirectory;
    }
    log.trace("Finished FileSystem initialization");
  }


  /**
   * Creates the initial data for the filesystem, such as, root user, root
   * directory and root's user home directory.
   */
  private void cleanInit() {
    log.trace("Initializing new FileSystem");
    setIdCounter(0);

    log.trace("Creating root user");
    _rootUser = createRootUser();

    log.trace("Creating root directory");
    _rootDirectory = createDirectory("/",null,_rootUser);
    _rootDirectory.setParent(_rootDirectory);

    log.trace("Creating home directory");
    Directory homeDir = createDirectory("home",_rootDirectory,_rootUser);

    _rootUser.setHomeDirectory(createDirectory("root", homeDir, _rootUser));

    _currentDirectory = _rootDirectory;
  }

  /**
   * Verifies if the file f is a directory and gets the corresponding directory
   * @return Directory corresponding to f argument, or null if its not a Directory
   */
  public Directory assertDirectory(File f) throws NotADirectoryException {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory dir = f.accept(dv);
    if (dir == null) {
      throw new NotADirectoryException(f.getName());
    }
    return dir;
  }


  /**
   * Logins a user into the filesystem, changing current directory to home directory
   */
  private void login(User user, String password) {
    if (user.getPassword().equals(password)) {
      _loggedUser = user;
      _currentDirectory = _loggedUser.getHomeDirectory();
    } else {
      /**
       * TODO: Should throw WrongPasswordException
       */
      System.out.println("-- Wrong password. Login aborted");
    }
  }

  public void login(String username, String password) throws UserUnknownException, WrongPasswordException {
    log.trace("Logging in");
    if (!userExists(username)) {
      throw new UserUnknownException(username);
    }
    login(getUserByUsername(username), password);
  }

  /**
   * Verifies if a username only contains letters and digits
   */
  private Boolean isValidUsername(String username) {
    char[] characters = username.toCharArray();

    for (char c: characters) {
      if (!Character.isLetter(c) && !Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Searches Users Set by username (since username is unique) to find a specified user.
   * Returns null if no user is found. Does not throw exception here
   */
  private User getUserByUsername(String username) {
    for (User user: this.getUsersSet()) {
      if (user.getUsername().equalsIgnoreCase(username)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Verifies if a user exists by its username. Usernames are unique
   */
  private Boolean userExists(String username) {
    return (getUserByUsername(username)!=null);
  }

  private Boolean userExists(User user) {
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

    if (!isValidUsername(username))
      throw new InvalidUsernameException(username);

    if (userExists(username))
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
    try {
      Directory home = assertDirectory(_rootDirectory.getFileByName("home"));
      createDirectory(username, home, user);
    } catch(FileUnknownException e) {
      System.out.println(e.getMessage());
    } catch(NotADirectoryException e) {
      System.out.println(e.getMessage());
    }

    log.trace("Added user " + username);
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

    addUsers(user);

    return user;
  }

  /**
   * ------------------------------------------------------------
   * File creation and deletion methods. Permissions should be checked here
   */

  private Directory createDirectory(String name, Directory parent, User owner) {
    Directory dir;
    setIdCounter(getIdCounter()+1);
    dir = new Directory(name,parent,getIdCounter(),owner);
    addFiles(dir);
    return dir;
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner) {
    PlainFile pf;
    setIdCounter(getIdCounter()+1);
    pf = new PlainFile(name,parent,getIdCounter(),owner);
    addFiles(pf);
    return pf;
  }

  private App createApp(String name, Directory parent, User owner) {
    App app;
    setIdCounter(getIdCounter()+1);
    app = new App(name,parent,getIdCounter(),owner);
    addFiles(app);
    return app;
  }

  private Link createLink(String name, Directory parent, User owner) {
    Link link;
    setIdCounter(getIdCounter()+1);
    link = new Link(name,parent,getIdCounter(),owner);
    addFiles(link);
    return link;
  }

  private void removeFile(File f) {
    f.remove();
  }

  /**
   * Public file creation methods
   */

  public Directory createDirectory(String name) {
    return createDirectory(name,_currentDirectory,_loggedUser);
  }

  public PlainFile createPlainFile(String name) {
    return createPlainFile(name,_currentDirectory,_loggedUser);
  }

  public App createApp(String name) {
    return createApp(name,_currentDirectory,_loggedUser);
  }

  public Link createLink(String name) {
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
    for (File f: getFilesSet()) {
      dir = f.accept(dv);
      if (dir != null && dir.isTopLevelDirectory())
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
   */

  /**
   * @return current working directory path
   */
  public String listPath() {
    return _currentDirectory.getPath();
  }

  /**
   * @return current working directory name
   */
  public String currentDirectory() {
    return _currentDirectory.getName();
  }

  /**
   * @return current working directory listing (files)
   */
  public String listDirectory()
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      return _currentDirectory.listFilesAll();
    }

  /**
   * @return result of executing file
   */
  public String executeFile(String file) throws FileUnknownException {
    return _currentDirectory.getFileByName(file).execute();
  }

  /**
   * ------------------------------------------------------
   * Operations by path
   */

  /**
   * Get a file by its path.
   *
   * @param path
   * @return The file at the end of the path.
   * @throws FileUnknownException
   */
  public File getFileByPath(String path) throws FileUnknownException, NotADirectoryException {
    if (path.equals("/")) return _rootDirectory;
    Directory current;
    DirectoryVisitor dv = new DirectoryVisitor();

    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    if (path.charAt(0) == '/') {
      current = _rootDirectory;
      tokensList.remove(0);
    } else{
      current = _currentDirectory;
    }

    for (String tok : tokensList) {
      current = current.getFileByName(tok).accept(dv);
      if (current==null) {
        /**
         * TODO: Implement exception handling
         */
        return null;
      }
    }
    return current.getFileByName(target);
  }

  /**
   * List file content from a given path
   *
   */
  public String listFileByPathSimple(String path) throws IllegalAccessException, FileUnknownException, NotADirectoryException,
         NoSuchMethodException, InvocationTargetException {
           DirectoryVisitor dv = new DirectoryVisitor();
           Directory d = getFileByPath(path).accept(dv);
           return d.listFilesSimple();
         }

  /**
   * remove a file by its path.
   *
   * @param path
   * @throws FileUnknownException
   */
  public void removeFileByPath(String path) throws FileUnknownException, NotADirectoryException {
    removeFile (getFileByPath(path));
  }
  /**
   * Helper function to call when the directories in the path need to be processed/created
   */
  private Directory createFileByPathHelper(Directory current, ArrayList<String> tokensList) {
    File temp ;
    DirectoryVisitor dv = new DirectoryVisitor();

    for (String tok : tokensList) {
      try {
        temp = current.getFileByName(tok);
        current = temp.accept(dv);
      } catch(FileUnknownException e) {
        current = createDirectory(tok, current, _rootUser);
      }
      if (current==null) {
        System.out.println("Conflicting file names");
        return null;
      }
    }
    return current;
  }

  /**
   * Create a PlainFile by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public PlainFile createPlainFileByPath(String path) throws FileExistsException {
    Directory current;
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    if (path.charAt(0) == '/') {
      current = _rootDirectory;
      tokensList.remove(0);
    }else{
      current = _currentDirectory;
    }

    Directory currentDir = createFileByPathHelper(current, tokensList);

    return createPlainFile(target, currentDir, _rootUser);
  }

  /**
   * Create a Directory by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public Directory createDirectoryByPath(String path) throws FileExistsException {

    Directory current;
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    if (path.charAt(0) == '/') {
      current = _rootDirectory;
      tokensList.remove(0);
    }else{
      current = _currentDirectory;
    }

    Directory currentDir = createFileByPathHelper(current, tokensList);

    try {
      DirectoryVisitor dv = new DirectoryVisitor();
      return currentDir.getFileByName(target).accept(dv);
    }
    catch (FileUnknownException e) {
      return createDirectory(target, currentDir, _rootUser);
    }

  }

  /**
   * Create an App by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public void createAppByPath(String path) throws FileExistsException {
    /* Copy code from create'File'ByPath */
  }

  /**
   * Create a Link by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   */
  public void createLinkByPath(String path) throws FileExistsException {
    /* Copy code from create'File'ByPath */
  }


  public Document xmlExport() {
    XMLExporterVisitor xml = new XMLExporterVisitor();
    DirectoryVisitor isDirectory = new DirectoryVisitor();
    Element mydrive = new Element("myDrive");
    Document doc = new Document(mydrive);

    for (User u: getUsersSet()){
      if(!u.getUsername().equals("root"))
        mydrive.addContent(u.xmlExport());
    }
    for (File f: getFilesSet()){
      if(f.getOwner().getUsername().equals("root")){
        if(f.accept(isDirectory) != null){
          Directory dir = f.accept(isDirectory);
          if(dir.getSize() == 2){
            if(dir.getPath().equals("home/root"))
              mydrive.addContent(f.accept(xml));
          }
        }
        else mydrive.addContent(f.accept(xml));
      }
      else mydrive.addContent(f.accept(xml));
    }
    return doc;
  }

  public void xmlImport(Element firstElement) {
    try {
      for (Element userElement: firstElement.getChildren("user")) {
        String username = new String(userElement.getAttribute("username").getValue());
        Element homeElement = userElement.getChild("home");
  
        String path;
        if (homeElement == null) path = "/home" + "/" + username;
        else path = new String(homeElement.getText().getBytes("UTF-8"));

        Directory homedir = createDirectoryByPath(path);

        User u = new User(homedir, username);
        u.setFileSystem(this);
        u.xmlImport(userElement);
      }

      for (Element dirElement: firstElement.getChildren("dir")) {
        String name = dirElement.getChild("name").getText();
        String path = dirElement.getChild("path").getText();
        path = path + "/" + name;
        Directory dir = createDirectoryByPath(path);

        Element owner = dirElement.getChild("owner");
        User u = getUserByUsername(new String(owner.getText()));
        if (u== null)
          System.out.println("NULL USER");
        dir.setOwner(u);

        dir.xmlImport(dirElement);
      }

      for (Element plainElement: firstElement.getChildren("plain")) {
        String name = plainElement.getChild("name").getText();
        String path = plainElement.getChild("path").getText();
        path = path + "/" + name;
        PlainFile plain = createPlainFileByPath(path);

        Element owner = plainElement.getChild("owner");
        User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
        plain.setOwner(u);

        plain.xmlImport(plainElement);
      }

    } catch (UnsupportedEncodingException |  FileExistsException | UserUnknownException | ImportDocumentException e) {
      System.out.println("Error in import filesystem");
    }
  }

}

