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
        String cleanedPrice = price.replace(",", "");
        if(cleanedPrice.startsWith("Call")){
            finalPrice = "Not Available";
        } else{
            finalPrice = cleanedPrice.substring(price.indexOf("$"), price.indexOf("/"));
        }

        return finalPrice.trim();
    }

}
