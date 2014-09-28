package edu.dnu.androidopac;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Parcel;
import android.os.Parcelable;

/*
 *      <item> 
       <title>Conquerors of time : </title> 
       <isbn>0719555175</isbn> 
       <link>http://opac.koha.workbuffer.org/cgi-bin/koha/opac-detail.pl?biblionumber=2470</link> 
       <description><![CDATA[
 
 
 
 
 
	   <p>By Fishlock, Trevor,. 
	   London : J. Murray, 2004
                        . xiii, 444 p., [16] p. of plates :
                        
                         24 cm.. 
                         0719555175 </p><p> 
 
<a href="http://opac.koha.workbuffer.org/cgi-bin/koha/opac-reserve.pl?biblionumber=2470">Place Hold on <i>Conquerors of time :</i></a></p> 
 
						]]></description> 
       <guid>http://opac.koha.workbuffer.org/cgi-bin/koha/opac-detail.pl?biblionumber=2470</guid> 
     </item> 
 */

public class Record extends Object implements Parcelable {
	//private long articleId;
	//private long feedId;
	private String title;
	private String isbn;
	private String description;
	private URL url;
	private String id;
	
	public Record clone() {
		Record a = new Record();
		a.title = this.title;
		a.isbn = this.isbn;
		a.description = this.description;
		a.url = this.url;
		a.id= this.id;
		return a;
	}
	
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public URL getURL() {
		return url;
	}
	public String getISBN() {
		return isbn;
	}
	public void setTitle(String t) {
		title = t;
	}
	public void setDescription(String d) {
		description= d;
	}
	public void setURL(URL u) {
		url = u;
		id = u.toString().substring(this.url.toString().indexOf("=") + 1);
	}
	public void setISBN(String i) {
		isbn = i;
	}
	public String getID() {
		return this.id;
	}
	public String getGroup() {
		// In the meantime just set the article ID, i.e force no grouping
		return this.title;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(isbn);
		dest.writeString(url.toString());
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(id);
	}
		
	/**
	 * Required for Parcelables
	 */
	public static final Parcelable.Creator<Record> CREATOR
			= new Parcelable.Creator<Record>() {
		public Record createFromParcel(Parcel in) {
			return new Record(in);
		}

		public Record[] newArray(int size) {
			return new Record[size];
		}
	};
	/**
	 * For use by CREATOR
	 * @param in
	 */
	private Record(Parcel in) {
		isbn = in.readString();
		try {
			url = new URL(in.readString());
		} catch (MalformedURLException e) {
			url = null;
			e.printStackTrace();
		}
		title = in.readString();
		description = in.readString();
		id = in.readString();
	}

	public Record() {
		// TODO Auto-generated constructor stub
	}
}

