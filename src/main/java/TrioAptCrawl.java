import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class TrioAptCrawl implements AptCrawl{
    String boxClass = "pb-4 mb-2 col-12  col-sm-6 col-lg-4";
    String planNameClass = "card-title h4 font-weight-bold text-capitalize";
    String configurationClass = "list-unstyled list-inline mb-2 text-sm";
    String priceClass = "font-weight-bold  mb-1 text-md";

    @Override
    public String getFileName() {
        return "/tmp/trio.json";
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
        String[] split = configuration.split("\\s+");
        StringBuilder planConfig = new StringBuilder();
        for(int i = 0; i < split.length - 1; i++){
            if (split[i+1].contains("Sq")){
                break;
            } else{
                planConfig.append(split[i]).append(" ");
            }
        }
        return planConfig.toString().trim();
    }

    @Override
    public String processPrice(String price) {
        String finalPrice = "";
        if(price.startsWith("Call")){
            finalPrice = "Not Available";
        } else{
            finalPrice = price.substring(price.indexOf("$"), price.indexOf("/"));
        }
        return finalPrice.trim();
    }

    @Override
    public Map<String, Plan> toPlan(String s) {
        Map<String,Plan> res = new HashMap<>();
        if(s.isEmpty()){
            return res;
        }
        String[] planStrs = s.split("\\r?\\n|\\r");
        for(String str : planStrs){
            Plan plan = new Plan();
            String[] parts = str.split(",");
            plan.setPlanName(parts[0].substring(5));
            plan.setConfiguration(parts[1]);
            String price = null;
            if(parts.length == 3){
                price = parts[2].substring(0, parts[2].length()-1);
            } else{
                price = parts[2]+","+parts[3].substring(0, parts[3].length()-1);
            }

            plan.setPrice(price);
            res.put(plan.getPlanName(), plan);
        }
        return res;
    }
}
