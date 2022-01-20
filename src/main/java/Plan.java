import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public abstract class Plan {
    String planName;
    String configuration;
    String price;


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

    public abstract Plan createPlan();

    public Map<String, Plan> crawl(String url, String boxClass, String planNameClass, String configurationClass, String priceClass){
        Map<String, Plan> map = new HashMap<>();

        try {

            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass(boxClass);

            for(Element ele : elements){
                Plan plan = createPlan();

                Elements planNameEle = ele.getElementsByClass(planNameClass);
                plan.setPlanName(plan.processPlanName(planNameEle.text()));

                Elements configurationEle = ele.getElementsByClass(configurationClass);
                        plan.setConfiguration(plan.processConfiguration(configurationEle.text()));

                Elements priceEle = ele.getElementsByClass(priceClass);
                plan.setPrice(plan.processPrice(priceEle.text()));

                map.put(plan.getPlanName(), plan);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    public abstract String processPlanName(String planName);
    public abstract String processConfiguration(String configuration);
    public abstract String processPrice(String price);
    public abstract String serialize(Map<String, Plan> map);
}
