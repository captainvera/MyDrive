package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class Link extends Link_Base {

	public Link(String name, Directory parent, Integer id, User owner) {
		init(name, parent, id, owner);
	}
	/**
	 * TEMPORARY
	 */
	@Override
		public int getSize(){
			return 0;
		}

	@Override
		public void execute(){
		}

	@Override
		public <T> T accept(GenericVisitor<T> v){
			return v.visit(this);
		}

	@Override
		public void remove() {
			super.remove();
		}
	/**
	 * @param xml Element
	 */
	public void xmlImport(Element linkElement) throws ImportDocumentException{
		try{
			setId(linkElement.getAttribute("id").getIntValue());

			Element perm = linkElement.getChild("perm");
			if (perm != null)
				setUserPermission(new String(perm.getText().getBytes("UTF-8")));

			Element value = linkElement.getChild("value");
			if (value != null)
				setData(new String(value.getText().getBytes("UTF-8")));

		} catch(UnsupportedEncodingException | DataConversionException e){
			throw new ImportDocumentException(String.valueOf(getId()));
		}
	}

}
