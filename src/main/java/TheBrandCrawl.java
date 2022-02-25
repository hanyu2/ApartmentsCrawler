import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class TheBrandCrawl implements AptCrawl{
    @Override
    public String getFileName() {
        return "/tmp/theBrand.json";
    }

    @Override
    public Map<String, Plan> crawl(String url) {
        Map<String, Plan> map = new HashMap<>();

        try {

            Document document = Jsoup.connect(url).get();

            String s = document.toString();
            int startIndex = s.indexOf("dataSet = ") + 9;
            int endIndex = s.indexOf("</script>",startIndex);
            String str = s.substring(startIndex, endIndex).replaceAll("\"", "");
            //System.out.println(str);
            String[] strs = str.split("},");
            strs[0] = strs[0].substring(1);
            for(String ss : strs){
                String[] planDetail = ss.split(",");
                Plan plan = new Plan();
                plan.setPlanName(planDetail[0]);
                plan.setConfiguration(planDetail[1] +" "+ planDetail[2] +" "+ planDetail[3]);
                plan.setPrice(planDetail[7]);
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
