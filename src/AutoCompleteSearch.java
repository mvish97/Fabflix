

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class AutoCompleteSearch
 */
@WebServlet("/autoCompleteSearch")
public class AutoCompleteSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutoCompleteSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
             
            
			// Pooling Code
            Context initCtx = new InitialContext();
            if (initCtx == null)
                System.out.println("initCtx is NULL");

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            		System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
            
            if (ds == null)
            		System.out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
            		System.out.println("dbcon is null.");
            //
            //Statement statement = dbcon.createStatement();
            
            String userInput = request.getParameter("userInput");
            String[] tokens = userInput.split(" ");

            String limit = " limit 10;";
            
            String movieQuery = "select id, title from movies" + AutoCompleteSearch.addPrefixSearch(userInput, "title") + limit  ;
            String starQuery = "select id, name from stars" + AutoCompleteSearch.addPrefixSearch(userInput, "name") + limit;
           
            // Prepared Statements
            java.sql.PreparedStatement getMovies;
            getMovies = dbcon.prepareStatement(movieQuery);
            
            java.sql.PreparedStatement getStars;
            getStars = dbcon.prepareStatement(starQuery);
            
            for(int i = 0; i < tokens.length; i++) {
	        		int temp = i + 1;
	        		getMovies.setString(temp, tokens[i] + "*");
	        		getStars.setString(temp, tokens[i] + "*");
            }
            
            System.out.println(getMovies.toString());
            System.out.println(getStars.toString());
            
            JsonArray jsonArray = new JsonArray();
            
            ResultSet rs = getMovies.executeQuery();
            
            AutoCompleteSearch.processResults(rs, jsonArray, "Movie");
            
            rs = getStars.executeQuery();
            
            AutoCompleteSearch.processResults(rs, jsonArray, "Star");
            
            out.write(jsonArray.toString());
            
            rs.close();
            //statement.close();
            dbcon.close();
            
		}
		catch (SQLException ex) {
			while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            }
		}
		catch (java.lang.Exception ex) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public static String addPrefixSearch(String userInput, String col) {
		String query = "";
		Boolean flag = false;
		String[] tokens = userInput.split(" ");
		
		for(String token: tokens) {
			if(token.length() > 0) {
				if(!flag) {
					query += " where match (" + col + ") AGAINST (" + "?" + " in boolean mode)";
					flag = true;
				}
				else {
					query += " and match (" + col + ") AGAINST (" + "?" + " in boolean mode)";
				}
			}
		}
		
		
		return query;
	}
	
	public static void processResults(ResultSet rs, JsonArray data, String category) {
		
		int limit = 0;
		int counter = 0;

		if(data.size() == 0) {
			limit = 5;
		}
		else {
			limit = 10 - data.size();
		}
		
		try {
			while(rs.next() && counter < limit) {
				
				JsonObject jsonObject = new JsonObject();
				JsonObject additionData = new JsonObject();
				additionData.addProperty("id", rs.getString("id"));
				
				if(category.equals("Movie")) {
					jsonObject.addProperty("value", rs.getString("title"));
					additionData.addProperty("category", "Movies");
				}
				else {
					jsonObject.addProperty("value", rs.getString("name"));
					additionData.addProperty("category", "Stars");
				}
				jsonObject.add("data", additionData);
				data.add(jsonObject);
				
				counter += 1;
			}
		}
		catch (java.lang.Exception ex) {
            System.out.println("SQL Exception" + ex.getMessage());
            return;
        }
		
	}
}
