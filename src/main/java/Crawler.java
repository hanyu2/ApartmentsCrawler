import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Crawler implements RequestHandler<Map<String,String>, Void> {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);


    public void getPageLinks(Map<String,String> apartment2URL) {
        String data = "test";
        setupEmail();
        for(String apartmentName : apartment2URL.keySet()){
            if (apartmentName.equals("Trio")){
                crawlTrio(apartment2URL.get(apartmentName));
            }
        }
    }

    private void crawlTrio(String url){
        try {
            //2. Fetch the HTML code
            Plan trioPlan = new TrioPlan();
            String boxClass = "pb-4 mb-2 col-12  col-sm-6 col-lg-4";
            String planNameClass = "card-title h4 font-weight-bold text-capitalize";
            String configurationClass = "list-unstyled list-inline mb-2 text-sm";
            String priceClass = "font-weight-bold  mb-1 text-md";
            Map<String,Plan> currentPlanMap = trioPlan.crawl(url, boxClass, planNameClass, configurationClass, priceClass);


            String ss = read();

            boolean changed = false;
            StringBuilder changedContent = new StringBuilder();
            Map<String,Plan> planFromFile = Plan.toPlan(ss);
            for(String currentName : plans.keySet()){
                if(!planFromFile.containsKey(currentName)){
                    changed = true;
                    changedContent.append(plans.get(currentName).toString()).append("\n");
                }else{
                    Plan old = planFromFile.get(currentName);
                    Plan newPlan = plans.get(currentName);
                    if(!old.getPrice().equals(newPlan.getPrice())){
                        changed = true;
                        changedContent.append(newPlan.toString());
                    }
                }
            }
            if(changed == true) {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(changedContent.toString(), "text/html; charset=utf-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);
//                if(changedContent.toString().contains("Available")){
//                    message.setSubject("Apartment is gone!");
//
//                } else {
                message.setSubject("New Apartment Available!");

                //}
                System.out.println(changedContent.toString());

                Transport.send(message);

                File myObj = new File("/tmp/result.json");
                myObj.delete();
                write(data);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEmail(){
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
    }



    private void write(String data) throws Exception{
        Writer writer = null;
        writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("/tmp/result.json"), "utf-8"));
        writer.write(data);
        writer.close();
    }

    private String read() throws Exception{
        File myObj = new File("/tmp/result.json");
        if(!myObj.exists()){
            return "";
        }
        Scanner myReader = new Scanner(myObj);
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            sb.append(myReader.nextLine());
            sb.append("\n");
        }
        myReader.close();
        return sb.toString();
    }


    public String handleRequest(Map<String, String> s, Context context) {
        return new Crawler().getPageLinks(s);
    }

    public static void main(String[] args) {
        new Crawler().handleRequest("https://www.trioaptspasadena.com/floorplans", null);
    }
}
