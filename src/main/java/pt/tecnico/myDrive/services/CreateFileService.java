package pt.tecnico.myDrive.services;

public class CreateFileService extends myDriveService {
	private long _token;
	private String _name;
	private String _type;
	/**
	 *
	 */
	public CreateFileService(long token, String name, String type) {
		super();
		_token = token;
		_name = name;
		_type = type;
	}

	@Override
	protected void dispatch() throws Exception {
		// TODO Auto-generated method stub
		FileSystem fs = getFileSystem();
		if(fs.verifyToken(_token)){
			fs.createFile(_name, _type);
		}
	}

}
