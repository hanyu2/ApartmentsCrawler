import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://www.camdenliving.com/apartments/glendale-ca/camden-glendale/available-apartments").get();
        Elements elements = document.getElementsByClass("jsx-3930959212 floorplan-card mb-4 md:mb-6 floor-plan-card-border-gradient rounded-md min p-px relative");

        for(Element ele : elements){
            Plan plan = new Plan();

            Elements planNameEle = ele.getElementsByClass("jsx-3930959212 my-4 font-sans font-extrabold text-20 lg:text-20");
            plan.setPlanName(planNameEle.text());

            Elements configurationEle = ele.getElementsByClass("jsx-3930959212 flex justify-between w-full my-4");
            plan.setConfiguration(configurationEle.text());

            Elements priceEle = ele.getElementsByClass("jsx-3930959212 flex flex-col items-end");
            plan.setPrice(priceEle.text());

            System.out.println(plan.toString());
        }
    }

}

