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
    _rootDirectory = createRootDirectory();


    log.trace("Creating home directory");
    Directory homeDir = createDirectory("home",_rootDirectory,_rootUser);

    _rootUser.setHomeDirectory(createDirectory("root", homeDir, _rootUser));

    _currentDirectory = _rootDirectory;
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
   *
   * @param user
   * @return True if user is the root user
   */
  private boolean isRoot(User user) {
    return user == _rootUser;
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

    checkUsername(username);

    if (userExists(username))
      throw new UserExistsException(username);

    user.setFileSystem(this);
    user.setUsername(username);
    user.setPassword(password);
    user.setUmask("rwxd----");
    user.setName(name);

    /**
     * TODO: Solve error throwing here. Exceptions shouldn't happen;
     * Should be handled elsewhere
     */
    try {
      Directory home = assertDirectory(_rootDirectory.getFileByName("home"));
      Directory userHome = createDirectory(username, home, user);
      user.setHomeDirectory(userHome);
    } catch(FileUnknownException e) {
      System.out.println(e.getMessage());
    } catch(NotADirectoryException e) {
      System.out.println(e.getMessage());
    }

    log.trace("Added user " + username);
    addUsers(user);

    return user;
  }

  // TODO: Refactor methods below

  /**
   * Creates Root User
   * It's home directory isn't created here to avoid conflicts in FileSystem init
   */

  public User createRootUser() {
    User user =
      User.UserBuilder.create()
      .withFileSystem(this)
      .withUsername("root")
      .withName("Super User")
      .withPassword("***")
      .withUmask("rwxdr-x-")
      .build();

    System.out.println("Size: " + getUsersSet().size());
    return user;
  }

  /* ****************************************************************************
   * |                 FileSystem's File creation methods                       |
   * ****************************************************************************
   */

  public Directory createRootDirectory() {
    RootDirectory rd =
      RootDirectory.RootDirectoryBuilder.create()
      .withId(getIdCounter())
      .withName("/")
      .withOwner(_rootUser)
      .withFileSystem(this)
      .build();

    return rd;
  }

  private Directory createDirectory(String name, Directory parent, User owner) {
    setIdCounter(getIdCounter()+1);
    Directory dir =
      Directory.DirectoryBuilder.create()
      .withId(getIdCounter())
      .withOwner(owner)
      .withParent(parent)
      .withName(name)
      .withFileSystem(this)
      .build();

    return dir;
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner) {
    setIdCounter(getIdCounter()+1);
    PlainFile pf =
      PlainFile.PFBuilder.create()
      .withId(getIdCounter())
      .withOwner(owner)
      .withParent(parent)
      .withName(name)
      .withFileSystem(this)
      .build();
    return pf;
  }

  private App createApp(String name, Directory parent, User owner) {
    setIdCounter(getIdCounter()+1);
    App app =
      App.AppBuilder.create()
      .withId(getIdCounter())
      .withOwner(owner)
      .withParent(parent)
      .withName(name)
      .withFileSystem(this)
      .build();
    return app;
  }

  private Link createLink(String name, Directory parent, User owner) {
    setIdCounter(getIdCounter()+1);
    Link link =
      Link.LinkBuilder.create()
      .withId(getIdCounter())
      .withOwner(owner)
      .withParent(parent)
      .withName(name)
      .withFileSystem(this)
      .build();
    return link;
  }

  private void removeFile(File f) {
    f.remove();
  }

  /* ****************************************************************************
   * |                     Public File creation methods                         |
   * ****************************************************************************
   */

  public Directory createDirectory(String name)
    throws InvalidFilenameException, InsufficientPermissionsException {
    checkFilename(name);
    checkWritePermissions(_loggedUser, _currentDirectory);
    return createDirectory(name,_currentDirectory,_loggedUser);
  }

  public PlainFile createPlainFile(String name)
    throws InvalidFilenameException, InsufficientPermissionsException {
    checkFilename(name);
    checkWritePermissions(_loggedUser, _currentDirectory);
    return createPlainFile(name,_currentDirectory,_loggedUser);
  }

  public App createApp(String name)
    throws InvalidFilenameException, InsufficientPermissionsException {
    checkFilename(name);
    checkWritePermissions(_loggedUser, _currentDirectory);
    return createApp(name,_currentDirectory,_loggedUser);
  }

  public Link createLink(String name)
    throws InvalidFilenameException, InsufficientPermissionsException {
    checkFilename(name);
    checkWritePermissions(_loggedUser, _currentDirectory);
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
  public void changeDirectory(String dirName)
    throws FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
    Directory dir = assertDirectory(_currentDirectory.getFileByName(dirName));
    checkExecutionPermissions(_loggedUser, dir);
    _currentDirectory = dir;
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
  public String executeFile(String filename) throws FileUnknownException, InsufficientPermissionsException {
    File file = _currentDirectory.getFileByName(filename);
    checkExecutionPermissions(_loggedUser, file);
    return file.execute();
  }

  /* ****************************************************************************
   * |                          Operations by Path                              |
   * ****************************************************************************
   */

  /**
   * Get a file by its path.
   *
   * @param path
   * @return The file at the end of the path.
   * @throws FileUnknownException
   * @throws InsufficientPermissionsException
   * @throws NotADirectoryException
   */
  public File getFileByPath(String path)
    throws FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
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
      checkReadPermissions(_loggedUser, current);
      checkExecutionPermissions(_loggedUser, current);
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
   * @param path
   * @return A string containing a simple list of files
   *
   * @throws IllegalAccessException
   * @throws FileUnknownException
   * @throws NotADirectoryException
   * @throws NoSuchMethodException
   * @throws InsufficientPermissionsException
   * @throws InvocationTargetException
   */
  public String listFileByPathSimple(String path) throws
    IllegalAccessException, FileUnknownException, NotADirectoryException,
    NoSuchMethodException, InvocationTargetException, InsufficientPermissionsException {
      DirectoryVisitor dv = new DirectoryVisitor();
      Directory d = getFileByPath(path).accept(dv);
      return d.listFilesSimple();
    }


  /**
   * @param path
   * @return A string containing a list of files with all of their properties.
   *
   * @throws IllegalAccessException
   * @throws FileUnknownException
   * @throws NotADirectoryException
   * @throws NoSuchMethodException
   * @throws InsufficientPermissionsException
   * @throws InvocationTargetException
   */
  public String listFileByPathAll(String path) throws
    IllegalAccessException, FileUnknownException, NotADirectoryException,
    NoSuchMethodException, InvocationTargetException, InsufficientPermissionsException {
      DirectoryVisitor dv = new DirectoryVisitor();
      Directory d = getFileByPath(path).accept(dv);
      return d.listFilesAll();
    }

  /**
   * remove a file by its path.
   *
   * @param path
   * @throws FileUnknownException
   * @throws InsufficientPermissionsException
   * @throws NotADirectoryException
   */
  public void removeFileByPath(String path) throws
    FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
      File file = getFileByPath(path);
      checkDeletionPermissions(_loggedUser, file);
      removeFile (file);
    }

  /**
   * Helper function to call when the directories in the path need to be processed/created
   */
  private Directory createFileByPathHelper(Directory current, ArrayList<String> tokensList)
    throws InsufficientPermissionsException {
    File temp ;
    DirectoryVisitor dv = new DirectoryVisitor();

    for (String tok : tokensList) {
      try {
        temp = current.getFileByName(tok);
        checkReadPermissions(_loggedUser, current);
        checkExecutionPermissions(_loggedUser, current);
        current = temp.accept(dv);
      } catch(FileUnknownException e) {
        checkWritePermissions(_loggedUser, current);
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
   * @throws InsufficientPermissionsException
   */
  public PlainFile createPlainFileByPath(String path)
    throws FileExistsException, InsufficientPermissionsException {
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
   * @throws InsufficientPermissionsException
   */
  public Directory createDirectoryByPath(String path)
    throws FileExistsException, InsufficientPermissionsException {
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
   * @throws FileExistsException
   * @throws InsufficientPermissionsException
   */
  public void createAppByPath(String path)
    throws FileExistsException, InsufficientPermissionsException {
    /* Copy code from create'File'ByPath */
  }

  /**
   * Create a Link by its path.
   *
   * @param path
   * @throws FileExistsException
   * @throws InsufficientPermissionsException
   */
  public void createLinkByPath(String path)
    throws FileExistsException, InsufficientPermissionsException {
    /* Copy code from create'File'ByPath */
  }

  /**
   * Verifies if a file is valid to export
   *
   * @param File
   */
  public Boolean isFileExportValid(File f) {
    DirectoryVisitor isDirectory = new DirectoryVisitor();
    if(f.getOwner().getUsername().equals("root")){
      Directory dir = f.accept(isDirectory);
      if(dir != null){
        if(dir.getSize() != 2) return false;
      }
    }
    return true;
  }

  /**
   * Creates a document, with the data in the FileSystem, in XML
   */
  public Document xmlExport() {
    XMLExporterVisitor xml = new XMLExporterVisitor();
    Element mydrive = new Element("myDrive");
    Document doc = new Document(mydrive);

    for (User u: getUsersSet()){
      if(!u.getUsername().equals("root"))
        mydrive.addContent(u.xmlExport());
    }
    for (File f: getFilesSet()){
      if(isFileExportValid(f)) mydrive.addContent(f.accept(xml));
    }
    return doc;
  }

  public void xmlImportUser(Element userElement) throws UnsupportedEncodingException, UserExistsException, InvalidUsernameException{
    String username = new String(userElement.getAttribute("username").getValue().getBytes("UTF-8"));

    Element nameElement = userElement.getChild("name");
    String name;
    if (nameElement != null) name = new String(nameElement.getText().getBytes("UTF-8"));
    else name = username;

    Element pwdElement = userElement.getChild("password");
    String pwd;
    if (pwdElement != null) pwd = new String(pwdElement.getText().getBytes("UTF-8"));
    else pwd = username;

    createUser(username,name,pwd);
  }

  public void xmlImportDir(Element dirElement) throws
    UnsupportedEncodingException, UserUnknownException, ImportDocumentException,
    FileExistsException, InsufficientPermissionsException {
      String name = new String(dirElement.getChild("name").getText().getBytes("UTF-8"));
      String path = new String(dirElement.getChild("path").getText().getBytes("UTF-8"));
      path = path + "/" + name;
      Directory dir = createDirectoryByPath(path);

      Element owner = dirElement.getChild("owner");
      User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
      dir.setOwner(u);

      dir.xmlImport(dirElement);
    }

  public void xmlImportPlain(Element plainElement) throws
    UnsupportedEncodingException, UserUnknownException, ImportDocumentException,
    FileExistsException, InsufficientPermissionsException {
      String name = new String(plainElement.getChild("name").getText().getBytes("UTF-8"));
      String path = new String(plainElement.getChild("path").getText().getBytes("UTF-8"));
      path = path + "/" + name;
      PlainFile plain = createPlainFileByPath(path);

      Element owner = plainElement.getChild("owner");
      User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8")));
      plain.setOwner(u);

      plain.xmlImport(plainElement);
    }

  public void xmlImport(Element firstElement) throws InsufficientPermissionsException {
    try {
      for (Element userElement: firstElement.getChildren("user"))
        xmlImportUser(userElement);

      for (Element dirElement: firstElement.getChildren("dir"))
        xmlImportDir(dirElement);

      for (Element plainElement: firstElement.getChildren("plain"))
        xmlImportPlain(plainElement);

    } catch (UnsupportedEncodingException |  FileExistsException | UserUnknownException | ImportDocumentException | UserExistsException | InvalidUsernameException e) {
      System.out.println("Error in import filesystem");
    }
  }

  /* ****************************************************************************
   * |                            Checking methods                              |
   * ****************************************************************************
   */

  /**
   * Verifies if username only contains letters and digits
   * @param username
   * @throws InvalidUsernameException
   */
  private void checkUsername(String username) throws InvalidUsernameException {
    char[] characters = username.toCharArray();

    for (char c: characters) {
      if (!Character.isLetter(c) && !Character.isDigit(c)) {
        throw new InvalidUsernameException(username);
      }
    }
  }

  /**
   * Verifies if filename only contains letters and digits
   * @param filename
   * @return
   */
  private void checkFilename(String filename) throws InvalidFilenameException {
    char[] characters = filename.toCharArray();

    for (char c: characters) {
      if (!Character.isLetter(c) && !Character.isDigit(c)) {
        throw new InvalidFilenameException(filename);
      }
    }
  }

  /**
   * Verifies if username has atleast 3 characters
   * @param username
   * @throws InvalidUsernameSizeException
   */
  private void checkUsernameSize(String username) throws InvalidUsernameSizeException {
    if(username.length() <= 3) throw new InvalidUsernameSizeException(3);
  }

  /**
   * Verifies if filepath has atmost 1024 characters
   * @param filepath
   * @throws InvalidFilepathSizeException
   */
  private void checkFilepathSize(String filepath) throws InvalidFilepathSizeException {
    if(filepath.length() >= 1024) throw new InvalidFilepathSizeException(1024);
  }

  /**
   * Verifies if user has permission to perform some operation on file
   *
   * @param user
   * @param file
   * @param index
   * @param c
   * @throws InsufficientPermissionsException
   */
  private void checkPermissions(User user, File file, int index, char c)
    throws InsufficientPermissionsException {
    String permissions = getPermissions(user, file);
    if(permissions.charAt(index) != c)
      throw new InsufficientPermissionsException();
  }

  private void checkReadPermissions(User user, File file) throws InsufficientPermissionsException {
    checkPermissions(user, file, 0, 'r');
  }

  private void checkWritePermissions(User user, File file) throws InsufficientPermissionsException {
    checkPermissions(user, file, 1, 'w');
  }

  private void checkExecutionPermissions(User user, File file) throws InsufficientPermissionsException {
    checkPermissions(user, file, 2, 'x');
  }

  private void checkDeletionPermissions(User user, File file) throws InsufficientPermissionsException {
    checkPermissions(user, file, 3, 'd');
  }

  private String getPermissions(User user, File file) {
    if (isRoot(user))
      return "rwdx";
    else if (file.getOwner() == user)
      return file.getUserPermission();
    else
      return file.getOthersPermission();
  }

  /* ****************************************************************************
   * |                           Asserting methods                              |
   * ****************************************************************************
   */

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

}
