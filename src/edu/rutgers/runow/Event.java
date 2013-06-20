/** Represents a single event */
//TODO multitagging
package edu.rutgers.runow;

import java.io.Serializable;
import java.util.Date;

public class Event implements Comparable<Event>, Serializable{
	public Integer id;
	public String name;
	public Date when;
	public String tag;
	public String description;
	public String location;
	public String url;
	public String photo_url;
	public Event(Integer id, String name, String gid, Date when, String location, String description, String url, String photo_url, String tag){
		this.id=id;
		this.name=name;
		//gid unimplemented
		this.when=when;
		this.tag=tag;
		this.description=description;
		this.url=url;
		this.photo_url=photo_url;
		this.location=location;
	}
	@Override
	public int compareTo(Event other) {
		return this.when.compareTo(other.when);
	}
}
