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
import org.joda.time.DateTime;

public class Login extends Login_Base {
	/**
	 * Login constructor, receives logged user, currentDirectory and expiration date.
	 */
	public Login(User user, Directory currentDirectory, Long token) {
		super();
		setCurrentDirectory(currentDirectory);
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
	public boolean verifyToken(long token) {
		return token == super.getToken();
	}
	public boolean notExpired() {
		return new DateTime().compareTo(super.getExpirationDate()) < 0;
	}
	@Override
	public Long getToken(){
		return null;
	}
	@Override
	public DateTime getExpirationDate(){
		return null;
	}
	@Override
	public void setExpirationDate(DateTime expirationDate) {
	}
	@Override
	public void setToken(Long token) {
	}
	@Override
	public FileSystem getFileSystem() {
		return null;
	}
	@Override
	public void setFileSystem(FileSystem fileSystem) {
	}
	@Override
	public void setUser(User user) {
	}
	

}
