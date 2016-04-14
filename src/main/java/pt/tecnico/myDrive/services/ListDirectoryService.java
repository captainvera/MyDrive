package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class ListDirectoryService extends myDriveService{

  private long _token;
  private String _filepath;
  private String _data;
	/**
	 *
	 */
	public ListDirectoryService(long token, String filepath) {
		super();
    _token = token;
    _filepath = filepath;
	}

	@Override
	protected void dispatch() throws Exception {
    FileSystem fs = getFileSystem();  
    _data = fs.listFile(_token, _filepath);
	}

  public String result() {
    return _data;
  }
}
