
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;


public class IrishRail {
  public static void main(String[] args) throws IOException, ParserConfigurationException, org.xml.sax.SAXException{
    File table = new File("table.html");

    //URL for XML data for the chosen station
    URL source = new URL("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins?StationCode=PTRTN&NumMins=90&format=xml");
    System.out.println(source);

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(source.openStream());
    BufferedWriter bw = new BufferedWriter(new FileWriter(table, true));

    StringBuilder bldr = new StringBuilder();
    String str;
    BufferedReader in = new BufferedReader(new FileReader(table));
    while((str = in.readLine())!=null)
      bldr.append(str);

    in.close();
    String content = bldr.toString();

    String realtime = "";

    //Gets the data from the XML file and formats it with HTML tags for later display
    NodeList trains = doc.getElementsByTagName("objStationData");
    
    realtime +="<style>\r\n"
    		+ "table {\r\n"
    		+ "  font-family: arial, sans-serif;\r\n"
    		+ "  border-collapse: collapse;\r\n"
    		+ "  width: 50%;\r\n"
    		+"  margin-left: auto;\r\n"
    		+ "  margin-right: auto;"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "td, th {\r\n"
    		+ "  border: 1px solid #dddddd;\r\n"
    		+ "  text-align: left;\r\n"
    		+ "  padding: 8px;\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "tr:nth-child(even) {\r\n"
    		+ "  background-color: #dddddd;\r\n"
    		+ "}\r\n"
    		+"h1 {"
    		+ "  text-align: center"
    		+ "}"
    		+ "</style>";
    
    realtime += "<h1>"+((Element) trains.item(0)).getElementsByTagName("Stationfullname").item(0).getTextContent()+"</h1>";
    String station=((Element) trains.item(0)).getElementsByTagName("Stationfullname").item(0).getTextContent();
    for(int i = 0; i < trains.getLength(); i++){
      Node train_node = trains.item(i);
      Element train = (Element) train_node;

      String train_table = "<table>"
    		+ "<tr><th>Destination: </th><td>"+train.getElementsByTagName("Destination").item(0).getTextContent()+"</td></tr>"
    		+ "<tr><th>Arrival Time at Destination: </th><td>"+train.getElementsByTagName("Destinationtime").item(0).getTextContent()+" + ("+train.getElementsByTagName("Late").item(0).getTextContent()+") minutes delay</td></tr>"
    		+ "<tr><th>Expected Arrival at "+station+": </th><td>"+train.getElementsByTagName("Exparrival").item(0).getTextContent()+"</td></tr>"
    		+ "<tr><th>Expected Departure from "+station+" : </th><td>"+train.getElementsByTagName("Expdepart").item(0).getTextContent()+"</td></tr>"
    		+ "<tr><th>Origin: </th><td>"+train.getElementsByTagName("Origin").item(0).getTextContent()+"</td></tr>"
    		+ "<tr><th>Last Recorded Location : </th><td>"+train.getElementsByTagName("Lastlocation").item(0).getTextContent()+"</td></tr>"
      		+ "</table></br>";
    
        realtime += train_table;
        
    }

    //Clears File
    PrintWriter writer = new PrintWriter(table);
    writer.print("");
    writer.close();

    realtime = "<!-- StartInfo -->" + realtime + "<!-- EndInfo -->";
    content = realtime.replaceAll("<!-- StartInfo -->.*<!-- EndInfo -->", realtime);

    //Writes the real time content to the HTML page
    bw.write(content);
    bw.close();
    System.out.println("File Updated!");
  }
}