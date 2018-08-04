package qrcodegen.modules.vcard;

/**
 *
 * @author Stefan Ganzer
 */
public class BeginVersionEndProperty {
	
	private static final String	BEGIN_PROPERTY = "BEGIN:VCARD";
	private static final String VERSION_PROPERTY = "VERSION:";
	private static final String END_PROPERTY = "END:VCARD";
	private final VCardVersion version;
	
	static BeginVersionEndProperty getBeginVersionEndProperty(VCardVersion version){
		return new BeginVersionEndProperty(version);
	}
	
	private BeginVersionEndProperty(VCardVersion version){
		if(version == null){
			throw new NullPointerException();
		}
		this.version = version;
	}
	
	String writeBeginVersionEndProperty(CharSequence content){
		String c = content.toString();
		StringBuilder sb = new StringBuilder(c.length() + 100);
		sb.append(BEGIN_PROPERTY)
				.append(VERSION_PROPERTY).append(version.toString())
				.append(content)
				.append(END_PROPERTY);
		return sb.toString();
	}
	
	
	
}
