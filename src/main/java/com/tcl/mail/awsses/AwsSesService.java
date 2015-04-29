package com.tcl.mail.awsses;

import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

public class AwsSesService
{
    final static private Log DEBUGGER = LogFactory.getLog(AwsSesService.class);

    private static final String FROM = "tclwebservice@126.com";

    /**
     * 验证邮箱地址。 如果SES启用的是生产环境的话，则不需要验证收件人，需要验证发件人。如果SES是测试环境的话，两者都需要验证。
     * 
     * @param ses
     *            SES服务对象
     * @param address
     *            邮箱地址
     */
    @SuppressWarnings("unused")
    private static void verifyEmailAddress(AmazonSimpleEmailService ses, String address)
    {
        ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
        if (verifiedEmails.getVerifiedEmailAddresses().contains(address))
        {
            return;
        }

        ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
        AwsSesService.DEBUGGER.info("Please check the email address " + address + " to verify it");
    }

    private static AmazonSimpleEmailService getAmazonSimpleEmailService()
    {
        if (null == AwsSesClient.getInstance()
                || null == AwsSesClient.getInstance().getAmazonSimpleEmailService())
        {
            AWSCredentials credentials = new ClasspathPropertiesFileCredentialsProvider()
                    .getCredentials();
            return new AmazonSimpleEmailServiceClient(credentials);
        }

        return AwsSesClient.getInstance().getAmazonSimpleEmailService();
    }

    private static AWSCredentials getAWSCredentials()
    {
        if (null == AwsSesClient.getInstance()
                || null == AwsSesClient.getInstance().getCredentials())
        {
            AWSCredentials credentials = new ClasspathPropertiesFileCredentialsProvider()
                    .getCredentials();
            return credentials;
        }

        return AwsSesClient.getInstance().getCredentials();
    }

    /**
     * 使用SES发送邮件
     * 
     * @param toEmails
     *            收件人地址数组
     * @param subject
     *            主题
     * @param body
     *            邮件正文
     * @return 发送结果，true表示发送成功，false表示发送失败
     */
    public static boolean sendEmail(String[] toEmailsArr, String subject, String body)
    {
        AwsSesService.DEBUGGER
                .debug("Sending email to " + toEmailsArr + ", subject is: " + subject);
        try
        {
            AWSCredentials credentials = AwsSesService.getAWSCredentials();
            AmazonSimpleEmailService ses = AwsSesService.getAmazonSimpleEmailService();
            Region usWest2 = Region.getRegion(Regions.US_EAST_1);
            ses.setRegion(usWest2);

            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "aws");
            props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
            props.setProperty("mail.aws.password", credentials.getAWSSecretKey());

            Session session = Session.getInstance(props);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(AwsSesService.FROM));
            int toEmailsLen = toEmailsArr.length;
            Address[] toAddressArr = new Address[toEmailsLen];
            for (int i = 0; i < toEmailsLen; i++)
            {
                toAddressArr[i] = new InternetAddress(toEmailsArr[i]);
            }
            msg.addRecipients(Message.RecipientType.TO, toAddressArr);
            msg.setSubject(subject);
            msg.setText(body);
            msg.saveChanges();

            Transport t = new AWSJavaMailTransport(session, null);
            t.connect();
            t.sendMessage(msg, null);

            t.close();
        }
        catch (AddressException e)
        {
            e.printStackTrace();
            AwsSesService.DEBUGGER
                    .error("Caught an AddressException, which means one or more of your "
                            + "addresses are improperly formatted.");
            return false;
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            AwsSesService.DEBUGGER
                    .error("Caught a MessagingException, which means that there was a "
                            + "problem sending your message to Amazon's E-mail Service check the "
                            + "stack trace for more information.");
            return false;
        }

        return true;
    }

    /**
     * 使用SES发送邮件
     * 
     * @param toEmailsList
     *            收件人地址列表
     * @param subject
     *            主题
     * @param body
     *            邮件正文
     * @return 发送结果，true表示发送成功，false表示发送失败
     */
    public static boolean sendEmail(List<String> toEmailsList, String subject, String body)
    {
        if (null == toEmailsList || toEmailsList.isEmpty())
        {
            return false;
        }

        String[] toEmailsArr = new String[toEmailsList.size()];
        toEmailsList.toArray(toEmailsArr);

        return sendEmail(toEmailsArr, subject, body);
    }
}
