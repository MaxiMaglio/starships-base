package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Reader {
    public static List<String> getLines(String directory) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(directory))){
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public Map<String, String> getMap(List<String> lines) {
        Map<String, String> map = new HashMap<>();
        for (String line : lines){
            String[] split = line.split(":");
            map.put(split[0], split[1]);
        }
        return map;
    }




}
