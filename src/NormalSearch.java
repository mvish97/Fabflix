import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.sql.DataSource;

/**
 * Servlet implementation class NormalSearch
 */
@WebServlet("/normalSearch")
public class NormalSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NormalSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		long startTime = System.nanoTime();
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		long tj = (long) 0.0;
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
            //Statement statement = dbcon.createStatement(); 
            
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
            
            String userInput = request.getParameter("query");
            
            String completeQuery = "";
            
            String query = "select id, title, year, dir, genres, actors, r.rating, ids "
            		+ "from (select m.id as id, m.title as title, m.year as year, m.director as dir, "
            		+ "group_concat(distinct g.name) as genres , "
            		+ "group_concat(distinct s.name) as actors, group_concat(distinct s.id) as ids "
            		+ "from movies m, genres_in_movies gim, genres g, stars_in_movies sim, stars s "
            		+ "where m.id = gim.movieID and gim.genreId = g.id and sim.movieID = m.id and sim.starId = s.id group by m.id) "
            		+ "as mov, ratings r where mov.id = r.movieId ";
            
            
            String[] tokens = userInput.split(" ");
            
            String link = NormalSearch.addPrefixSearch(tokens);
            
            completeQuery += query + link + QueryProcessor.pagination(request);
            
            query += link + ";";
            
            
            java.sql.PreparedStatement getMovies;
            getMovies = dbcon.prepareStatement(completeQuery);
            
            java.sql.PreparedStatement getMoviesSize;
            getMoviesSize = dbcon.prepareStatement(query);
	        
	        for(int i = 0; i < tokens.length; i++) {
	        		int temp = i + 1;
	        		getMovies.setString(temp, tokens[i] + "*");
	        		getMoviesSize.setString(temp, tokens[i] + "*");
	        }
	        
	        // JDBC Calls 
	        long startTS = System.nanoTime();
	        ResultSet rs = getMoviesSize.executeQuery();
	        
	        rs.last();
	        int size = rs.getRow();
	       
	        rs = getMovies.executeQuery();
	        long endTS = System.nanoTime();
			tj = endTS - startTime; // TJ 
	        // 
	        
	        System.out.println(getMovies.toString());
            
            JsonArray jsonArray = QueryProcessor.createResult(rs, size);
            
            
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
		long endTime = System.nanoTime();
		long ts = endTime - startTime; // TS
		
		String contextPath = getServletContext().getRealPath("/");
		String filePath = contextPath+"/timelog.txt";

		System.out.println(filePath);

		File myfile = new File(filePath);
		if(!myfile.exists()) {
			try {
				myfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		FileWriter writer;
		try {
			writer = new FileWriter(myfile, true);
			writer.write(ts+","+tj+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		out.close();
	}
	
	public static String addPrefixSearch(String[] userInput) {
		String completeQuery = "and mov.id in (select id from movies where";
		Boolean flag = false;
		
		
		for(int i = 0; i < userInput.length; i++) {
			if(userInput.length > 0) {
				if(!flag) {
					completeQuery += " match (title) AGAINST (" + "?" + " in boolean mode)";
					flag = true;
				}
				else {
					completeQuery += " and match (title) AGAINST (" + "?" + " in boolean mode)";
				}
			}
		}
		
		
		return completeQuery + ") ";
	}

}
