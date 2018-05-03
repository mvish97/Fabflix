

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
//import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetMovieDetails
 */
@WebServlet("/getMovieDetails")
public class GetMovieDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMovieDetails() {
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
		
		String link = GetMovieDetails.processRequest(request);
		
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
            
            
            String query = "select id, title, year, dir, genres, actors, r.rating, ids "
            		+ "from (select m.id as id, m.title as title, m.year as year, m.director as dir, "
            		+ "group_concat(distinct g.name) as genres , group_concat(distinct s.name) as actors, "
            		+ "group_concat(distinct s.id) as ids from movies m, genres_in_movies gim, genres g, stars_in_movies sim, stars s "
            		+ "where m.id = gim.movieID and gim.genreId = g.id and sim.movieID = m.id and sim.starId = s.id "
            		+ "group by m.id) as mov, ratings r where mov.id = r.movieId and";
            
	        query += link + " order by r.rating desc;";
	        System.out.println(query.toString());
	        
	        ResultSet rs = statement.executeQuery(query);
	        
	        int count = 0;
	        
	        JsonArray jsonArray = QueryProcessor.createResult(rs, count);
        
	        out.write(jsonArray.toString());
	        
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
	
	public static String processRequest(HttpServletRequest request) {
		String link = "";
		
		String id = request.getParameter("id");
		
		link += " id = '" + id + "'";
		
		return link;
	}

}
