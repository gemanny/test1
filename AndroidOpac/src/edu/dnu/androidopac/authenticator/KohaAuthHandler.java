package edu.dnu.androidopac.authenticator;

/*
 * 
 * Example Call

ilsdi.pl?service=HoldTitle&patron_id=1&bib_id=1&request_location=127.0.0.1
Example Response

<?xml version="1.0" encoding="ISO-8859-1" ?>
<HoldTitle>
  <title>(les) galères de l'Orfèvre</title>
  <date_available>2009-05-11</date_available>
  <pickup_location>Bibliothèque Jean-Prunier</pickup_location>
</HoldTitle>

Example Call
ilsdi.pl?service=AuthenticatePatron&username=john9&password=soul
Example Response
<?xml version="1.0" encoding="ISO-8859-1" ?>
<AuthenticatePatron>
  <id>419</id>
</AuthenticatePatron>

 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import edu.dnu.androidopac.Constants;
import edu.dnu.androidopac.R;
import edu.dnu.androidopac.log.LogConfig;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class KohaAuthHandler {
	static final String TAG = LogConfig.getLogTag(KohaAuthHandler.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly
	// for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;
	
	static String auri = "";

    private static HttpClient mHttpClient;
	
    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static void maybeCreateHttpClient() {
        if (getHttpClient() == null) {
            setHttpClient(new DefaultHttpClient());
            final HttpParams params = getHttpClient().getParams();
            HttpConnectionParams.setConnectionTimeout(params,
                Constants.REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, Constants.REGISTRATION_TIMEOUT);
            ConnManagerParams.setTimeout(params, Constants.REGISTRATION_TIMEOUT);
        }
    }

    /**
     * Executes the network requests on a separate thread.
     * 
     * @param runnable The runnable instance containing network mOperations to
     *        be executed.
     */
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Connects to the server, authenticates the provided username and
     * password.
     * 
     * @param username The user's username
     * @param password The user's password
     * @param handler The hander instance from the calling UI thread.
     * @param context The context of the calling Activity.
     * @return boolean The boolean result indicating whether the user was
     *         successfully authenticated.
     */
    public static boolean authenticate(String username, String password, Handler handler, final Context context) {
        HttpResponse resp;

        maybeCreateHttpClient();

    	// application preferences
    	SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String aURI = mPrefs.getString(context.getResources().getString(R.string.pref_base_url_key).toString(),
												context.getResources().getString(R.string.base_url).toString());
		aURI = aURI + mPrefs.getString(context.getResources().getString(R.string.pref_login_url_key).toString(),
				context.getResources().getString(R.string.login_url).toString());
		
		auri = aURI;
		
		// Auth post is ... koha_login_context=opac&userid=member&password=member1
		// We're trying to send the koha_login_context as part of the post URL. 

        final HttpPost post = new HttpPost(aURI);
        //post.setHeader("Cookie", cgi_cookie);
        // Add your data  
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
        nameValuePairs.add(new BasicNameValuePair("koha_login_context", "opac"));  
        nameValuePairs.add(new BasicNameValuePair(Constants.PARAM_USERNAME, username));  
        nameValuePairs.add(new BasicNameValuePair(Constants.PARAM_PASSWORD, password));  
        try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Auth post encoding exception: " + e);
	        sendResult(null, handler, context);
	        return false;
		}  
        
        try {
            resp = getHttpClient().execute(post);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	/*
            	 * Cookie: SESSc46d291f9a3827d00a6c8c823f9d5590=822f42053c5f7a8d7d4940267f1be6e2; 
            	 *          CGISESSID=eb4966cbdf869c27d72d9f8afcc3753c
            	 */
            	// TODO - need to also check the body of the response document for a token to denote 
            	// the user is logged in :(
            	
            	String auth_token = getCookie(resp, "Set-Cookie", "CGISESSID");
            	HttpEntity resEntity = resp.getEntity();
            	String content = convertStreamToString(resEntity.getContent());

            	if ( auth_token != null  && content.contains(Constants.LOGGED_IN)) {
            		if ( DEBUG ) Log.d(TAG, "Got auth token: '" + auth_token + "' setting " + Constants.AUTH_SESSION_KEY);
    					
    				mPrefs.edit()
    					.putString(Constants.AUTH_SESSION_KEY, auth_token)
    					.commit()
    				;
    			    sendResult(auth_token, handler, context);
                    return true;
                }
           } else {
        	   if ( DEBUG ) Log.d(TAG, "Error authenticating" + resp.getStatusLine());
            }
        } catch (final IOException e) {
            Log.e(TAG, "IOException when getting authtoken", e);
        } finally {
        	if ( DEBUG ) Log.d(TAG, "getAuthtoken completing");
        }
        sendResult(null, handler, context);
        return false;
    }

    public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
    
    public static String getCookie (HttpResponse resp, String name, String filter) {
        Header[] headers = resp.getAllHeaders();
        if ( DEBUG ) Log.d(TAG, "Looking for header "+ name + " with value containing " + filter);
        for (int i=0; i < headers.length; i++) {
            Header h = headers[i];
            if ( DEBUG ) Log.d(TAG, "Found header [" + h.getName() + ": " + h.getValue() + "]");
            
            if ( h.getName().equalsIgnoreCase(name) && h.getValue().startsWith(filter)) {
            if ( DEBUG ) Log.d(TAG, "Matched header [" + h.getName() + ": " + h.getValue() + "]");
            	return h.getValue();
            }
        }
		return null;
    }
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param authToken The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final String authToken, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
            	((AuthenticatorActivity) context).onAuthenticationResult(authToken);
            }
        });
    }

    /**
     * Attempts to authenticate the user credentials on the server.
     * 
     * @param username The user's username
     * @param password The user's password to be authenticated
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context
     * @return Thread The thread on which the network mOperations are executed.
     */
    public static Thread attemptAuth(final String username,
        final String password, final Handler handler, final Context context) {
        final Runnable runnable = new Runnable() {
            public void run() {
                authenticate(username, password, handler, context);
            }
        };
        // run on background thread.
        return KohaAuthHandler.performOnBackgroundThread(runnable);
    }

	public static void setHttpClient(HttpClient mHttpClient) {
		KohaAuthHandler.mHttpClient = mHttpClient;
	}

	public static HttpClient getHttpClient() {
		return mHttpClient;
	}
}
