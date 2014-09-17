package com.flexdms.flexims.util;

public interface EmailSenderI {

	void sendMessage(String to, String subject, String content);
}
