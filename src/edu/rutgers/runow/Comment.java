package edu.rutgers.runow;

import java.util.Date;

public class Comment {
	public String user;
	public Date when;
	public String content;
	public Comment(String user, String content){
		this.user = user;
		// implement timestamp
		this.content = content;
	}
}