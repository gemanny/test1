package edu.dnu.androidopac;

public class Constants {
    
	public static final int PREFERENCES = 1;
	public static final int SEARCH = 2;
	public static final int INFO = 3;
	public static final int LOGIN = 4;
	public static final int RESET = 5;
	public static final int SCAN = 6;
	public static final int MY_BOOKS = 7;
	public static final int REFRESH = 8;
	public static final int LOGOUT = 9;
	
	public static final String SEARCH_PUB_DATE_RANGE_PARAM = "limit-yr";
	public static final String LIMIT_AVAILABLE = "limit=available";
	
	public static final String ACCOUNT_TYPE = "nz.net.catalyst.KiritakiKoha.account";
	public static final String AUTHTOKEN_TYPE = "nz.net.catalyst.KiritakiKoha.account";
    public static final String PARAM_USERNAME = "userid";
    public static final String PARAM_PASSWORD = "password";
    public static final String AUTH_SESSION_KEY = "auth_session";
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
	public static final String PARAM_AUTHTOKEN_TYPE = "authToken";
	public static final String LOGGED_IN = "logout.x=1";
	
	public static final String CONFIG_SCAN_INTENT = "com.google.zxing.client.android.SCAN";
	public static final String CONFIG_SCAN_MODE = "QR_CODE_MODE";
	public static final String SEARCH_SCAN_MODE = "PRODUCT_MODE";
	public static final String ISBN = "ISBN";
	
	public static final int RESP_SUCCESS = 1;
	public static final int RESP_NO_ITEMS = 2;
	public static final int RESP_INVALID_SESSION = 3;
	public static final int RESP_FAILED = 4;

}
