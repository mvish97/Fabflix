import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class castParser {
	public static void main(String[] args) {
		try {
			String path = args[1];
			castParser.castParse(path);
		}catch(Exception ex){
			castParser.castParse("/home/ubuntu/cs122b-winter18-team-13/stanford-movies/casts124.xml");
		}
	}
	
	public static void castParse(String path) {
		Document xmlDoc = getDocument(path);//("/Users/maniperiasamy/Desktop/stanford-movies/casts124.xml");
		//System.out.println("Root: " + xmlDoc.getDocumentElement().getNodeName());
		
		NodeList dirFilms = xmlDoc.getElementsByTagName("dirfilms");
		
		for (int i=0; i< dirFilms.getLength(); i++) {
			Element dirFilmInfo = (Element)dirFilms.item(i);
			NodeList nodes = dirFilmInfo.getElementsByTagName("filmc");
			for(int j=0; j<nodes.getLength(); j++) {
				castParser.handleFilmc((Element)nodes.item(j));
			}
		}
	}
	
	private static void handleFilmc(Element filmc) {
		// Handle the m tag here
		NodeList nodes = filmc.getElementsByTagName("m");

		for(int i=0; i<nodes.getLength(); i++) {
			String s_name = null, m_title = null, movie_id = null;
			NodeList m = nodes.item(i).getChildNodes();
			for(int j=0; j<m.getLength(); j++) {
				if(m.item(j).getNodeName().equals("f")) {
					movie_id = m.item(j).getTextContent();
				}
				if(m.item(j).getNodeName().equals("t")) {
					m_title = m.item(j).getTextContent();
				}
				if(m.item(j).getNodeName().equals("a")) {
					s_name = m.item(j).getTextContent();
				}
			}
			
			//System.out.println(movie_id + " " + s_name + " " + m_title);
			Parser.addMovie(movie_id, m_title, "0", "", "null", s_name, null, "null");
			
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
}
