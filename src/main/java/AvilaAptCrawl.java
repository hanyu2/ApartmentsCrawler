import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class AvilaAptCrawl implements AptCrawl{
    String boxClass = "fp-grid-item";
    String planNameClass = "B1";
    String configurationClass = "details-col bed-bath";
    String priceClass = "details-col rent";

    @Override
    public String getFileName() {
        return "/tmp/avila.json";
    }

    @Override
    public Map<String, Plan> crawl(String url) {
        Map<String, Plan> map = new HashMap<>();

        try {

            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass(boxClass);

            for(Element ele : elements){
                Plan plan = new Plan();
                Elements ee = ele.getElementsByTag("meta");
                for(Element e : ee){
                    String content = e.attr("content");
                    String name = e.attr("itemprop");

                    if("name".equals(name)) {
                        plan.setPlanName(content);
                    }
                }

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
        if(configuration.contains("Studio")){
            return "Studio";
        } else{
            return configuration.substring(10);
        }

    }

    @Override
    public String processPrice(String price) {
        if(price.contains("—")){
            return "Not Available";
        }
        return price;
    }
}
