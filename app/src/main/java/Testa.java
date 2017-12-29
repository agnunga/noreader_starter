import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by agufed on 12/28/17.
 */

public class Testa {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder("");
        sb.append("   aaa \n");
        sb.append("     bbb          \n");
        sb.append("ccc  \n");
        sb.append("\n");
        sb.append("ddd\r\n");
        sb.append("\n Olo oo \nJ A D  pal ase - ddd\r\n");
        sb.append("\n Olooo \nJ A D E pal ase - ddd\r\n");
        sb.append("\n O l o o o \nJ A D E pal ase - ddd\r\n");
        sb.append("\r\n");
        sb.append("eee\n");

        String text = sb.toString();
        System.out.println("---Original---");
        System.out.println(text);

        System.out.println("---Split---");
        int count = 1;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            System.out.println("line " + count++ + " :|" + line+ "|:");
        }

        for (String line:getListOfContent(text)) {
            System.out.println("line :|" + line+ "|:");

        }
        System.out.println("\n\nDominant :|" + getDorminantContent(getListOfContent(text))+ "|:");

    }


    public static List<String> getListOfContent(String content){
        List<String> list = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            line = line.replaceAll("\\s", "");
//            line = line.replaceAll(" ", "");
            list.add(line);
        }
        System.out.println("Size :"+list.size());
        list.removeAll(Arrays.asList("", null));
        System.out.println("Reduced to :"+list.size());
        return list;
    }

    public static String getDorminantContent(List<String> contents){

        //for java 1.8
        /*//Filter by leghth of string
        List<String> list2 = getListOfContent(text).stream()
                .filter(s -> s.length() == 16)
                .collect(Collectors.toList());*/

      /*  List<String> winners = contents.stream()
                .collect(groupingBy(x->x, counting()))  // make a map of string to count
                .entrySet().stream()
                .collect(groupingBy(                    // make it into a sorted map
                        Map.Entry::getValue,            // of count to list of strings
                        TreeMap::new,
                        mapping(Map.Entry::getKey, toList()))
                ).lastEntry().getValue();*/

        Map<String, Integer> map = new HashMap<>();
        for(int i = 0; i < contents.size(); i++){
            String s = contents.get(i);
            if(map.get(s) == null){
                map.put(s,1);
            }else{
                map.put(s, map.get(s) + 1);
            }
        }
        int largest = 0;
        String stringOfLargest = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            if( value > largest){
                largest = value;
                stringOfLargest = key;
            }
        }
        return  stringOfLargest;
    }


}