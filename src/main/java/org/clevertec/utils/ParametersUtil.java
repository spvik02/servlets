package org.clevertec.utils;


import org.clevertec.models.ProductInReceipt;
import org.clevertec.models.Receipt;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public class ParametersUtil {
    //разбивает пары значение1-значение2
    public static void parseParameters(String[] args, Receipt receipt) {
        List<ProductInReceipt> positions = new ArrayList<>();
        for (String arg : args) {
            if (arg.contains("Card")) {
                receipt.setDiscountCardId(Integer.parseInt(arg.substring(arg.indexOf("-") + 1)));
            } else {
                positions.add(new ProductInReceipt(
                        Integer.parseInt(arg.substring(0, arg.indexOf("-"))),
                        Integer.parseInt(arg.substring(arg.indexOf("-") + 1))
                ));
            }
        }
        receipt.setPositions(positions);
    }

    //читает параметры из указанного файла
    public static String[] readParametersFromFile(String fileName) {
        File file = new File("src/main/resources/dataFiles" + File.separator + fileName);
        String[] parameters = new String[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            if (file.exists()) {
                String line = reader.readLine();
                if (line != null) {
                    parameters = line.split(" ");
                } else throw new DataFormatException("File is empty!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return parameters;
    }

    public static String[] splitParameters(String line, String delimiter) {
        String[] parameters = new String[0];
        if (line != null) {
            parameters = line.split(delimiter);
        }
        return parameters;
    }

    /**
     * Create a map based on a .yaml file by the path specified in the parameter.
     * @param path path to .yaml file
     * @return Map object based on .yaml file
     */
    public static Map<String, Object> getMapFromYaml(String path){
        try {
            return new Yaml().load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
