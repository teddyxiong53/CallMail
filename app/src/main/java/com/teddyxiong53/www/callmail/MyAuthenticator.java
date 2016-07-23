package com.teddyxiong53.www.callmail;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class MyAuthenticator extends Authenticator {
    private String userName = null;
    private String password = null;

    public MyAuthenticator() {

    }
    public MyAuthenticator(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        System.out.println(userName + "xxx  " + password);
        return new PasswordAuthentication(userName, password);
    }
}
