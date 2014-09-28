package edu.dnu.androidopac.search;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import edu.dnu.androidopac.authenticator.KohaAuthHandler;
import edu.dnu.androidopac.log.LogConfig;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BookThumbnailService {
	static final String TAG = LogConfig.getLogTag(BookThumbnailService.class);
	public static InputStream getThumbnail(String isbn){
		if(isbn == null)return null;
		isbn = cleanseISBN(isbn);
		if(isbn == "")return null;
		String aURI = "http://covers.openlibrary.org/b/ISBN/" + isbn + "-S.jpg";
		try{
			URL inputURL = new URL(aURI);
			URLConnection connect = inputURL.openConnection();
			int contentLength = connect.getContentLength();
			Log.d(TAG, "Content length= "+contentLength);
			if (contentLength == -1 || contentLength == 43 )return null;
			return connect.getInputStream();
		}
		catch (IOException e){
			Log.e(TAG, "Error Retrieving Cover Data - Malformed URL", e);
			Log.e(TAG, aURI);
		}
		
		return null;
	}
	public static InputStream getThumb(String bURL,String bibn){
		if(bibn == null)return null;
		String aURI = bURL+"opac-image.pl?thumbnail=1&biblionumber="+bibn;
		//Log.d(TAG, "aURI = "+aURI);
	//http://koha.vn/cgi-bin/koha/opac-image.pl?thumbnail=1&biblionumber=1013
		try{
			URL inputURL = new URL(aURI);
			URLConnection connect = inputURL.openConnection();
			int contentLength = connect.getContentLength();
			Log.d(TAG, "Content URL length= "+contentLength);
		    if (contentLength == -1 || contentLength == 43 )return null;
			return connect.getInputStream();
		}
		catch (IOException e){
			Log.e(TAG, "Error Retrieving Cover Data - Malformed URL", e);
			Log.e(TAG, aURI);
		}
		
		return null;
	}
	
	public static InputStream checkGoogle(String isbn){
		if(isbn == null)return null;
		Log.d(TAG, "2GroupISBN "+isbn);
		isbn = cleanseISBN(isbn);
		Log.d(TAG, "2GroupISBN== "+isbn);
		if(isbn == "")return null;
		String aURI = "http://books.google.com/books?bibkeys=ISBN:" + isbn + "&jscmd=viewapi";
		try{
			URL inputURL = new URL(aURI);
			URLConnection connect = inputURL.openConnection();
			String responce = KohaAuthHandler.convertStreamToString(connect.getInputStream());
			responce = responce.substring(responce.indexOf('{'), responce.length()-2);
			JsonParser parser = new JsonParser();
			JsonObject j = parser.parse(responce).getAsJsonObject();
			if(j.get("ISBN:" + isbn) == null)return null;
			j = j.get("ISBN:" + isbn).getAsJsonObject();
			if(j.get("thumbnail_url") == null) return null;
			inputURL = new URL(j.get("thumbnail_url").getAsString());
			return inputURL.openConnection().getInputStream();
		}
		catch (IOException e){
			Log.e(TAG, "Error Retrieving Cover Data - Malformed URL", e);
			Log.e(TAG, aURI);
		}
		return null;
	}
	
	private static String cleanseISBN(String s){
		String newString = "";
		s = s.toLowerCase();
		char[] allowed = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'x'};
		int start = 0;
		for(start = 0;start < s.length();start ++)
		{
			try
			{
				Integer.parseInt(""+s.charAt(start));
				break;
			}
			catch(Exception e){}
		}
		for(int i = start;i < s.length();i++){
			boolean val = false;
			for(int j = 0;j < allowed.length;j ++){
				if(s.charAt(i) == allowed[j]){
					newString += s.charAt(i);
					val = true;
					break;
				}
			}
			if(i == 12+start || !val){
				return newString;
			}
		}
		return newString;
	}
}
