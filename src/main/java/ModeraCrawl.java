import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class ModeraCrawl implements AptCrawl {

    @Override
    public String getFileName() {
        return "/tmp/modera.json";
    }

    @Override
    public Map<String, Plan> crawl(String url) {
        Map<String, Plan> map = new HashMap<>();

        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("fp-group-item");

            for(Element ele : elements){
                Plan plan = new Plan();

                Elements planNameEle = ele.getElementsByClass("fp-name");
                plan.setPlanName(planNameEle.text());

                Elements configurationEle = ele.getElementsByClass("fp-col bed-bath");
                plan.setConfiguration(configurationEle.text().substring(12));

                Elements priceEle = ele.getElementsByClass("fp-col rent");
                plan.setPrice(priceEle.text().substring(20));

                map.put(plan.getPlanName(), plan);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

}
