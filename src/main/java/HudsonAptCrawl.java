import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class HudsonAptCrawl implements AptCrawl{
    String boxClass = "card text-center h-100";
    String planNameClass = "card-title h4 font-weight-bold text-capitalize";
    String configurationClass = "list-unstyled list-inline mb-2 text-sm";
    String priceClass = "font-weight-bold  mb-1 text-md";
    @Override
    public String getFileName() {
        return "/tmp/hudson.json";
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
    public String processPrice(String price) {
        if(price.contains("Call")){
            return "Not Available";
        }
        return price;
    }
}
