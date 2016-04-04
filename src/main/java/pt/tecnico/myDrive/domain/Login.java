package pt.tecnico.myDrive.domain;

import java.math.BigInteger;
import java.util.Random;

public class Login extends Login_Base {

	public Login() {
		super();
	}

	public long createSession(String username, String password){
		removeInvalidSessions();
		long token = new BigInteger(64,new Random()).longValue();
		//check if token is unique
		return token;
	}

	public void removeInvalidSessions(){


	}

	public boolean checkToken(long token){

		return true;
	}
}
