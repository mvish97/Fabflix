

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
 * Servlet implementation class AddMovie
 */
@WebServlet("/addMovie")
public class AddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMovie() {
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
            
            
            String m_id = AddMovie.generateId("select * from movies order by id desc limit 1;", "tty", 3);
            String s_id = AddMovie.generateId("select * from stars order by id desc limit 1;", "nm", 2);
            System.out.println(m_id);
            System.out.println(s_id);
            
            String m_title = request.getParameter("m_title");
            String m_year = request.getParameter("m_year");
            String m_director = request.getParameter("m_director");
            String s_name = request.getParameter("s_name");
            String s_year = request.getParameter("s_year");
            String g_name = request.getParameter("g_name");
            
            
            String query = "call add_movie(" + AddMovie.checkIfNull(m_id) + ", " 
            		+ AddMovie.checkIfNull(m_title) + ", "
            		+ m_year + ", "
            		+ AddMovie.checkIfNull(m_director) + ", "
            		+ AddMovie.checkIfNull(s_id) + ", "
            		+ AddMovie.checkIfNull(s_name) + ", "
            		+ s_year + ", "
            		+ AddMovie.checkIfNull(g_name) + ");";
            
            
            
            System.out.println(query.toString());
            statement.execute(query);
            
	        jsonObject.addProperty("result", true);
	        
	        out.write(jsonObject.toString());
	        
	        statement.close();
	        dbcon.close();
		}
		catch (SQLException ex) {
			jsonObject.addProperty("result", false);
			jsonObject.addProperty("err", ex.getMessage());
			out.write(jsonObject.toString());
			while (ex != null) {
                System.out.println("SQL Exception:  " + ex.getMessage());
                ex = ex.getNextException();
            }
		}
		catch (java.lang.Exception ex) {
			jsonObject.addProperty("result", false);
			jsonObject.addProperty("err", ex.getMessage());
			out.write(jsonObject.toString());
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + ex.getMessage() + "</P></BODY></HTML>");
            return;
        }
		
	}
	
	public static String generateId(String query, String prefix, int index) {
		String id = "";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
	        Connection dbcon = DriverManager.getConnection(Credentials.getURL(), Credentials.getUser(), Credentials.getPassword());
	        Statement statement = dbcon.createStatement();
	        
	        ResultSet rs = statement.executeQuery(query);
	        
	        rs.next();
	    		String highestId = rs.getString("id");
	        int parseInt = Integer.parseInt(highestId.substring(index)) + 1;
	        
	        id = prefix + parseInt;
	        
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
            System.out.println(ex.getMessage());
        }
		
		
		return id;
	}
	
	public static String checkIfNull(String inp) {
		
		if(inp.equals("null")) {
			return inp;
		}
		else {
			return "'" + inp + "'";
		}
	}
}
