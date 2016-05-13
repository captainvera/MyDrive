package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class DeleteFileService extends myDriveService{

	private String _filename;
	private long _token;
	/**
	 * DeleteFileService Constructor.
	 */
	public DeleteFileService(long token, String filename) {
		super();
		_filename = filename;
		_token = token;
	}

	@Override
	protected void dispatch()  {
		FileSystem fs = FileSystem.getInstance();
		fs.deleteFile(_token, _filename);

	}

}
