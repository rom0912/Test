package com.romy.notification.common.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuth extends Authenticator {

	PasswordAuthentication pa;
    
    public MailAuth(String id, String pw) {
        
        pa = new PasswordAuthentication(id, pw);
    }
    
    public PasswordAuthentication getPasswordAuthentication() {
        return pa;
    }
	
}