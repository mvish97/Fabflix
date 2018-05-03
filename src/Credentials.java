

public class Credentials {
	private final static String loginUser =  "root"; //  "testuser";
	private final static String loginPasswd =  "chennai1"; //"cs122b"; 
	private final static String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
	
	public final static String SITE_KEY = "6LdU10YUAAAAAMsNhl1LRFRamWkDyT4_uZ6GIYad";
	public final static String SECRET_KEY = "6LdU10YUAAAAAKWW-56WLpTw5sE25D4p03NajaKk";
	
	public static String getUser() {
		return Credentials.loginUser;
	}
	
	public static String getURL() {
		return Credentials.loginUrl;
	}
	
	public static String getPassword() {
		return Credentials.loginPasswd;
	}
}
