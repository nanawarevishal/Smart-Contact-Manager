package com.smart.smartcontactmanager.Service;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
     public boolean sendEmail(String subject,String message1,String to){
        
        boolean flag = false;
        // rest of the code

        String from = "nanawarevishal17@gmail.com";

        // Variable for gmail host

        String host = "smtp.gmail.com";

        //get System propertise
        Properties properties = System.getProperties();


        // host set

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        String username = "nanawarevishal17";
        String password = "ofdgqcberzmfswmd";

        // get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                
                return new PasswordAuthentication(username, password);
            }
        });

         try {

            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            // message.setText(message1);
            message.setContent(message1, "text/html");
            Transport.send(message);
            // System.out.println("Sent Successfully...!");
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some");
        }

        return flag;
    }
}
