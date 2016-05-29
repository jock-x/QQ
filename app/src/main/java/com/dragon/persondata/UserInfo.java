package com.dragon.persondata;

public class UserInfo {
	
	public int userPhoto;
	public String userQQ=null;
	public String userPassword=null;
	public int deleteButtonRes;
	public UserInfo(int userPhoto, String userQQ, String userPassword, int deleteButtonRes) {
		super();
		this.userPhoto = userPhoto;
		this.userQQ = userQQ;
		this.deleteButtonRes = deleteButtonRes;
		this.userPassword=userPassword;
	}
	
}
