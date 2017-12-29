package com.ag.noreader.util;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by agufed on 12/28/17.
 */

public class Utilize {


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
        if(contents == null){
            return null;
        }

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

    public static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }
}
