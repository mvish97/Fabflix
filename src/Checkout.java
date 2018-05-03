import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;

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
 * Servlet implementation class Checkout
 */
@WebServlet("/checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Checkout() {
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
            
            
            String checkingQuery = "select * from creditcards "
            		+ "where id = '" + request.getParameter("id") + "'"
            		+ " and firstName = '" + request.getParameter("firstName") + "'"
            		+ " and lastName = '" + request.getParameter("lastName") + "'"
            		+ " and expiration = '" + request.getParameter("expiration")  +  "';";
            
            /*String updateQuery = "insert into "
            		+ "sales(customerId, movieId, saleDate) "
            		+ "values("
            		+ request.getParameter("customerId") + ", "
            		+ "'" + request.getParameter("movieId") + "', "
            		+ "'" + request.getParameter("date") + "');"; */
            		
            
            System.out.println(checkingQuery.toString());
            
            //System.out.println(updateQuery.toString());
            
            ResultSet rs = statement.executeQuery(checkingQuery);
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("valid", false);
            System.out.println("JSON made");
            
            if(rs.first()) {
            		jsonObject.addProperty("valid", true);
            		
            		ArrayList<String> movieIds = QueryProcessor.processInput(request.getParameter("movieId"));
            		ArrayList<String> quantitys = QueryProcessor.processInput(request.getParameter("quantity"));
            		
            		for(int i = 0; i < movieIds.size(); i++) {
            			String updateQuery = Checkout.makeQuery(request.getParameter("customerId"), 
            					movieIds.get(i), request.getParameter("date"));
            			
            			System.out.println(movieIds.get(i));
            			System.out.println(updateQuery.toString());
            			
            			for(int q = 0; q < Integer.parseInt(quantitys.get(i)); q++) {
            				statement.execute(updateQuery);
            			}
            		}
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
	
	public static String makeQuery(String customerId, String movieId, String date) {
		String updateQuery = "insert into "
        		+ "sales(customerId, movieId, saleDate) "
        		+ "values("
        		+ customerId + ", "
        		+ "'" + movieId + "', "
        		+ "'" + date + "');";
		
		return updateQuery;
	}
}
