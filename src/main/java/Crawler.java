import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Crawler {

    public void getPageLinks(String URL) {

        try {

            //2. Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            //3. Parse the HTML to extract links to other URLs
            Elements elements = document.getElementsByClass("pb-4 mb-2 col-12  col-sm-6 col-lg-4");
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            List<Plan> plans = new ArrayList<Plan>();
            for(Element ele : elements){
                Plan plan = new Plan();

                Elements planNameEle = ele.getElementsByClass("card-title h4 font-weight-bold text-capitalize");
                plan.setPlanName(planNameEle.text());

                Elements configurationEle = ele.getElementsByClass("list-unstyled list-inline mb-2 text-sm");
                plan.setConfiguration(configurationEle.text());

                Elements priceEle = ele.getElementsByClass("font-weight-bold  mb-1 text-md");
                plan.setPrice(priceEle.text());

                Elements available = ele.getElementsByClass("col-12  my-2");
                String availablity = available.text();
                if(availablity.toLowerCase().contains("contact")){
                    plan.setAvailable(false);
                }else{
                    plan.setAvailable(true);
                }
                plans.add(plan);

            }
            writer.close();

            // Assuming you are sending email from through gmails smtp
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
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse("hanyu20703@gmail.com"));
            message.setSubject("Mail Subject");

            String data = plans.stream().map(Plan::toString).collect(Collectors.joining("\n"));;

            System.out.println(data);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(data, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }

    }


    public static void main(String[] args) {
        //1. Pick a URL from the frontier
        new Crawler().getPageLinks("https://www.trioaptspasadena.com/floorplans");
    }
}

class Plan {
    String planName;
    String configuration;
    String price;
    boolean available;


    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        String[] split = configuration.split("\\s+");
        StringBuilder planConfig = new StringBuilder();
        for(int i = 0; i < split.length - 1; i++){
            if (split[i+1].contains("Sq")){
                break;
            } else{
                planConfig.append(split[i]).append(" ");
            }
        }
        this.configuration = planConfig.toString().trim();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        String finalPrice = "";
        if(price.startsWith("Call")){
            finalPrice = "Not Available";
        } else{
            finalPrice = price.substring(price.indexOf("$"), price.indexOf("/"));
        }
        this.price = finalPrice.trim();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "" + planName + '\'' +
                ", " + configuration + '\'' +
                ", " + price + '\'' +
                "}";
    }
}
