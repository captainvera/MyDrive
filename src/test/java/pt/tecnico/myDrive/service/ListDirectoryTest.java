package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;

import pt.tecnico.myDrive.services.ListDirectoryService;

import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;

import pt.tecnico.myDrive.exceptions.NotAPlainFileException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

public class ListDirectoryTest extends AbstractServiceTest {

	private FileSystem _fs;
	private User _user;
	private User _user2;
	private Login _login;
	private int _id;
	private Directory _dirdirectories;
	private Directory _dirall;
	private Directory _dirapppf;
	private Directory _dirapplink;
	private Directory _dirpflink;
	private Directory _dirapp;
	private Directory _dirpf;
	private Directory _dirlink;
	private PlainFile _pf;
	private App _app;
	private Link _link;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */
	@Override
	protected void populate() {
		try {
			_fs = FileSystem.getInstance();
			_user = new User(_fs, "litxo", "litxo", "litxo");
			_user.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"litxo", _fs.getHomeDirectory(), _user));
			_user2 = new User(_fs, "user2", "user2", "litxo");
			_user2.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"user2", _fs.getHomeDirectory(), _user));
			_login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);
			_id = 9999;


			/* We'll have something like this
			 * |- dirapp
			 *     |- app
			 * |- dirpf
			 *     |- pf
			 * |- dirlink
			 *     |- link -> ../dirdirectories/dirall
			 * |- dirdirectories
			 *     |- dirapplink
			 *     			|- app
			 *     			|- link
			 *     |- dirpflink
			 *     			|- pf
			 *     			|- link
			 *     |- dirall
			 *          |- app
			 *          |- link -> ../../dirall
			 *          |- pf
			 *          |- dirapppf
			 *              |- app
			 *              |- pf
			 *              
			 * */

			_dirapp = new Directory (_fs, _id++, "dirapp", _user.getHomeDirectory(), _user);
			_app = new App (_fs, _id++, "app", _dirapp, _user, "app_Data");

			_dirpf = new Directory (_fs, _id++, "dirpf", _user.getHomeDirectory(), _user);
			_pf = new PlainFile (_fs, _id++, "pf" , _dirpf, _user, "pf_Data");

			_dirlink = new Directory (_fs, _id++, "dirlink", _user.getHomeDirectory(), _user);
			_link = new Link (_fs, _id++, "link", _dirlink, _user, "../dirdirectories/dirall");

			_dirdirectories = new Directory (_fs, _id++, "dirdirectories", _user.getHomeDirectory(), _user);
			_dirall = new Directory (_fs, _id++, "dirall", _dirdirectories, _user);
			_dirapplink = new Directory (_fs, _id++, "dirapplink", _dirdirectories, _user);
			_dirpflink = new Directory (_fs, _id++, "dirpflink", _dirdirectories, _user);

			/*
			 * adding all tipes of files to _dirall
			 */
			_dirapppf = new Directory (_fs, _id++, "dirapppf", _dirall, _user);
			new App (_fs, _id++, "app", _dirall, _user, "app_Data");
			new PlainFile (_fs, _id++, "pf" , _dirall, _user, "pf_Data");
			new Link (_fs, _id++, "link", _dirall, _user, "../../dirall");

			/*
			 * adding app and plainfile to _dirapppf
			 */
			new App (_fs, _id++, "app", _dirapppf, _user, "app_Data");
			new PlainFile (_fs, _id++, "pf" , _dirapppf, _user, "pf_Data");

			/*
			 * adding app and plainfile to _dirapplink
			 */
			new App (_fs, _id++, "app", _dirapplink, _user, "app_Data");
			new Link (_fs, _id++, "link", _dirapplink, _user, "../../dirdirectories/dirall");

			/*
			 * adding pf and link to _dirpflink
			 */
			new PlainFile (_fs, _id++, "pf" , _dirpflink, _user, "pf_Data");
			new Link (_fs, _id++, "link", _dirpflink, _user, "../../dirdirectories/dirall");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * directory.toString()
	 * plainfile.toString()
	 * application.toString()
	 * Link.toString()
	 */

	@Test
	public void toStrings() throws Exception {
		String directory = "d " + _user.getUmask() + " " + "litxo";
		String plainfile = "- " + _user.getUmask() + " " + "pf";
		String app = "a " + _user.getUmask() + " " + "app";
		String link = "l " + _user.getUmask() + " " + "link" + "->" +  "../dirdirectories/dirall";
		assertEquals("Dir.toString() checks out.", directory, _user.getHomeDirectory().toString());
		assertEquals("pf.toString() checks out.", plainfile, _pf.toString());
		assertEquals("app.toString() checks out.", app, _app.toString());
		assertEquals("Link.toString() checks out.", link, _link.toString());
	}


	/**
	 * Listing directories with only 1 kind of file
	 */
	@Test
	public void listOrder() throws Exception {
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		Directory home =_user.getHomeDirectory();
		String self = ((String) home.toString().replaceAll(home.getName(), ".") + "\n");
		String parent = ((String) home.getParent().toString().replaceAll(home.getParent().getName(), "..") + "\n");
		String dirapp = "d " + _user.getUmask() + " " + "dirapp" + "\n";
		String dirdirectories = "d " + _user.getUmask() + " " + "dirdirectories" + "\n";
		String dirlink = "d " + _user.getUmask() + " " + "dirlink" + "\n";
		String dirpf = "d " + _user.getUmask() + " " + "dirpf" + "\n";
		String list = self + parent + dirapp + dirdirectories + dirlink + dirpf;


		assertEquals("List user1", list, result);
	}

	@Test
	public void listdirdirectories() throws Exception {
		Directory tested = _dirdirectories;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();


		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String dirapplink = _dirapplink.toString() + "\n";
		String dirpflink = _dirpflink.toString() + "\n";
		String dirall = _dirall.toString() + "\n";
		String list = self + parent + dirall + dirapplink + dirpflink;


		assertEquals("List dirdirectories", list, result);
	}

	@Test
	public void listdirall() throws Exception {
		Directory tested = _dirall;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();


		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String app = tested.getFileByName("app").toString() + "\n";
		String link = tested.getFileByName("link").toString() + "\n";
		String pf = tested.getFileByName("pf").toString() + "\n";
		String dir = tested.getFileByName("dirapppf").toString() + "\n";
		String list = self + parent + app + dir + link + pf;


		assertEquals("List dirall", list , result);
	}

	@Test
	public void listdirapp() throws Exception {
		Directory tested = _dirapp;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String app = tested.getFileByName("app").toString() + "\n";
		String list = self + parent + app;


		assertEquals("List dirapp", list, result);
	}

	@Test
	public void listdirpf() throws Exception {
		Directory tested = _dirpf;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String pf = tested.getFileByName("pf").toString() + "\n";
		String list = self + parent + pf;

		assertEquals("List dirapp", list, result);
	}

	@Test
	public void listdirlink() throws Exception {
		Directory tested = _dirlink;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String link = tested.getFileByName("link").toString() + "\n";
		String list = self + parent + link;


		assertEquals("List dirapp", list , result);
	}

	@Test
	public void listdirapppf() throws Exception {
		Directory tested = _dirapppf;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String app = tested.getFileByName("app").toString() + "\n";
		String pf = tested.getFileByName("pf").toString() + "\n";
		String list = self + parent + app + pf;

		assertEquals("List dirdapppf", list , result);
	}

	@Test
	public void listdirapplink() throws Exception {
		Directory tested = _dirapplink;
		_login.setCurrentDirectory(tested);
		ListDirectoryService lds = new ListDirectoryService(123l);
		lds.execute();
		String result = lds.result();

		String self = ((String) tested.toString().replaceAll(tested.getName(), ".") + "\n");
		String parent = ((String) tested.getParent().toString().replaceAll(tested.getParent().getName(), "..") + "\n");
		String app = tested.getFileByName("app").toString() + "\n";
		String link = tested.getFileByName("link").toString() + "\n";
		String list = self + parent + app + link;

		assertEquals("List dirdapplink", list , result);
	}

	//  @Test
	//  public void listdirpflink() throws Exception {
	//  	_login.setCurrentDirectory(_dirpflink);
	//  	ListDirectoryService lds = new ListDirectoryService(123l);
	//  	lds.execute();
	//  	String result = lds.result();
	//
	//  	assertEquals("List dirpflink", _dirpflink.listFilesSimple() , result);
	//  }
	//  




}
