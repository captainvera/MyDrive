package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class ExecuteFileService extends myDriveService {

  private long _token;
  private String _filepath;

	/**
	 * Execute File Service
	 */

	public ExecuteFileService(long token, String filepath) {
		super();
    _token = token;
    _filepath = filepath;
	}

	@Override
	protected void dispatch() throws Exception {
    FileSystem fs = getFileSystem();
    fs.readFile(_token, _filepath);
	}
}
