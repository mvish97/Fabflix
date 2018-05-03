

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

//import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetStarDetails
 */
@WebServlet("/getStarDetails")
public class GetStarDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStarDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		
		try {
			/* Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
            Statement statement = dbcon.createStatement(); */
            
        
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
            Statement statement = dbcon.createStatement();
            
            String id = request.getParameter("id");
            
            String query = "select s.id as id, s.name as name, birthYear, group_concat(title) as movies, "
            		+ " group_concat(m.id) as movie_ids from stars s, movies m, stars_in_movies sim "
            		+ "where m.id = sim.movieID and sim.starId = s.id "
            		+ "and s.id = '" + id + "' group by s.id;";
            
	        System.out.println(query.toString());
	        
	        ResultSet rs = statement.executeQuery(query);
	        
	        
	        JsonObject jsonObject = new JsonObject();
        
	        while(rs.next()) {
	        		rs.getRow();
	        		
	        		jsonObject.addProperty("id", rs.getString("id"));
	        		jsonObject.addProperty("name", rs.getString("name"));
	        		jsonObject.addProperty("birthYear", rs.getString("birthYear"));
	        		jsonObject.addProperty("movies", rs.getString("movies"));
	        		jsonObject.addProperty("movieIds", rs.getString("movie_ids"));
	        }
        
	        out.write(jsonObject.toString());
	        
	        rs.close();
	        statement.close();
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
	}
}
