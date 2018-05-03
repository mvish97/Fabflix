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
 * Servlet implementation class Search
 */
@WebServlet("/search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
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
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		response.setContentType("application/json");
		
		String link = Search.processRequest(request);
		
		PrintWriter out = response.getWriter();
		
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
            
            
            
            
            String limitedQuery  = "";
            
            String query = "select id, title, year, dir, genres, actors, r.rating, ids "
            		+ "from (select m.id as id, m.title as title, m.year as year, m.director as dir, "
            		+ "group_concat(distinct g.name) as genres , "
            		+ "group_concat(distinct s.name) as actors, group_concat(distinct s.id) as ids "
            		+ "from movies m, genres_in_movies gim, genres g, stars_in_movies sim, stars s "
            		+ "where m.id = gim.movieID and gim.genreId = g.id and sim.movieID = m.id and sim.starId = s.id group by m.id) "
            		+ "as mov, ratings r where mov.id = r.movieId";
            
            
            limitedQuery += query + link + QueryProcessor.pagination(request);
            query += link + ";";
            
            java.sql.PreparedStatement getMovies;
            getMovies = dbcon.prepareStatement(limitedQuery);
            
            java.sql.PreparedStatement getMoviesSize;
            getMoviesSize = dbcon.prepareStatement(query);
            
            String[] inputs = Search.getUserInput(request);
            for(int i = 0; i < 4; i++) {
            		if(inputs[i].length() > 0) {
            			getMovies.setString(i + 1, "%" + inputs[i] + "%");
            			getMoviesSize.setString(i + 1, "%" + inputs[i] + "%");
            		}
            }
            
            ResultSet rs = getMoviesSize.executeQuery();
            rs.last();
            
            int size = rs.getRow();
            
            rs = getMovies.executeQuery();
            
            System.out.println(getMovies.toString());
            System.out.println(getMoviesSize.toString());
            
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
		out.close();
	}
	
	public static String processRequest(HttpServletRequest request) {
		String link = "";
		
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		
		
		if(title.length() > 0) {
			link += " and title LIKE " + "?";
		}
		
		if(year.length() > 0) {
			link += " and year LIKE " + "?";
		}
		
		if(director.length() > 0) {
			link += " and dir LIKE " + "?";
		}
		
		if(star.length() > 0) {
			link += " and actors LIKE " + "?";
		}
		
		return link;
	}
	
	public static String[] getUserInput(HttpServletRequest request) {
		String[] inputs = new String[4];
		
		inputs[0] = request.getParameter("title");
		inputs[1] = request.getParameter("year");
		inputs[2] = request.getParameter("director");
		inputs[3] = request.getParameter("star");
		
		return inputs;
	}

}