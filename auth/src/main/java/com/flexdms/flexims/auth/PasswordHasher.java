package com.flexdms.flexims.auth;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

import com.flexdms.flexims.users.FxUser;

public final class PasswordHasher {

	public static final int ITERATION = 10;
	
	private PasswordHasher() {
		
	}
	public static void hashPasswordStatic(FxUser user) {
		String hashedString = hashPassword(user.getPassword(), user.getId());
		user.setPassword(hashedString);
	
	}
	
	public static String hashPassword(String password, long userid) {
		Md5Hash hash = new Md5Hash(password, ByteSource.Util.bytes(String.valueOf(userid)), ITERATION);
		String hashedString = hash.toBase64();
		return hashedString;
	}
	
}
