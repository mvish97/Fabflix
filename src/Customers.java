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
 * Servlet implementation class Customers
 */
@WebServlet("/login")
public class Customers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Customers() {
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
		// TODO Auto-generated method stub
		
		response.setContentType("application/json");
		
		
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
		
		//boolean valid = VerifyUtils.verify(gRecaptchaResponse); 
		boolean valid = true;
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Boolean is_mobile = Boolean.parseBoolean(request.getParameter("isMobile"));
		
		
		PrintWriter out = response.getWriter();
		
		try {
			/*Class.forName("com.mysql.jdbc.Driver").newInstance();
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
            
            
            String customerQuery = "select * from customers where email = '"+ email +"' and password = '" + password + "';";
            
            String employeeQuery = "select * from employees where email = '"+ email +"' and password = '" + password + "';";
            
            ResultSet rs = statement.executeQuery(employeeQuery);
            System.out.println(employeeQuery.toString());
            
            JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("result", false);
			jsonObject.addProperty("is_employee", false);
            
			if(rs.next() && (valid || is_mobile)) {
				jsonObject.addProperty("is_employee", true);
				jsonObject.addProperty("result", true);
				jsonObject.addProperty("id", email);
				jsonObject.addProperty("name", rs.getString("fullname"));
			}
			else {
				rs = statement.executeQuery(customerQuery);
				System.out.println(customerQuery.toString());
				
				if(rs.next() && (valid || is_mobile)) {
		            	jsonObject.addProperty("result", true);
		            	jsonObject.addProperty("name", rs.getString("firstName"));
		            	jsonObject.addProperty("id", rs.getString("id"));
				}
			}
        
            out.write(jsonObject.toString());
            
            rs.close();
            statement.close();
            dbcon.close();
            
		}
		catch (SQLException ex) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("err", ex.getMessage());
			out.write(jsonObject.toString());
			while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            }
        }
		catch (java.lang.Exception ex) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("err", ex.getMessage());
			out.write(jsonObject.toString());
			System.out.println(ex.getMessage());
        }
		
		out.close();
	}

}
