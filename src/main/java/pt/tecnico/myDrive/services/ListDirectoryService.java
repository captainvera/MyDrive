package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class ListDirectoryService extends myDriveService{

  private long _token;
  private String _path;
  private String _data;

	/**
	 * List Current Directory
	 */
  public ListDirectoryService(long token) {
    this(token, ".");
  }

	public ListDirectoryService(long token, String path) {
    super();
    _token = token;
    _path = path;
	}

	@Override
	protected void dispatch() {
    FileSystem fs = getFileSystem();
    _data = fs.listDirectory(_token, _path);
	}

  public String result() {
    return _data;
  }
}
