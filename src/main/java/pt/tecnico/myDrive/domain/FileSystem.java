package pt.tecnico.myDrive.domain;
// Domain specific imports
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.services.dto.EnvironmentVariableDTO;
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
import org.jdom2.DataConversionException;

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
import java.util.Set;
import java.util.List;

import java.math.BigInteger;

public class FileSystem extends FileSystem_Base {

  private static final Logger log = LogManager.getRootLogger();


  /**
   * FileSystem temporary state variables
   * _login: keeps track of the current login in use
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
      File file = getFileByPath("/", super.getRootUser(), super.getRootDirectory());
      removeFile(file, super.getRootUser());
      super.getFilesSet().clear();
    }catch (InsufficientPermissionsException | FileUnknownException | NotADirectoryException e){
      e.printStackTrace();
    }
    for (Login login: super.getLoginsSet())
      login.remove();
    for (User u: super.getUsersSet()){
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
  public void init() {
    /**
     * Creation of root directory and home folder if there are no files,
     * means we're initializing a new filesystem
     * If the filesystem is initialized we search for the root user and
     * root directory
     */

    if (super.getFilesSet().size() == 0) {
      cleanInit();
    } else{
      log.trace("Initializing existing FileSystem");
      if (super.getRootDirectory() == null) {
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
    RootUser rootUser = new RootUser(this);
    super.setRootUser(rootUser);

    log.trace("Creating root directory ('/') ");
    RootDirectory rootDir = new RootDirectory(this, "/", super.getRootUser());
    super.setRootDirectory(rootDir);

    log.trace("Creating home directory");
    Directory homeDir = createDirectory("home",super.getRootDirectory(),super.getRootUser());

    log.trace("Creating root home");
    Directory rootHome = createDirectory(rootUser.getUsername(), homeDir, rootUser);
    rootUser.setHomeDirectory(rootHome);

    log.trace("Creating guest user");
    GuestUser guestUser = new GuestUser(this);
    super.setGuestUser(guestUser);

    log.trace("Creating guest home");
    Directory guestHome = createDirectory(guestUser.getUsername(), homeDir, rootUser);
    guestUser.setHomeDirectory(guestHome);
    guestHome.setOwner(guestUser);

  }

  /**
   *
   * @param user
   * @return True if user is the root user
   */
  public boolean isRoot(User user) {
    return user == super.getRootUser();
  }

  /**
   *
   * @param user
   * @return True if user is the root user
   */
  public boolean isGuest(User user) {
    return user == getGuestUser();
  }

  /**
   * Searches Users Set by username (since username is unique) to find a specified user.
   * Returns null if no user is found. Does not throw exception here
   */
  public User getUserByUsername(String username) {
    for (User user: super.getUsersSet()) {
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


  /* ****************************************************************************
   * |                 FileSystem's Users creation methods                       |
   * ****************************************************************************
   */
  /**
   * Creates a new user, checking for username constraints
   * Also creates its home directory
   * @return the created User
   */
  public User createUser(String username, String name, String password) {
    if (userExists(username))
      throw new UserExistsException(username);

    User user = new User(this, username, name, password);

    log.trace("Adding user " + username);
    Directory home = assertDirectory(super.getRootDirectory().getFileByName("home"));
    Directory userHome = createDirectory(username, home, user);
    user.setHomeDirectory(userHome);
    userHome.setOwner(user);


    log.trace("Added user " + username);

    return user;
  }

  /* ****************************************************************************
   * |                 FileSystem's Files creation methods                       |
   * ****************************************************************************
   */

  public int requestId() {
    super.setIdCounter(super.getIdCounter()+1);
    return super.getIdCounter();
  }

  private Directory createDirectory(String name, Directory parent, User owner) {
    return parent.createDirectory(name, owner);
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner) {
    return parent.createPlainFile(name, owner);
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner, String data) {
    return parent.createPlainFile(name, owner, data);
  }

  private App createApp(String name, Directory parent, User owner) {
    return parent.createApp(name, owner);
  }

  private Link createLink(String name, Directory parent, User owner, String data) {
    return parent.createLink(name, owner, data);
  }

  private void removeFile(File file, User user) {
    file.remove(user);
  }

  private void removeFile(String filename, User user, Directory currentDirectory) {
    currentDirectory.remove(filename, user);
  }

  /* ****************************************************************************
   * |                     Public File creation methods                         |
   * ****************************************************************************
   */

  public Directory getHomeDirectory() {
      return assertDirectory(super.getRootDirectory().getFileByName("home"));
  }

  /**
   * Changes current working directory
   */
  public void changeDirectory(String dirName, User user, Directory directory) {
    Directory d = assertDirectory(getFileByPath(dirName, user, directory));
    _login.setCurrentDirectory(d, user);
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
   * @return current working directory listing (files)
   */
  public String listDirectory(Directory directory, User user) {
    return directory.listFilesAll(user);
  }

  /**
   * @return result of executing file
   */
  public void executeFile(String path, User user, Directory directory, String[] arguments) {
    File file = getFileByPath(path, user, directory);
    if(user.isExecutable(file))
        file.execute(user, arguments);
    else
        executeWithExtensionApp(user, file, path);
  }


  public void executeWithExtensionApp(User user, File file, String path){
    //assertExtension
    String extension = file.parseExtension();
    if( extension == null) 
        throw new NoExtensionException(file.getName());

    //assertassociation
    App app = user.getAssociation(extension);
    if(app == null)
        throw new NoAssociatedAppException(extension);

    String[] arguments = {path};
        app.execute(user,arguments);
  }
  /* ****************************************************************************
   * |                          Operations by Path                              |
   * ****************************************************************************
   */

  /**
   * Get a file by its path.
   *
   * @param path
   * @param user
   * @param directory
   * @return The file at the end of the path.
   */
  public File getFileByPath(String path, User user, Directory directory) {
    if (path.length() == 0) throw new FileUnknownException(path);
    if (path.equals("/")) return super.getRootDirectory();

    ArrayList<String> tokensList = processPath(path);
    //tokensList = processEnvVars(tokensList);
    // Absolute or relative?
    if (path.charAt(0) == '/') {
      //tokensList.remove(0);
      return super.getRootDirectory().getFile(tokensList, user);
    } else {
      return directory.getFile(tokensList, user);
    }
  }

  public ArrayList<String> processPath(String path) {
    ArrayList<String> result = new ArrayList<String>();

    if(path.charAt(path.length()-1) == '/') 
        path = path.substring(0, path.length()-1);
    
    if(path.charAt(0) == '/') 
        path = path.substring(1,path.length());
    
    String[] tokens = {path};
    CharSequence cs = "/";
    if(path.contains(cs)){
      tokens = path.split("/");
    }

    for(String token : tokens){
        if(token.charAt(0) == '$') result.addAll(processEnvVars(token));
        else result.add(token);
    }
    return result;

  }

  public ArrayList<String> processEnvVars(String token){
    String envVarPath = _login.getEnvVarbyName(token.substring(1,token.length())).getValue();
    ArrayList<String> tokens = processPath(envVarPath);
      
    return tokens;
  }


  /**
   * @param path
   * @param user
   * @param directory
   * @return A string containing a simple list of files
   */

  public String listFileByPathSimple(String path, User user, Directory directory) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory d = getFileByPath(path, user, directory).accept(dv);
    return d.listFilesAll(user);
  }


  /**
   * @param path
   * @param user
   * @param directory
   * @return A string containing a list of files with all of their properties.
   */
  public String listFileByPathAll(String path, User user, Directory directory) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory d = getFileByPath(path, user, directory).accept(dv);
    return d.listFilesAll(user);
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

    for (User u: super.getUsersSet()){
      if(!u.getUsername().equals("root"))
        mydrive.addContent(u.xmlExport());
    }
    for (File f: super.getFilesSet()){

      /*if(isFileExportValid(f))*/
      mydrive.addContent(f.accept(xml));

    }
    return doc;
  }

  public void xmlImportUser(Element userElement) throws UnsupportedEncodingException {
    String username = new String(userElement.getAttribute("username").getValue());

    Element nameElement = userElement.getChild("name");
    String name;
    if (nameElement != null) name = new String(nameElement.getText());
    else name = username;

    Element pwdElement = userElement.getChild("password");
    String pwd;
    if (pwdElement != null) pwd = new String(pwdElement.getText());
    else pwd = username;

    Element permElement = userElement.getChild("mask");
    String mask;
    if (permElement != null) mask = new String(permElement.getText());
    else mask = "rwxd----";

    User u = new User(this, username, name, pwd, mask);
    Directory home = (Directory) super.getRootDirectory().getFileByName("home");
    Directory homeDir = new Directory (this, username, home, u);

    u.setHomeDirectory(homeDir);
  }

  public Directory checkPathImport(String path){
    path = path.substring(1, path.length());
    String[] folders = path.split("/");
    System.out.println("[DEBUUUUUG] " + path);
    Directory current = super.getRootDirectory();
    for (String folder : folders){
      try{
        current = assertDirectory(current.getFileByName(folder));
      } catch (FileUnknownException e){
        current = new Directory (this, folder, current, super.getRootUser());
      }
    }
    return current;
  }

  public void xmlImportDir(Element dirElement) throws UnsupportedEncodingException, DataConversionException {
    String name = new String(dirElement.getChild("name").getText());
    String path = new String(dirElement.getChild("path").getText());
    Directory parent = checkPathImport(path);

    Element owner = dirElement.getChild("owner");
    User u = getUserByUsername(new String(owner.getText()));
    Directory dir = new Directory(this, name, parent, u);

    dir.xmlImport(dirElement);
  }

  public void xmlImportPlain(Element plainElement) throws UnsupportedEncodingException, DataConversionException {
    String name = new String(plainElement.getChild("name").getText());
    String path = new String(plainElement.getChild("path").getText());
    Directory parent = checkPathImport(path);

    Element owner = plainElement.getChild("owner");
    User u = getUserByUsername(new String(owner.getText()));

    Element valueElement = plainElement.getChild("contents");
    String value = new String(valueElement.getText());

    PlainFile plain = new PlainFile(this, name, parent, u, value);

    plain.xmlImport(plainElement);
  }

  public void xmlImportLink(Element linkElement) throws UnsupportedEncodingException, DataConversionException {
    String name = new String(linkElement.getChild("name").getText());
    String path = new String(linkElement.getChild("path").getText());
    Directory parent = checkPathImport(path);

    Element owner = linkElement.getChild("owner");
    User u = getUserByUsername(new String(owner.getText()));

    Element valueElement = linkElement.getChild("value");
    String value = new String(valueElement.getText());

    Link link = new Link(this, name, parent, u, value);

    link.xmlImport(linkElement);
  }

  public void xmlImportApp(Element appElement) throws UnsupportedEncodingException, DataConversionException {
    String name = new String(appElement.getChild("name").getText());
    String path = new String(appElement.getChild("path").getText());
    Directory parent = checkPathImport(path);

    Element owner = appElement.getChild("owner");
    User u = getUserByUsername(new String(owner.getText()));

    Element valueElement = appElement.getChild("method");
    String value = new String(valueElement.getText());

    App app = new App(this, name, parent, u, value);

    app.xmlImport(appElement);
  }

  public void xmlImport(Element firstElement) {
    try {
      for (Element userElement: firstElement.getChildren("user"))
        xmlImportUser(userElement);

      for (Element dirElement: firstElement.getChildren("dir"))
        xmlImportDir(dirElement);

      for (Element plainElement: firstElement.getChildren("plain"))
        xmlImportPlain(plainElement);

      for (Element linkElement: firstElement.getChildren("link"))
        xmlImportLink(linkElement);

      for (Element appElement: firstElement.getChildren("app"))
        xmlImportApp(appElement);

    } catch (UnsupportedEncodingException | DataConversionException  e) {
      System.out.println("Error in import filesystem");
    }
  }

  /* ****************************************************************************
   * |                           Asserting methods                              |
   * ****************************************************************************
   */

  /**
   * Verifies if the file f is a directory and gets the corresponding directory
   * @return Directory corresponding to f argument, or null if its not a Directory
   */
  public Directory assertDirectory(File f) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory dir = f.accept(dv);
    if (dir == null) {
      throw new NotADirectoryException(f.getName());
    }
    return dir;
  }

  public PlainFile assertPlainFile(File f) {
    PlainFileVisitor pfv = new PlainFileVisitor();
    PlainFile pf = f.accept(pfv);
    if (pf == null)
      throw new NotAPlainFileException(f.getName());
    else
      return pf;
  }

  public App assertApp(File f) {
    AppVisitor av = new AppVisitor();
    App a = f.accept(av);
    if (a == null)
      throw new NotAAppException(f.getName());
    else
      return a;
  }

  public Link assertLink(File f) {
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
    for (Login login : super.getLoginsSet()) {
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
  private void updateSession(long token) {
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
    for (Login login: super.getLoginsSet()){
      if(login.hasExpired())
        login.remove();
    }

  }

  /**
   * Delete Login with token.
   */
  private void removeLoginByToken(long token){
    for (Login login: super.getLoginsSet()){
      if(login.compareToken(token))
        login.remove();
    }

  }


  /* ****************************************************************************
   * |                              Services                                    |
   * ****************************************************************************
   */

  public void createFile(String name, String type, String content, long token) {
    updateSession(token);

    if(content.equals("")) createFileWithoutContent(name, type, _login.getUser(), _login.getCurrentDirectory());
    else createFileWithContent(name, type, content, _login.getUser(), _login.getCurrentDirectory());
  }

  private void createFileWithoutContent(String name, String type, User user, Directory directory) {
    switch(type.toLowerCase()){
      case "directory":
        createDirectory(name, directory, user);
        break;

      case "plainfile":
        createPlainFile(name, directory, user);
        break;

      case "app":
        createApp(name, directory, user);
        break;

      case "link":
        throw new CreateLinkWithoutContentException();
      default:
        throw new UnknownTypeException(type);
    }
  }

  private void createFileWithContent(String name, String type, String data, User user, Directory directory) {
    switch(type.toLowerCase()){
      case "directory":
        throw new CreateDirectoryWithContentException();

      case "plainfile":
        createPlainFile(name, directory, user, data);
        break;

      case "app":
        App a = createApp(name, directory, user);
        a.setData(data);
        break;

      case "link":
        createLink(name, directory, user, data);
        break;
      default:
        throw new UnknownTypeException(type);
    }
  }
  /**
   * Logins a user into the filesystem, changing current directory to home directory
   */
  private long login(User user, String password) {
    if (user.verifyPassword(password)) {
      cullLogins();
      Long token = new BigInteger(64, new Random()).longValue();
      // If the token is not unique we keep generating
      while(existsToken(token)) {
        token = new BigInteger(64, new Random()).longValue();
      }

      _login = new Login(this, user, user.getHomeDirectory(), token);
      return token;
    } else { // if password was incorrect;
      throw new WrongPasswordException(user.getUsername());
    }
  }

  public long login(String username, String password) {
    log.trace("Logging in");
    if (!userExists(username)) {
      throw new UserUnknownException(username);
    }
    return login(getUserByUsername(username), password);
  }

  public String readFile(long token, String filename) {
    updateSession(token);
    File file = getFileByPath(filename, _login.getUser(), _login.getCurrentDirectory());
    PlainFile pf = assertPlainFile(file);
    return pf.getData(_login.getUser());
  }

  public void writeFile(long token, String path, String content) {
    updateSession(token);

    File file = getFileByPath(path, _login.getUser(), _login.getCurrentDirectory());

    PlainFile pf = assertPlainFile(file);
    pf.setData(content, _login.getUser());
  }

  public void deleteFile(long token, String filename) {
    updateSession(token);
    removeFile(filename, _login.getUser(), _login.getCurrentDirectory());
  }

  public String changeDirectory(long token, String dirpath) {
    updateSession(token);
    changeDirectory(dirpath, _login.getUser(), _login.getCurrentDirectory());
    return _login.getCurrentDirectory().getPath();
  }

  public String listDirectory(long token, String filepath) {
    updateSession(token);
    Directory dir = assertDirectory(getFileByPath(filepath, _login.getUser(), _login.getCurrentDirectory()));
    return listDirectory(dir, _login.getUser());
  }

  public void executeFile(long token, String filename, String[] arguments) {
    updateSession(token);
    executeFile(filename, _login.getUser(), _login.getCurrentDirectory(), arguments);
  }

  public List<EnvironmentVariableDTO> addEnvironmentVariable(long token, String name, String value){
    updateSession(token);
    if(!name.equals("") && name != null && value != null && !value.equals(""))
      _login.addEnvVar(name, value);
    return _login.listEnvVar();
  }

  public void logout(long token){
    if (!isValidToken(token)) {
      endSession();
      log.warn("Invalid Token.");
      throw new InvalidTokenException();
    }
    removeLoginByToken(token);    
  }

  /**
   * Fenix framework *sigh*
   */

  @Override
  public void setIdCounter(Integer id) {
    throw new MethodDeniedException();
  }

  @Override
  public Integer getIdCounter() {
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
  public void removeUsers(User user) {
    throw new MethodDeniedException();
  }

  @Override
  public void removeFiles(File file) {
    throw new MethodDeniedException();
  }

  @Override
  public void removeLogins(Login login) {
    throw new MethodDeniedException();
  }

  @Override
  public Set<Login> getLoginsSet() {
    throw new MethodDeniedException();
  }

  @Override
  public Set<File> getFilesSet() {
    throw new MethodDeniedException();
  }

  @Override
  public Set<User> getUsersSet() {
    throw new MethodDeniedException();
  }

  @Override
  public void setRootUser(RootUser rootUser) {
    throw new MethodDeniedException();
  }

  @Override
  public void setGuestUser(GuestUser guestUser) {
    throw new MethodDeniedException();
  }

  @Override
  public RootUser getRootUser() {
    throw new MethodDeniedException();
  }

  @Override
  public RootDirectory getRootDirectory() {
    throw new MethodDeniedException();
  }

  @Override
  public void setRootDirectory(RootDirectory rootDirectory) {
    throw new MethodDeniedException();
  }

  public static String apptest(String[] cenas){
    System.out.println("Testing app execution. Executed apptest");
    System.out.println("Provided arguments:");
    for(String s : cenas){
      System.out.println("arg -> "+s);
    }
    return "HEY!";
  }
}
