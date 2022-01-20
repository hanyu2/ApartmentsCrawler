import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class AmliAptCrawl implements AptCrawl{
    String boxClass = "af-class-floorplans-card styles_wrapper__imgcN";
    String planNameClass = "af-class-text-block-61 styles_name__R8R9I";
    String configurationClass = "af-class-div-block-194";
    String priceClass = "af-class-text-block-63 styles_price__1TJcU";
    @Override
    public String getFileName() {
        return "/tmp/amli.json";
    }

    @Override
    public Map<String, Plan> crawl(String url) {
        Map<String, Plan> map = new HashMap<>();

        try {

            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass(boxClass);

            for(Element ele : elements){
                Plan plan = new Plan();

                Elements planNameEle = ele.getElementsByClass(planNameClass);
                plan.setPlanName(processPlanName(planNameEle.text()));

                Elements configurationEle = ele.getElementsByClass(configurationClass);
                plan.setConfiguration(processConfiguration(configurationEle.text()));

                Elements priceEle = ele.getElementsByClass(priceClass);
                plan.setPrice(processPrice(priceEle.text()));

                map.put(plan.getPlanName(), plan);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public String processPlanName(String planName) {
        return planName;
    }

    @Override
    public String processConfiguration(String configuration) {
        return configuration;
    }

    @Override
    public String processPrice(String price) {
        if(price.length() == 0){
            return "Not Available";
        }else{
            String finalPirce = price.substring(0,2)+price.substring(3,6);
            return finalPirce;
        }
    }

}
