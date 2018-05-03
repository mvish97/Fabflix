import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QueryProcessor {
	
	public static JsonArray createResult(ResultSet rs, int size) {
		JsonArray jsonArray = new JsonArray();
        
        try {
	        while(rs.next()) {
	        		rs.getRow();
	        	
	        		JsonObject jsonObject = new JsonObject();
	        		jsonObject.addProperty("size", size);
	        		jsonObject.addProperty("id", rs.getString("id"));
	        		jsonObject.addProperty("title", rs.getString("title"));
	        		jsonObject.addProperty("year", rs.getInt("year"));
	        		jsonObject.addProperty("director", rs.getString("dir"));
	        		jsonObject.addProperty("actors", rs.getString("actors"));
	        		jsonObject.addProperty("genres", rs.getString("genres"));
	        		jsonObject.addProperty("rating", rs.getDouble("rating"));
	        		jsonObject.addProperty("star_ids", rs.getString("ids"));
	        		jsonArray.add(jsonObject);
	        }
        }
        catch (SQLException ex) {
			while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            }
		}
        
        return jsonArray;
	}
	
	public static String pagination(HttpServletRequest request) {
		String link = "";
		int limit = 20;
		int offset = 0;
		
		try {
			limit = Integer.parseInt(request.getParameter("limit"));
			offset = Integer.parseInt(request.getParameter("offset")) * limit;
		}
		catch(Exception e){
			System.out.println("Wrong Parameter Name");
		}
		
		link += " limit " + limit + " offset " + offset + " ;";
		
		return link;
	}
	
	public static int rsCount(String query) {
		int count = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
	        Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
	        Statement statement = dbcon.createStatement();
	        
	        ResultSet rs = statement.executeQuery(query);
            
            rs.last();
            count = rs.getRow();
            
            rs.close();
            statement.close();
            dbcon.close();
		}
		catch (java.lang.Exception ex) {
            System.out.println("Error loading the drivers");
        }
		
		
		return count;
	}
	
	public static ArrayList<String> processInput(String in) {
		ArrayList<String> processed =  new ArrayList<String>();
		
		String temp = "";
		for(int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			
			if(c != ' ' && c != ',') {
				temp += c;
			}
			else {
				processed.add(temp);
				
				temp = "";
			}
		}
		processed.add(temp);
		
		return processed;
	}
	
	public static void insertMovie(String title, String year, String director, String id) {
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
	        Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
	        //Statement statement = dbcon.createStatement();
	        
	        
	        String query = "insert into movies(id, title, year, director) values( '" + id 
	        		+ "', '" + title + "', "
	        		+ year + ", '" + director + "');";
	         
	        //////
	        String updateQuery = "insert into movies(id, title, year, director) values(?, ?, ?);";
	        java.sql.PreparedStatement updateStars;
	        updateStars = dbcon.prepareStatement(updateQuery);
            
            updateStars.setString(1, id); 
            updateStars.setString(2, title); 
            updateStars.setString(3, year); 
            updateStars.setString(4, director); 
            
            updateStars.executeQuery();
            ///
	        
	        //statement.execute(query);
	        
	        System.out.println(query.toString());
	        
	        dbcon.close();
            //statement.close();
		}
		catch (java.lang.Exception ex) {
            System.out.println("Error loading the drivers");
        }
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

            /*
            if(year == null) {
            		year = "null";
            }
            
            if(year.equals("")) {
            		year = "null";
            } */
            
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
            
	        System.out.println(query.toString());
	        
	        //statement.execute(query);
	        
	        dbcon.close();
            statement.close();
		}
		catch (java.lang.Exception ex) {
            System.out.println(ex.getMessage());
        }
	}
	
	public static void addMovie(String m_id, String m_title, String m_year, String m_director, String s_id, String s_name, String s_year, String g_name) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
            Statement statement = dbcon.createStatement();
            
            /*if(m_year == null) {
            		m_year = "0000";
            }
        
	        if(m_year.equals("")) {
	        		m_year = "0000";
	        } */
            
            try {
	        		Integer.parseInt(m_year);
	        } catch(Exception ex) {
	        		m_year = null;
	        }
            
            String crappyPart;
            
            if (g_name.contains("'")) {
            		crappyPart = "\"" + g_name + "\"); ";
            } else {
            		crappyPart = "'" + g_name + "'); ";
            }
            
            String query = "call addMovie(" + AddMovie.checkIfNull(m_id) + ", " 
            		+ "\"" + m_title + "\", "
            		+ m_year + ", "
            		+ AddMovie.checkIfNull(m_director) + ", "
            		+ AddMovie.checkIfNull(s_id) + ", "
            		+ AddMovie.checkIfNull(s_name) + ", "
            		+ s_year + ", "
            		+ crappyPart;
            
            //System.out.println(query.toString());
            statement.execute(query);
            
            dbcon.close();
            statement.close();
		}
		catch(java.sql.SQLNonTransientConnectionException se) {
			System.out.println("Transient Error: " + se.getMessage());
		}
		catch(java.sql.SQLSyntaxErrorException s) {
			System.out.println("Syntax Error: " + s.getMessage());
		}
		catch (java.sql.SQLIntegrityConstraintViolationException e) {
			System.out.println("Duplicate");
		}
		catch (java.lang.Exception ex) {
            System.out.println(ex.getMessage());
        }
	}
	
	public static void main(String[] args) {
		
	}
}
