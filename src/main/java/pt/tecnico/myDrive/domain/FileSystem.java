package pt.tecnico.myDrive.domain;

// Domain specific imports
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.ist.fenixframework.DomainRoot;

// Domain Exceptions
import pt.tecnico.myDrive.exceptions.*;

// Domain visitors
import pt.tecnico.myDrive.visitors.DirectoryVisitor;
import pt.tecnico.myDrive.visitors.PlainFileVisitor;
import pt.tecnico.myDrive.visitors.AppVisitor;
import pt.tecnico.myDrive.visitors.LinkVisitor;
import pt.tecnico.myDrive.visitors.XMLExporterVisitor;

// Jdom2
import org.jdom2.Element;
import org.jdom2.Document;

// IO
import java.io.UnsupportedEncodingException;

// Fenix Framework
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

// JodaTime
import org.joda.time.DateTime;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

// Util imports
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

import java.math.BigInteger;

public class FileSystem extends FileSystem_Base {

  private static final Logger log = LogManager.getRootLogger();


  /**
   * FileSystem temporary state variables
   * _loggedUser: keeps track of the user logged in
   * _currentDirectory: keeps track of the current navigation directory
   */

  private Login _login;

  private FileSystem() {
    log.trace("Constructing new FileSystem");
    super.setRoot(FenixFramework.getDomainRoot());
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

  public void cleanup() {
    try{
      File file = getFileByPath("/", getRootUser(), getRootDirectory());
      removeFile(file);
      getFilesSet().clear();
    }catch (InsufficientPermissionsException | FileUnknownException | NotADirectoryException e){
      e.printStackTrace();
    }
    for (Login login: getLoginsSet())
      login.remove();
    for (User u: getUsersSet()){
      u.remove();
    }
  }

  /**
   * Resets the filesystem. Wipes all data stored in it and creates its initial
   * data.
   */
  @Atomic
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
      if (getRootDirectory() == null) {
        throw new RootDirectoryNotFoundException();
      }
    }
    log.trace("Finished FileSystem initialization");
  }


  /**
   * Creates the initial data for the filesystem, such as, root user, root
   * directory and root's user home directory.
   */
  private void cleanInit() {
    log.trace("Initializing new FileSystem");
    super.setIdCounter(0);

    log.trace("Creating root user");
    super.setRootUser(new RootUser(this));

    log.trace("Creating root directory");
    super.setRootDirectory(new RootDirectory(this, 0, "/", getRootUser()));


    log.trace("Creating home directory");
    Directory homeDir = createDirectory("home",getRootDirectory(),getRootUser());

    getRootUser().setHomeDirectory(createDirectory("root", homeDir, getRootUser()));

  }

  /**
   *
   * @param user
   * @return True if user is the root user
   */
  private boolean isRoot(User user) {
    return user == getRootUser();
  }

  /**
   * Searches Users Set by username (since username is unique) to find a specified user.
   * Returns null if no user is found. Does not throw exception here
   */
  public User getUserByUsername(String username) {
    for (User user: this.getUsersSet()) {
      if (user.getUsername().equalsIgnoreCase(username)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Searches Logins Set by User's username (since username is unique) to find a specific Login.
   * Returns null if no login is found.
   */
  private Login getLoginByUser(User user){
    for(Login login: this.getLoginsSet()){
      if(login.getUser().equals(user))
        return login;
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

  @Override
  public void setIdCounter(Integer id) {
    throw new MethodDeniedException();
  }

  @Override
  public void setRoot(DomainRoot dr) {
    throw new MethodDeniedException();
  }

  @Override
  public void addUsers(User user) {
    throw new MethodDeniedException();
  }

  @Override
  public void addFiles(File file){
    throw new MethodDeniedException();
  }

  @Override
  public void addLogins(Login login){
    throw new MethodDeniedException();
  }

  @Override
  public void setRootUser(RootUser rootUser) {
    throw new MethodDeniedException();
  }

  @Override
  public void setRootDirectory(RootDirectory rootDirectory) {
    throw new MethodDeniedException();
  }



  /* ****************************************************************************
   * |                 FileSystem's Users creation methods                       |
   * ****************************************************************************
   */
  /**
   * Creates a new user, checking for username constraints
   * Also creates its home directory
   * @return the created User
   */
  public User createUser(String username, String name, String password)
    throws UserExistsException, InvalidUsernameException {

    checkUsername(username);

    if (userExists(username))
      throw new UserExistsException(username);

    User user = new User(this, username, name, password);

    /**
     * TODO: Solve error throwing here. Exceptions shouldn't happen;
     * Should be handled elsewhere
     */
    log.trace("Adding user " + username);
    try {
      Directory home = assertDirectory(getRootDirectory().getFileByName("home"));
      Directory userHome = createDirectory(username, home, user);
      user.setHomeDirectory(userHome);
    } catch(FileUnknownException e) {
      System.out.println(e.getMessage());
    } catch(NotADirectoryException e) {
      System.out.println(e.getMessage());
    }


    log.trace("Added user " + username);

    return user;
  }

  /* ****************************************************************************
   * |                 FileSystem's Files creation methods                       |
   * ****************************************************************************
   */

  public int requestId() {
    super.setIdCounter(getIdCounter()+1);
    return getIdCounter();
  }

  private Directory createDirectory(String name, Directory parent, User owner) {
    Directory dir = new Directory(this, requestId(), name, parent, owner);
    return dir;
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner) {
    PlainFile pf = new PlainFile(this, requestId(), name, parent, owner);
    return pf;
  }

  private App createApp(String name, Directory parent, User owner) {
    App app = new App(this, requestId(), name, parent, owner);
    return app;
  }

  private Link createLink(String name, Directory parent, User owner, String data) {
    Link link = new Link(this, requestId(), name, parent, owner, data);
    return link;
  }

  private void removeFile(File f) {
    f.remove();
  }

  /* ****************************************************************************
   * |                     Public File creation methods                         |
   * ****************************************************************************
   */

  public Directory createDirectory(String name, User user, Directory directory)
    throws InvalidFilenameException, InsufficientPermissionsException, FileExistsException {
    checkFilename(name);
    checkFileUnique(name, directory);
    // Write permissions
    return createDirectory(name,directory,user);
  }

  public PlainFile createPlainFile(String name, User user, Directory directory)
    throws InvalidFilenameException, InsufficientPermissionsException, FileExistsException {
    checkFilename(name);
    checkFileUnique(name, directory);
    // Write permissions
    return createPlainFile(name,directory,user);
  }

  public App createApp(String name, User user, Directory directory)
    throws InvalidFilenameException, InsufficientPermissionsException, FileExistsException {
    checkFilename(name);
    checkFileUnique(name, directory);
    // Write permissions
    return createApp(name,directory,user);
  }

  public Link createLink(String name, String data, User user, Directory directory)
    throws InvalidFilenameException, InsufficientPermissionsException, FileExistsException {
    checkFilename(name);
    checkFileUnique(name, directory);
    // Write permissions
    return createLink(name,directory,user,data);
  }

  /**
   * Finds Root Directory
   * Does not throw exception if Root is not found
   * TODO: Should throw exception
   */
  public RootDirectory getRootDirectory() {
    Directory dir;
    DirectoryVisitor dv = new DirectoryVisitor();
    for (File f: getFilesSet()) {
      dir = f.accept(dv);
      if (dir != null && dir.isTopLevelDirectory())
        return (RootDirectory) dir;
    }

    /**
     * No root directory found, means either corrupted or
     * empty filesystem
     */

    return null;
  }

  public Directory getHomeDirectory() {
  	/**
  	 * TODO:XXX:FIXME DO PROPER CHECKING AND EXCEPTION HANDLING
  	 */
  	try {
    	return (Directory) getRootDirectory().getFileByName("home");
  	}catch(FileUnknownException e){
  		throw new RuntimeException("WRONG FILE STRUCTURE");
  	}
  }

  /**
   * Changes current working directory
   */
  public void changeDirectory(String dirName, User user, Directory directory)
    throws FileUnknownException, NotADirectoryException, InsufficientPermissionsException, NotALinkException {
    final Directory dir = directory ;
    /**
     * TODO::FIX:PERMISSIONS
     */
    //checkExecutionPermissions(user, dir);
    _login.setCurrentDirectory(dir);
  }


  /**
   * ----------------------------------------------------
   * Public FileSystem usage methods
   * Allows execution of several commands on the current FileSystem
   */

  /**
   * @return current working directory path
   */
  public String listPath(Directory directory) {
    return directory.getPath();
  }

  /**
   * @return current working directory name
   */
  public String currentDirectory(Directory directory) {
    return directory.getName();
  }

  /**
   * @return current working directory listing (files)
   */
  public String listDirectory(Directory directory)
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    return directory.listFilesAll();
  }

  /**
   * @return result of executing file
   */
  public String executeFile(String path, User user, Directory directory) throws NotADirectoryException, FileUnknownException, InsufficientPermissionsException, NotALinkException {
    File file = getFileByPath(path, user, directory);
    /**
     * TODO::XXX:FIX PERMISSIONS
     */
    // checkExecutionPermissions(user, file);
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
  public File getFileByPath(String path, User user, Directory directory)
    throws FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
    if (path.equals("/")) return getRootDirectory();

    String[] tokens = path.split("/");

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
  
    Directory current = null;

    if (path.charAt(0) == '/') {
      current = getRootDirectory();
      tokensList.remove(0);
      
      return getRootDirectory().getFile(tokensList, user);
    } else{
      return directory.getFile(tokensList, user);
    }
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

  public String listFileByPathSimple(String path, User user, Directory directory) throws
    IllegalAccessException, FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
      DirectoryVisitor dv = new DirectoryVisitor();
      Directory d = getFileByPath(path, user, directory).accept(dv);
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
  public String listFileByPathAll(String path, User user, Directory directory) throws
    IllegalAccessException, FileUnknownException, NotADirectoryException,
    InsufficientPermissionsException {
      DirectoryVisitor dv = new DirectoryVisitor();
      Directory d = getFileByPath(path, user, directory).accept(dv);
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
  public void removeFileByPath(String path, User user, Directory directory) throws
    FileUnknownException, NotADirectoryException, InsufficientPermissionsException {
      File file = getFileByPath(path, user, directory);
      // Deletion permissions
      /** checkDeletionPermissions(user, file); */
      removeFile (file);
    }

  /**
   * Helper function to call when the directories in the path need to be processed/created
   */
  private Directory createFileByPathHelper(Directory current, ArrayList<String> tokensList, User user)
    throws InsufficientPermissionsException {
    File temp ;
    DirectoryVisitor dv = new DirectoryVisitor();

    for (String tok : tokensList) {
      try {
        temp = current.getFileByName(tok);
        // Permissions
        /** checkReadPermissions(user, current); */
        /** checkExecutionPermissions(user, current); */
        current = temp.accept(dv);
      } catch(FileUnknownException e) {
        // Permissions
        /** checkWritePermissions(user, current); */
        current = createDirectory(tok, current, getRootUser());
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
  public PlainFile createPlainFileByPath(String path, User user, Directory directory)
    throws FileExistsException, InsufficientPermissionsException, FileExistsException {
    Directory current;
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    if (path.charAt(0) == '/') {
      current = getRootDirectory();
      tokensList.remove(0);
    }else{
      current = directory;
    }

    Directory currentDir = createFileByPathHelper(current, tokensList, user);
    checkFileUnique(target, currentDir);
    return createPlainFile(target, currentDir, getRootUser());
  }

  /**
   * Create a Directory by its path.
   *
   * @param path
   * @return The file created at the end of the path.
   * @throws FileExistsException
   * @throws InsufficientPermissionsException
   */
  public Directory createDirectoryByPath(String path, User user, Directory directory)
    throws FileExistsException, InsufficientPermissionsException {
    Directory current;
    String[] tokens = path.split("/");
    String target = tokens[tokens.length-1];

    ArrayList<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
    tokensList.remove(tokensList.size()-1);

    if (path.charAt(0) == '/') {
      current = getRootDirectory();
      tokensList.remove(0);
    }else{
      current = directory;
    }

    Directory currentDir = createFileByPathHelper(current, tokensList, user);
    checkFileUnique(target, currentDir);
    try {
      DirectoryVisitor dv = new DirectoryVisitor();
      return currentDir.getFileByName(target).accept(dv);
    }
    catch (FileUnknownException e) {
      return createDirectory(target, currentDir, getRootUser());
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
      Directory dir = createDirectoryByPath(path, getRootUser(), getRootDirectory());

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
      PlainFile plain = createPlainFileByPath(path, getRootUser(), getRootDirectory());

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
      if (!Character.isLetter(c) && !Character.isDigit(c)
          && c == 0 && c == '\\') {
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
   * Verifies if filename is unique in Directory
   * @param filename
   * @throws InvalidFilenameException
   */
  private void checkFileUnique(String filename, Directory dir) throws FileExistsException {
    if(dir.hasFile(filename)) throw new FileExistsException(filename);
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

  public PlainFile assertPlainFile(File f) throws NotAPlainFileException {
    PlainFileVisitor pfv = new PlainFileVisitor();
    PlainFile pf = f.accept(pfv);
    if (pf == null)
      throw new NotAPlainFileException(f.getName());
    else
      return pf;
  }

  public App assertApp(File f) throws NotAAppException {
    AppVisitor av = new AppVisitor();
    App a = f.accept(av);
    if (a == null)
      throw new NotAAppException(f.getName());
    else
      return a;
  }

  public Link assertLink(File f) throws NotALinkException {
    LinkVisitor lv = new LinkVisitor();
    Link l = f.accept(lv);
    if (l == null) {
      throw new NotALinkException(f.getName());
    }
    return l;
  }


  /* ****************************************************************************
   * |                           Token Handling                                 |
   * ****************************************************************************
   */

  /**
   * @param token
   * @return The login which holds token except if it doesn't exist, in that
   * case, null is returned.
   */
  public Login getLoginByToken(long token) {
    for (Login login : getLoginsSet()) {
      if (login.compareToken(token))
        return login;
    }
    return null;
  }

  /**
   * @param token
   * @return The user which holds token, except if it doesn't exist, in that
   * case, null is returned.
   *
   */
  public User getUserByToken(long token) {
    Login login = getLoginByToken(token);
    return login != null ? login.getUser() : null;
  }


  /**
   * A token is unique if there's no login that holds it
   * @param token
   * @return
   */
  public boolean existsToken(long token) {
    return getLoginByToken(token) != null;
  }

  /**
   * Checks whether a token is valid or not.
   * A token is valid if it has a login which holds it and hasn't expired
   *
   * @param token
   * @return
   */
  public boolean isValidToken(long token) {
    Login login = getLoginByToken(token);
    return login != null && !login.hasExpired();
  }


  /**
   * Checks the validity of a token
   *
   * @param token
   * @return Returns true if the login which holds token hasn't expired, false
   * otherwise
   */
  private void updateSession(long token) throws InvalidTokenException {
    if (!isValidToken(token)) {
      endSession();
      log.warn("Invalid Token.");
      throw new InvalidTokenException();
    }

    Login login = getLoginByToken(token);
    initSession(login);
  }

  private void endSession() {
    _login = null;
  }

  private void initSession(Login login) {
    _login = login;
    login.extendToken();
  }


  /**
   * Cleans up expired logins.
   */
  private void cullLogins(){
    for (Login login: this.getLoginsSet()){
      if(login.hasExpired())
        login.remove();
    }

  }

  /* ****************************************************************************
   * |                              Services                                    |
   * ****************************************************************************
   */

  public void createFile(String name, String type, String content, long token)
  throws CreateLinkWithoutContentException, CreateDirectoryWithContentException, InvalidTokenException, InsufficientPermissionsException, InvalidFilenameException, FileExistsException{
    updateSession(token);
    if(content == null) createFileWithoutContent(name, type, _login.getUser(), _login.getCurrentDirectory());
    else createFileWithContent(name, type, content, _login.getUser(), _login.getCurrentDirectory());
  }

  private void createFileWithoutContent(String name, String type, User user, Directory directory)
  throws CreateLinkWithoutContentException, InsufficientPermissionsException, InvalidFilenameException, FileExistsException{
    switch(type.toLowerCase()){
      case "directory":
        createDirectory(name, user, directory);
        break;

      case "plainfile":
        createPlainFile(name, user, directory);
        break;

      case "app":
        createApp(name, user, directory);
        break;

      case "link":
        throw new CreateLinkWithoutContentException();
    }
  }

  private void createFileWithContent(String name, String type, String content, User user, Directory directory)
  throws CreateDirectoryWithContentException, InsufficientPermissionsException, InvalidFilenameException, FileExistsException{
    switch(type.toLowerCase()){
      case "directory":
        throw new CreateDirectoryWithContentException();

      case "plainfile":
        PlainFile pf = createPlainFile(name, user, directory);
        pf.setData(content);
        break;

      case "app":
        App a = createApp(name, user, directory);
        a.setData(content);
        break;

      case "link":
        createLink(name, content, user, directory);
        break;
    }
  }
  /**
   * Logins a user into the filesystem, changing current directory to home directory
   */
  private long login(User user, String password) throws WrongPasswordException {
    if (user.verifyPassword(password)) {
      cullLogins();
      Long token = new BigInteger(64, new Random()).longValue();
      // If the token is not unique we keep generating
      while(existsToken(token)) {
        token = new BigInteger(64, new Random()).longValue();
      }

      _login = new Login(this, user, user.getHomeDirectory(), token);
      super.addLogins(_login);
      return token;
    } else { // if password was incorrect;
      throw new WrongPasswordException(user.getUsername());
    }
  }

  public long login(String username, String password) throws UserUnknownException, WrongPasswordException {
    log.trace("Logging in");
    if (!userExists(username)) {
      throw new UserUnknownException(username);
    }
    return login(getUserByUsername(username), password);
  }

  public String readFile(long token, String filename) throws
    NotAPlainFileException, InvalidTokenException, FileUnknownException,
    InsufficientPermissionsException, NotADirectoryException, NotALinkException {
      updateSession(token);
      File file = getFileByPath(filename, _login.getUser(), _login.getCurrentDirectory());
      // Check read permissions
      /** checkReadPermissions(_login.getUser(), file); */
      PlainFile pf = assertPlainFile(file);
      return pf.getData();
    }

  public void writeFile(long token, String path, String content)
    throws NotAPlainFileException, InvalidTokenException, FileUnknownException,
    InsufficientPermissionsException, NotALinkException {
	updateSession(token);

  }
  
  public void deleteFile(long token, String filename)
      throws InvalidTokenException, FileUnknownException,
      InsufficientPermissionsException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
  	updateSession(token);
  	removeFile(_login.getCurrentDirectory().getFileByName(filename));
  	//TODO::FIXME check permissions on remove 
    }

  public String changeDirectory(long token, String dirpath) throws
    FileUnknownException, NotADirectoryException, InsufficientPermissionsException, InvalidTokenException, NotALinkException {
      updateSession(token);
      changeDirectory(dirpath, _login.getUser(), _login.getCurrentDirectory());
      return _login.getCurrentDirectory().getPath();
  }

  public String listFile(long token, String path){
    return " ";
  }
}
