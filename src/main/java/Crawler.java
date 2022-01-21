import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.*;

public class Crawler implements RequestHandler<Map<String,String>, String> {

    public String getPageLinks(Map<String,String> apartment2URL) throws Exception {
        StringBuilder data = new StringBuilder();
        AptCrawl crawler;
        for(String apartmentName : apartment2URL.keySet()){
            if(apartmentName.equals("Trio")){
                crawler = new TrioAptCrawl();
            } else if (apartmentName.equals("Amli")){
                crawler = new AmliAptCrawl();
            } else if (apartmentName.equals("Avila")){
                crawler = new AvilaAptCrawl();
            } else if (apartmentName.equals("CityPlace")){
                crawler = new CityPlaceAptCrawl();
            } else{
                crawler = new HudsonAptCrawl();
            }
            String url = apartment2URL.get(apartmentName);

            Map<String, Plan> currentPlanMap = crawler.crawl(url);
            String info = crawler.findDiff(currentPlanMap);
            if (info.length() != 0) {
                data.append(apartmentName).append(":").append("\n");
                data.append(info).append("\n");
                crawler.deleteFile();
                crawler.write(crawler.serialize(currentPlanMap));
                data.append("\n");
            }
        }

        if (data.length() != 0){
            sendEmail(data.toString());
        }
        return "Scan finished!";
    }

    private void sendEmail(String content) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/plain");

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
        message.addRecipients(
                Message.RecipientType.TO, InternetAddress.parse("lx.hikari@gmail.com"));
        message.setSubject("Mail Subject");

        message.setContent(multipart);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = df.format(new Date());
        message.setSubject("Apartment Updates! " + formatted);
        System.out.println("Updates! ============");
        System.out.println(content);
        Transport.send(message);
    }



    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("Trio","https://www.trioaptspasadena.com/floorplans");
        map.put("Amli","https://www.amli.com/amli-old-pasadena/floorplans");
        map.put("Avila", "https://www.liveavila.com/pasadena/avila/conventional/");
        //map.put("CityPlace","https://www.liveatcityplace.com/floorplans");
        map.put("Hudson","https://www.livethehudson.com/floorplans");
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
