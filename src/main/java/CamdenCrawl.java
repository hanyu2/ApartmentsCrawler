import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class CamdenCrawl implements AptCrawl{
    @Override
    public String getFileName() {
        return "/tmp/camden.json";
    }

    @Override
    public Map<String, Plan> crawl(String url) {
        Map<String, Plan> map = new HashMap<>();

        try {

            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("jsx-3930959212 floorplan-card mb-4 md:mb-6 floor-plan-card-border-gradient rounded-md min p-px relative");

            for(Element ele : elements){
                Plan plan = new Plan();

                Elements planNameEle = ele.getElementsByClass("jsx-3930959212 my-4 font-sans font-extrabold text-20 lg:text-20");
                plan.setPlanName(planNameEle.text());

                Elements configurationEle = ele.getElementsByClass("jsx-3930959212 flex justify-between w-full my-4");
                plan.setConfiguration(configurationEle.text());

                Elements priceEle = ele.getElementsByClass("jsx-3930959212 flex flex-col items-end");
                plan.setPrice(priceEle.text().substring(15));

                map.put(plan.getPlanName(), plan);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
