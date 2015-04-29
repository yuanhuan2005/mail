package com.tcl.mail.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import com.tcl.mail.awsses.AwsSesService;
import com.tcl.mail.commonmail.EmailMsg;
import com.tcl.mail.commonmail.MailSender;

/**
 * 
 * 
 * @author yuanhuan 2015年4月7日 下午5:23:22
 */
public class Test
{
    public static void main(String[] args)
    {
        boolean result = false;

        // test AWS SES with addresses array
        String[] addresses = { "yuanhuan@tcl.com" };
        result = AwsSesService.sendEmail(addresses, "Test from ses array", "Hello world.");
        System.out.println("Test from ses array: " + result);

        // test AWS SES with addresses list
        List<String> toEmailsList = new ArrayList<String>();
        toEmailsList.add("yuanhuan@tcl.com");
        result = AwsSesService.sendEmail(toEmailsList, "Test from ses list", "Hello world.");
        System.out.println("Test from ses list: " + result);

        // test common mail service
        EmailMsg emailMsg = new EmailMsg();
        emailMsg.setBody("Hello world.");
        emailMsg.setFromEmailAddress("tclwebservice@126.com");
        emailMsg.setSmtpServer("smtp.126.com");
        emailMsg.setSmtpPort(25);
        emailMsg.setUsername("tclwebservice@126.com");
        emailMsg.setPassword("************"); // set password of this user
        emailMsg.setToEmailAddresses(addresses);
        emailMsg.setSubject("Test from common mail service");
        try
        {
            result = MailSender.getInstance().sendMail(emailMsg);
            System.out.println("Test from common mail service: " + result);
        }
        catch (UnsupportedEncodingException | MessagingException e)
        {
            e.printStackTrace();
        }
    }
}
