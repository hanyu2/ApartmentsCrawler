import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TrioPlan extends Plan{
    String planName;
    String configuration;
    String price;

    @Override
    public Plan createPlan() {
        return new TrioPlan();
    }

    @Override
    public String processPlanName(String planName) {
        return planName;
    }

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
    public String serialize(Map<String, Plan> map) {
        return map.values().stream().map(Plan::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return "Plan{" +
                planName + "," +
                configuration + ',' +
                price+
                "}";
    }

    public static Map<String, Plan> toPlan(String s){
        Map<String,Plan> res = new HashMap<String,Plan>();
        if(s.isEmpty()){
            return res;
        }
        String[] planStrs = s.split("\\r?\\n|\\r");
        for(String str : planStrs){
            Plan plan = new TrioPlan();
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
