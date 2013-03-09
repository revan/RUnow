/** Represents a single event */
package edu.rutgers.runow;

import java.io.Serializable;
import java.util.Date;

public class Event implements Comparable<Event>, Serializable{
	private String name;
	private Date date;
	private String[] tags;
	private String description;
	public Event(String name, Date date, String[] tags, String description){
		this.setName(name);
		this.setDate(date);
		this.setTags(tags);
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
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
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
