package pt.tecnico.myDrive.services;

public class CreateFileService extends myDriveService {
	private long _token;
	private String _name;
	private String _type;
	private String _content;
	/**
	 *
	 */
	public CreateFileService(long token, String name, String type) {
		super();
		_token = token;
		_name = name;
		_type = type;
		_content = null;
	}

	public CreateFileService(long token, String name, String type, String content) {
		super();
		_token = token;
		_name = name;
		_type = type;
		_content = content;
	}

	@Override
	protected void dispatch() throws Exception {
		// TODO Auto-generated method stub
		FileSystem fs = getFileSystem();
		fs.createFile(_name, _type, _content, _token);
	}

}
