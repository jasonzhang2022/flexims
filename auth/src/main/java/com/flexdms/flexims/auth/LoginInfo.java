package com.flexdms.flexims.auth;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * A data transfer class to transfer username/password between client and server
 * @author jason.zhang
 *
 */
@XmlRootElement
public class LoginInfo {

	String username;
	String password;
	String email;
	boolean rememberMe;
	
	String oldPassword;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isRememberMe() {
		return rememberMe;
	}
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
