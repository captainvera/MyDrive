package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.InvalidTokenException;
import pt.tecnico.myDrive.exceptions.NotALinkException;



public class ChangeDirectoryService extends myDriveService {

	private long _token;
	private String _dirpath;
	private String _data;
	/**
	 * Change Directory Service Constructor
	 */
	public ChangeDirectoryService(long token, String dirpath) {
		super();
		_token = token;
		_dirpath = dirpath;
	}

	@Override
	protected void dispatch() throws FileUnknownException, NotADirectoryException, InsufficientPermissionsException, InvalidTokenException, NotALinkException {
		FileSystem fs = FileSystem.getInstance();
		_data = fs.changeDirectory(_token, _dirpath);
	}

	public String result() {
    return _data;
  }

}
