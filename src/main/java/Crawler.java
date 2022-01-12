import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class Crawler {

    public void getPageLinks(String URL) {

        try {

            //2. Fetch the HTML code
            Document document = Jsoup.connect(URL).get();
            //3. Parse the HTML to extract links to other URLs
            Elements elements = document.getElementsByClass("pb-4 mb-2 col-12  col-sm-6 col-lg-4");
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

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
                writer.write(plan.toString());

            }
            writer.close();

        } catch (IOException e) {
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
        this.configuration = configuration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
                "planName='" + planName + '\'' +
                ", configuration='" + configuration + '\'' +
                ", price='" + price + '\'' +
                ", available=" + available +
                '}';
    }
}
