package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.exceptions.WrongPasswordException;

public class LoginService extends myDriveService {

	private String _username;
	private String _password;
	private long _result;

	/**
	 * Login Service Constructor
	 */
	public LoginService(String username, String password) {
		super();
		_username = username;
		_password = password;
	}

	@Override
	protected void dispatch() {
		FileSystem fs = FileSystem.getInstance();
		_result = fs.login(_username, _password);
	}

	public long result(){
		return _result;
	}

}
