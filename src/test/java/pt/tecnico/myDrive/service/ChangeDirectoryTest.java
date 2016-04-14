/**
 *
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import pt.tecnico.myDrive.services.ChangeDirectoryService;

public class ChangeDirectoryTest extends AbstractServiceTest {

	private FileSystem _fs;
  private User _user1;
	private User _user2;
  private Login _login1;
	private int _id;
	private Directory dir1;
	private Directory dir2;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */

	@Override
	protected void populate() {
		try{
			_fs = FileSystem.getInstance();
			_user1 = new User(_fs, "user1", "user1", "litxo");
      _user1.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"user1", _fs.getHomeDirectory(), _user1));
			_user2 = new User(_fs, "user2", "user2", "litxo");
      _user2.setHomeDirectory(new Directory(_fs, _fs.requestId(),
					"user2", _fs.getHomeDirectory(), _user2));
			_login1 = new Login(_fs, _user1, _user1.getHomeDirectory(), 123l);
			_id = 9999;

			/* We'll have something like this
			 * |- app
			 * |- pf
			 * |- linkAbsolute
			 * |- linkRelative
			 * |- linkToNotADirectory
			 * |- linkToInsufficientPermissions
			 * |- linkToFileUnknown
			 * |- dir1
			 *     |- plainfile1
			 *     |- linkToLink
			 *     |- linkToLinkBad
			 *     |- dir2
			 *          |- plainfile2
			 * */

			dir1 = new Directory (_fs, _id++, "dir1", _user1.getHomeDirectory(), _user1);
			dir2 = new Directory (_fs, _id++, "dir2", dir1, _user2);

			new App       (_fs, _id++, "app", _user1.getHomeDirectory(), _user1, "app_Data");
			new PlainFile (_fs, _id++, "pf" , _user1.getHomeDirectory(), _user1, "pf_Data");

			new Link      (_fs, _id++, "linkAbsolute", _user1.getHomeDirectory(), _user1, "/home/user1/dir1");
			new Link      (_fs, _id++, "linkRelative", _user1.getHomeDirectory(), _user1, "dir1");
			new Link      (_fs, _id++, "linkToNotADirectory", _user1.getHomeDirectory(), _user1, "dir1/plainfile1");
			new Link      (_fs, _id++, "linkToInsufficientPermissions", _user1.getHomeDirectory(), _user1, "dir1/dir2");
			new Link      (_fs, _id++, "linkToFileUnknown", _user1.getHomeDirectory(), _user1, "dir1/erro");

			new Link      (_fs, _id++, "linkToLink", dir1, _user1, "/home/user1/linkAbsolute");
			new Link      (_fs, _id++, "linkToLinkBad", dir1, _user1, "/home/user1/linkToFileUnknown");

			new PlainFile (_fs, _id++, "plainfile1", dir1, _user1, "plainfile1_Data");
			new PlainFile (_fs, _id++, "plainfile2", dir2, _user2, "plainfile2_Data");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryAbsolute(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1/dir1");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryRelative(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryLinkAbsoluteToAbsolute(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1/linkAbsolute");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryLinkRelativeToAbsolute(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkAbsolute");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryLinkAbsoluteToRelative(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1/linkRelative");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryLinkRelativeToRelative(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkRelative");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryLinkToLink(){
		try {
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/linkToLink");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(expected = FileUnknownException.class)
	public void fileUnknown() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/test");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void plainFile() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "pf");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void app() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "app");
		changeDirectory.execute();
	}

	@Test(expected = InsufficientPermissionsException.class)
	public void insufficientPermissions() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/dir2");
		changeDirectory.execute();
	}

	@Test(expected = FileUnknownException.class)
	public void linkToLinkError() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/linkToLinkBad");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void linkToNotADirectory() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToNotADirectory");
		changeDirectory.execute();
	}

	@Test(expected = InsufficientPermissionsException.class)
	public void linkToInsufficientPermissions() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToInsufficientPermissions");
		changeDirectory.execute();
	}

	@Test(expected = FileUnknownException.class)
	public void linkToFileUnknown() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToFileUnknown");
		changeDirectory.execute();
	}
}
