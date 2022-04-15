import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data2.json");


        list =parseXML("data.xml");
        json = listToJson(list);
        writeString(json, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    public static List<Employee> parseXML(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Node root = doc.getDocumentElement();
            NodeList employeeNodeList = root.getChildNodes();

            List<Employee> list = new ArrayList<>();

            for (int i = 0; i < employeeNodeList.getLength(); i++) {
                Node employeeNode = employeeNodeList.item(i);
                if (employeeNode.getNodeType() != Node.ELEMENT_NODE || !"employee".equals(employeeNode.getNodeName())) continue;

                Employee employee = new Employee();
                list.add(employee);

                NodeList nodeListProperties = employeeNode.getChildNodes();
                for (int k = 0; k < nodeListProperties.getLength(); k++) {
                    Node nodeProperty = nodeListProperties.item(k);
                    if (Node.ELEMENT_NODE != nodeProperty.getNodeType()) continue;

                    String name = nodeProperty.getNodeName();
                    if ("id".equals(name))
                    {
                        employee.id = Long.parseLong(nodeProperty.getFirstChild().getNodeValue());
                    }
                    else if ("firstName".equals(name))
                    {
                        employee.firstName = nodeProperty.getFirstChild().getNodeValue();
                    }
                    else if ("lastName".equals(name))
                    {
                        employee.lastName = nodeProperty.getFirstChild().getNodeValue();
                    }
                    else if ("country".equals(name))
                    {
                        employee.country = nodeProperty.getFirstChild().getNodeValue();
                    }
                    else if ("age".equals(name))
                    {
                        employee.age = Integer.parseInt(nodeProperty.getFirstChild().getNodeValue());
                    }
                }
            }
            return list;
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            return new ArrayList<Employee>();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String str, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(str);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
