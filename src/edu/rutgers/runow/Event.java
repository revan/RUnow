/** Represents a single event */
package edu.rutgers.runow;

import java.util.Date;

public class Event implements Comparable<Event>{
	private String name;
	private Date date;
	private String[] tags;
	public Event(String name, Date date, String[] tags){
		this.setName(name);
		this.setDate(date);
		this.setTags(tags);
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
	@Override
	public int compareTo(Event other) {
		return this.date.compareTo(other.getDate());
	}
}
