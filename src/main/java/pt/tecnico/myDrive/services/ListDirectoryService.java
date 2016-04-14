package pt.tecnico.myDrive.services;

import pt.tecnico.myDrive.domain.FileSystem;

public class ListDirectoryService extends myDriveService{

  private long _token;
  private String _data;
  
	/**
	 *
	 */
	public ListDirectoryService(long token) {
		super();
    _token = token;
	}
	

	@Override
	protected void dispatch() throws Exception {
    FileSystem fs = getFileSystem(); 
    _data = fs.listDirectory(_token);
	}

  public String result() {
    return _data;
  }
}
