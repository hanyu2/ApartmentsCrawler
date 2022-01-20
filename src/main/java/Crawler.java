import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

public class Crawler implements RequestHandler<Map<String,String>, String> {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);


    public String getPageLinks(Map<String,String> apartment2URL) throws Exception {
        StringBuilder data = new StringBuilder();
        AptCrawl crawler;
        for(String apartmentName : apartment2URL.keySet()){

            crawler = new TrioAptCrawl();
            String url = apartment2URL.get(apartmentName);

            Map<String, Plan> currentPlanMap = crawler.crawl(url);
            String trioInfo = crawler.findDiff(currentPlanMap);
            if (trioInfo.length() != 0) {
                data.append("Trio:\n");
                data.append(trioInfo).append("\n");
                crawler.deleteFile();
                crawler.write(crawler.serialize(currentPlanMap));
            }
        }
        if(data.length() != 0){
            sendEmail(data.toString());
        }
        return data.toString();
    }

    private void sendEmail(String content) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("wo111180611@gmail.com", "lzbpzcbscrfvigsa");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("wo111180611@gmail.com"));
        message.addRecipients(
                Message.RecipientType.TO, InternetAddress.parse("hanyu20703@gmail.com"));
        //message.addRecipients(
         //       Message.RecipientType.TO, InternetAddress.parse("lx.hikari@gmail.com"));
        message.setSubject("Mail Subject");

        message.setContent(multipart);

        message.setSubject("Apartment Updates!");


        System.out.println(content.toString());

        Transport.send(message);
    }



    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("Trio","https://www.trioaptspasadena.com/floorplans");
        new Crawler().handleRequest(map, null);
    }

    @Override
    public String handleRequest(Map<String, String> stringStringMap, Context context) {
        try {
            return new Crawler().getPageLinks(stringStringMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "test";
    }
}
