package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Calendar;
import java.util.Date;
import pt.tecnico.myDrive.domain.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class Login extends Login_Base {
  protected static final Logger log = LogManager.getRootLogger();

	/**
	 * Login constructor, receives logged user, currentDirectory and expiration date.
	 */
	public Login(FileSystem fs, User user, Directory currentDirectory, Long token) {
		super();
		setCurrentDirectory(currentDirectory);
    super.setFileSystem(fs);
		super.setUser(user);
		super.setToken(token);
		extendToken();
	}
	/**
	 * Basic remove implementation for Login objects
	 */
	public void remove() {
		nullifyRelations();
		deleteDomainObject();
	}
	/**
	 * Nullifies relations, that is, deletes/cancels any relation between this
	 * object and eventual others.
	 */
	protected void nullifyRelations() {
		super.setUser(null);
		super.setCurrentDirectory(null);
		super.setFileSystem(null);
	}

	public void extendToken() {
		super.setExpirationDate(new DateTime().plusHours(2));
	}
	public boolean compareToken(long token) {
		return token == super.getToken();
	}
	public boolean hasExpired () {
		return new DateTime().compareTo(super.getExpirationDate()) >= 0;
	}
	@Override
	public Long getToken(){
		log.warn("DO NOT USE Login.getToken() it only returns NULL");
		return null;
	}
	@Override
	public DateTime getExpirationDate(){
		log.warn("DO NOT USE Login.getExpirationDate() it only returns NULL");
		return null;
	}
	@Override
	public void setExpirationDate(DateTime expirationDate) {
		log.warn("DO NOT USE Login.setExpirationDate() it does nothing.");
	}
	@Override
	public void setToken(Long token) {
		log.warn("DO NOT USE Login.setToken() it does nothing.");
	}
	@Override
	public FileSystem getFileSystem() {
		log.warn("DO NOT USE Login.getFileSystem() it only returns NULL");
		return null;
	}
	@Override
	public void setFileSystem(FileSystem fileSystem) {
		log.warn("DO NOT USE Login.setFileSystem() it does nothing.");
	}
	@Override
	public void setUser(User user) {
		log.warn("DO NOT USE Login.setUser() it does nothing.");
	}


}
