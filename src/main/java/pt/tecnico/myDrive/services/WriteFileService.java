package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class WriteFileService extends myDriveService {

	private long _token;
	private String _filepath;
	private String _data;
	/**
	 *
	 */
	public WriteFileService(long token, String filepath, String content) {
		super();
		_token = token;
		_filepath = filepath;
		_data = content;
	}

	@Override
	protected void dispatch() {
		FileSystem fs = getFileSystem();
		fs.writeFile(_token, _filepath, _data);
	}

}
