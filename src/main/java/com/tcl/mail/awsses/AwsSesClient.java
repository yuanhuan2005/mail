package com.tcl.mail.awsses;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

/**
 * AWS SES¿Í»§¶Ë
 * 
 * @author YuanHuan
 * 
 */
public class AwsSesClient
{
    private static AmazonSimpleEmailService amazonSimpleEmailService;

    private static AWSCredentials credentials;

    private static class CassandraClientHolder
    {
        private static final AwsSesClient instance = new AwsSesClient();
    }

    private AwsSesClient()
    {
        // set your awsAccessKeyId and awsSecretAccessKey here.
        String awsAccessKeyId = "YOUR_awsAccessKeyId";
        String awsSecretAccessKey = "YOUR_awsSecretAccessKey";

        AwsSesClient.credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        AwsSesClient.amazonSimpleEmailService = new AmazonSimpleEmailServiceClient(
                AwsSesClient.credentials);
    }

    public static final AwsSesClient getInstance()
    {
        return CassandraClientHolder.instance;
    }

    public AmazonSimpleEmailService getAmazonSimpleEmailService()
    {
        return AwsSesClient.amazonSimpleEmailService;
    }

    public void setAmazonSimpleEmailService(AmazonSimpleEmailService amazonSimpleEmailService)
    {
        AwsSesClient.amazonSimpleEmailService = amazonSimpleEmailService;
    }

    public AWSCredentials getCredentials()
    {
        return AwsSesClient.credentials;
    }

    public void setCredentials(AWSCredentials credentials)
    {
        AwsSesClient.credentials = credentials;
    }
}
