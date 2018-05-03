import org.xml.sax.*;


import org.w3c.dom.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.xml.parsers.*;

public class Parser {
	
	public static void main(String[] args) {
		try {
			String path = args[1];
			Parser.movieParse(path);
		}catch(Exception ex){
			Parser.movieParse("/home/ubuntu/cs122b-winter18-team-13/stanford-movies/mains243.xml");
		}
	}
	
	public static void movieParse(String path) {
		Document xmlDoc = getDocument(path);//("/Users/maniperiasamy/Desktop/stanford-movies/mains243.xml");
		//System.out.println("Root: " + xmlDoc.getDocumentElement().getNodeName());
		
		NodeList filmsByDirector = xmlDoc.getElementsByTagName("directorfilms");
		
		for (int i=0; i<filmsByDirector.getLength(); i++) {
			Node node = filmsByDirector.item(i);
			Node child = node.getFirstChild();
			Element films = (Element)child.getNextSibling();
			NodeList filmList = films.getElementsByTagName("film");
			//System.out.println(filmList.item(0).getNodeName());
			NodeList dirInfo = child.getChildNodes();
			
			String director = null;
			
			for (int j=0; j<dirInfo.getLength(); j++) {
				Node info = dirInfo.item(j);
				if (info.getNodeName().equals("dirname")){
					director = info.getTextContent();
				}
			}
			
			Parser.getFilms(director, filmList);
			
			
		}
	}
	
	public static void getFilms(String dir, NodeList films) {
		
		for(int i = 0; i < films.getLength(); i++) {
			Node film = films.item(i);
			NodeList filmInfo = film.getChildNodes();
			
			String title = null;
			String year = null;
			String id = null;
			String[] cat = {};
			//System.out.println("DIRECTOR " + dir);
			for(int j = 0; j < filmInfo.getLength(); j++) {
				Node childFilm = filmInfo.item(j);
				if(childFilm.getNodeName().equals("t")) {
					title = childFilm.getTextContent();
				}
				else if(childFilm.getNodeName().equals("year")) {
					year = childFilm.getTextContent();
				}
				else if(childFilm.getNodeName().equals("fid")) {
					id = childFilm.getTextContent();
				}
				else if(childFilm.getNodeName().equals("cats")) {
					cat = childFilm.getTextContent().split(" ");
				}
			}
			
			
				//System.out.println("FILM " + title + " " + year + " " + id + " " + dir);
				
				for(int c = 0; c < cat.length; c++) {
					try {
						String temp = cat[c];
						if(temp.isEmpty()) { temp = "null"; }
						Parser.addMovie(id, title, year, dir, "null", "null", null, temp);
					} catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
	}
	
	
	public static String processArrays(String[] cat) {
		String r = "";
		for(int i = 0; i < cat.length; i++) {
			r += cat[i] + ", ";
		}
		return r;
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
	
	public static void addMovie(String m_id, String m_title, String m_year, String m_director, String s_id, String s_name, String s_year, String g_name) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
            //dbcon.setAutoCommit(false);
            Statement statement = dbcon.createStatement();
            
            if(m_year == null) {
            		m_year = "0000";
            }
        
	        if(m_year.equals("")) {
	        		m_year = "0000";
	        }
            
            try {
	        		Integer.parseInt(m_year);
	        } catch(Exception ex) {
	        		m_year = "0000";
	        }
            
            String crappyPart;
            
            if (g_name.contains("'")) {
            		crappyPart = "\"" + g_name + "\"); ";
            } else {
            		crappyPart = "'" + g_name + "'); ";
            }
            
            String query = "call addMovie(" + Parser.checkIfNull(m_id) + ", " 
            		+ "\"" + m_title + "\", "
            		+ m_year + ", "
            		+ Parser.checkIfNull(m_director) + ", "
            		+ Parser.checkIfNull(s_id) + ", "
            		+ Parser.checkIfNull(s_name) + ", "
            		+ s_year + ", "
            		+ crappyPart;

            /////
            String updateQuery = "call addMovie(?, ?, ?, ?, ?, ?, ?, ?);";
            java.sql.PreparedStatement updateMovies;
            updateMovies = dbcon.prepareStatement(updateQuery);
            
            updateMovies.setString(1, m_id); 
            updateMovies.setString(2, m_title); 
            updateMovies.setString(3, m_year); 
            updateMovies.setString(4, m_director); 
            updateMovies.setString(5, s_id);
            updateMovies.setString(6, s_name);
            updateMovies.setString(7, s_year); 
            updateMovies.setString(8, g_name); 
            
            updateMovies.executeQuery();
            /////
            
            //System.out.println(query.toString());
            //statement.execute(query);
            
            //dbcon.commit();
            dbcon.close();
            statement.close();
		}
		catch(java.sql.SQLNonTransientConnectionException se) {
			System.out.println("SQLNonTransientConnectionException, performing garbage collection");
			System.gc();
		}
		catch(java.sql.SQLSyntaxErrorException s) {
			System.out.println("Syntax Error: " + s.getMessage());
		}
		catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Duplicate Entry");
		}
		catch (java.lang.NullPointerException e) {
			System.out.println("One of the movie details was NULL");
		}
		catch (java.lang.Exception ex) {
            System.out.println(ex.getMessage());
        }
	}
	
	public static String checkIfNull(String inp) {
		
		if(inp.equals("null")) {
			return inp;
		}
		else {
			return "'" + inp + "'";
		}
	}
}
