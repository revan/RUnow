/** Represents a single event */
package edu.rutgers.runow;

import java.io.Serializable;
import java.util.Date;

public class Event implements Comparable<Event>, Serializable{
	private String name;
	private Date date;
	private String tag;
	private String description;
	public Event(String name, Date date, String tag, String description){
		this.setName(name);
		this.setDate(date);
		this.setTag(tag);
		this.setDescription(description);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public int compareTo(Event other) {
		return this.date.compareTo(other.getDate());
	}
}
