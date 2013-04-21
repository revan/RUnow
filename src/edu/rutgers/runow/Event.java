/** Represents a single event */
//TODO multitagging
package edu.rutgers.runow;

import java.io.Serializable;
import java.util.Date;

public class Event implements Comparable<Event>, Serializable{
	public String name;
	public Date when;
	public String tag;
	public String description;
	public String location;
	public Event(String name, String gid, Date when, String location, String description, String tag){
		this.name=name;
		//gid unimplemented
		this.when=when;
		this.tag=tag;
		this.description=description;
		this.location=location;
	}
	@Override
	public int compareTo(Event other) {
		return this.when.compareTo(other.when);
	}
}
