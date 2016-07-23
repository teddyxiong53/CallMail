package com.teddyxiong53.www.callmail;

import android.content.pm.PackageInstaller;
import android.util.Log;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class SimpleMailSender {

    public boolean sendTextEmail(MailSenderInfo info) {
        MyAuthenticator auth = null;
        Properties prop = info.getProperties();

        if(info.isValidate()) {
            auth = new MyAuthenticator(info.getUserName(), info.getPassword());

        }
        Session sendMailSession = Session.getInstance(prop, auth);
        try {
            Message mailMessage = new MimeMessage(sendMailSession);
            Address from = new InternetAddress(info.getSenderEmail());
            Log.d("xhl", "sendTextEmail: " + from);
            mailMessage.setFrom(from);

            Address to = new InternetAddress(info.getReceiverEmail());

            mailMessage.setRecipient(Message.RecipientType.TO, to);

            mailMessage.setSubject(info.getSubject());
            mailMessage.setSentDate(new Date());
            String mailContent = info.getContent();

            mailMessage.setText(mailContent);

            //Transport.send(mailMessage);
            Transport trans = sendMailSession.getTransport("smtp");
            Log.d("XHL", info.getMailServerHost()+ "xxxx " + "xxxx " +info.getUserName()+ "xxxx" + info.getPassword());
            trans.connect(info.getMailServerHost(), info.getUserName(), info.getPassword());
            mailMessage.saveChanges();
            Transport.send(mailMessage);

            Log.d("xhl", "send ok");
            return true;

        } catch (MessagingException e) {
            Log.d("xhl", "send err");
            e.printStackTrace();
        }
        return false;
    }
}
