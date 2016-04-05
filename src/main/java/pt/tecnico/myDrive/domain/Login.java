package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;
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
	public Login(User user, Directory currentDirectory, DateTime validity) {
		super();
		setCurrentDirectory(currentDirectory);
		setUser(user);
		setExpirationDate(validity);
		setToken(new BigInteger(64, new Random()).longValue());
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
   setUser(null);
   setCurrentDirectory(null);
   }
}
