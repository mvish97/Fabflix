

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

import com.google.gson.JsonObject;

/**
 * Servlet implementation class AddNewStar
 */
@WebServlet("/addNewStar")
public class AddNewStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewStar() {
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
		JsonObject jsonObject = new JsonObject();
		
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
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedbMaster");
            
            if (ds == null)
            		System.out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
            		System.out.println("dbcon is null.");
            //
            Statement statement = dbcon.createStatement();
            
            String idQuery = "select * from stars order by id desc limit 1";
            
            ResultSet rs = statement.executeQuery(idQuery);
            
            rs.next();
            	String highestId = rs.getString("id");
            int parseInt = Integer.parseInt(highestId.substring(2)) + 1;
            
            String id = "nm" + parseInt;
            String name = request.getParameter("name");
            String birthYear = request.getParameter("birthYear");
            String query = "insert into stars(id, name, birthYear) values("
            		+ "'" + id + "', "
            		+ "'" + name + "', "
            		+ "'" + birthYear + "');";
            
	        System.out.println(query.toString());
	        
	        statement.execute(query);
	        
	        jsonObject.addProperty("result", true);
	        
	        out.write(jsonObject.toString());
	        
	        statement.close();
	        dbcon.close();
		}
		catch (SQLException ex) {
			jsonObject.addProperty("result", false);
	        
	        out.write(jsonObject.toString());
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
