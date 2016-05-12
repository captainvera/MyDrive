package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class ExecuteFileService extends myDriveService {

  private long _token;
  private String _filepath;
  private String[] _arguments;
	/**
	 * Execute File Service
	 */

	public ExecuteFileService(long token, String filepath, String[] arguments) {
		super();
    _token = token;
    _filepath = filepath;
    _arguments = arguments;
	}

	@Override
	protected void dispatch() { 
    FileSystem fs = getFileSystem();
    fs.executeFile(_token, _filepath, _arguments);
	}

}
