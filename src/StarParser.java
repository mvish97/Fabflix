import org.xml.sax.*;
import org.w3c.dom.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.*;

public class StarParser {
	public static void main(String[] args) {
		try {
			String path = args[1];
			StarParser.starParse(path);
		}catch(Exception ex){
			StarParser.starParse("/home/ubuntu/cs122b-winter18-team-13/stanford-movies/actors63.xml");
		}
	}
	
	public static void starParse(String path) {
		//Document xmlDoc = getDocument("/Users/maniperiasamy/Desktop/stanford-movies/actors63.xml");
		Document xmlDoc = getDocument(path);
		System.out.println("Root: " + xmlDoc.getDocumentElement().getNodeName());
		
		NodeList actors = xmlDoc.getElementsByTagName("actor");
		
		for (int i=0; i<actors.getLength(); i++) {
			Node node = actors.item(i);
			NodeList actorInfo = node.getChildNodes();
			String name = null;
			String year = null;
			for (int j=0; j< actorInfo.getLength(); j++) {
				Node info = actorInfo.item(j);
				if (info.getNodeName().equals("stagename")){
					name = info.getTextContent();
				}
				if (info.getNodeName().equals("dob")){
					year = info.getTextContent();
				}
			}
			
			//System.out.println(name + " " + year);
			StarParser.insertStar(name, year);
		}
	}
	
	private static Document getDocument(String docString) {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(true);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			return builder.parse(new InputSource(docString));
		}
		catch(Exception ex) {
			 System.out.println(ex.getMessage());
		}
		
		return null;
	}
	
	public static void insertStar(String name, String year) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
            Statement statement = dbcon.createStatement();
            
            String idQuery = "select * from stars order by id desc limit 1";
            
            ResultSet rs = statement.executeQuery(idQuery);
            
            int parseint = 0;
            	if(rs.next()) {
	            	String highestId = rs.getString("id");
	            parseint = Integer.parseInt(highestId.substring(2)) + 1;
            	}
            	else {
            		parseint = 1;
            	}
            
            String id = "nm" + String.format("%07d", parseint);

            
            try {
            		Integer.parseInt(year);
            } catch(Exception ex) {
            		year = null;
            }
            
            
            String crappyPart;
            
            if (name.contains("'")) {
            		crappyPart = "\"" + name + "\", ";
            } else {
            		crappyPart = "'" + name + "', ";
            }
            
            
            String query = "insert into stars(id, name, birthYear) values("
            		+ "'" + id + "', "
            		+ crappyPart
            		+ "" + year + ");";
            ////
            String updateQuery = "insert into stars(id, name, birthYear) values(?, ?, ?);";
	        java.sql.PreparedStatement updateStars;
	        updateStars = dbcon.prepareStatement(updateQuery);
            
            updateStars.setString(1, id); 
            updateStars.setString(2, name); 
            updateStars.setString(3, year);
            
            //updateStars.executeQuery();
            updateStars.execute();
            ////
            
	        //System.out.println(query.toString());
	        
	        //statement.execute(query);

	        
	        dbcon.close();
            
		}
		catch (java.lang.Exception ex) {
            System.out.println(ex.getMessage());
        }
	}
}
