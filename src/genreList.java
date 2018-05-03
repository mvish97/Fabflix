

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
 * Servlet implementation class genreList
 */
@WebServlet("/genreList")
public class genreList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public genreList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
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
            
            String query = "select name from genres;";
            
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            int counter = 1;
            
            while(rs.next()) {
            		rs.getRow();
            		JsonObject jsonObject = new JsonObject();
            		jsonObject.addProperty(String.valueOf(counter), rs.getString("name"));
            		jsonArray.add(jsonObject);
            		counter++;
            }
            System.out.println(jsonArray.toString());
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
		out.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
