import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public interface AptCrawl {

    String getFileName();

    Map<String, Plan> crawl(String url);

    String processPlanName(String planName);

    String processConfiguration(String configuration);

    String processPrice(String price);

    default String serialize(Map<String, Plan> map) {
        return map.values().stream().map(Plan::toString).collect(Collectors.joining("\n"));
    }

    default String findDiff(Map<String, Plan> map) throws Exception{
        Map<String, Plan> oldPlanMap = toPlan(read());
        StringBuilder changedContent = new StringBuilder();
        for(String currentPlanName : map.keySet()){
            if(!oldPlanMap.containsKey(currentPlanName)){
                changedContent.append(map.get(currentPlanName).toString()).append("\n");
            } else{
                Plan newPlan = map.get(currentPlanName);
                Plan oldPlan = oldPlanMap.get(currentPlanName);
                if(!newPlan.getPrice().equals(oldPlan.getPrice())){
                    changedContent.append(map.get(currentPlanName).toString()).append("\n");
                }
            }
        }
        return changedContent.toString();

    }

    Map<String, Plan> toPlan(String s);

    default void write(String data) throws Exception {
        Writer writer = null;
        writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getFileName()), "utf-8"));
        writer.write(data);
        writer.close();
    }


    default String read() throws Exception {
        File myObj = new File(getFileName());
        if (!myObj.exists()) {
            return "";
        }
        Scanner myReader = new Scanner(myObj);
        StringBuilder sb = new StringBuilder();
        while (myReader.hasNextLine()) {
            sb.append(myReader.nextLine());
            sb.append("\n");
        }
        myReader.close();
        return sb.toString();
    }


    default void deleteFile() {
        File myObj = new File(getFileName());
        myObj.delete();
    }


}
