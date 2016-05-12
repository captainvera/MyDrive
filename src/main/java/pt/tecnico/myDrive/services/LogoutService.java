
package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class LogoutService extends myDriveService {

	private long _token;

	/**
	 * Logout Service Constructor
	 */
	public LogoutService(long token) {
		super();
		_token = token;
	}

	@Override
	protected void dispatch() {
		FileSystem fs = FileSystem.getInstance();
		fs.logout(_token);
	}


}
