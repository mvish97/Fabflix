

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
 * Servlet implementation class GetDBDetais
 */
@WebServlet("/getDBDetails")
public class GetDBDetais extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDBDetais() {
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
            
            
            String query = "SELECT t.table_name as tableName, group_concat(c.column_name) as columns, group_concat(c.data_type) as dataType "
            		+ "FROM INFORMATION_SCHEMA.columns c, INFORMATION_SCHEMA.tables t "
            		+ "WHERE c.table_name = t.table_name and TABLE_TYPE = 'BASE TABLE' and t.table_schema = 'moviedb' group by t.table_name;";
            
            ResultSet rs = statement.executeQuery(query);
            System.out.println(query.toString());
            
            JsonArray jsonArray = new JsonArray();
            
            	while(rs.next()) {
            		rs.getRow();
            		
            		JsonObject jsonObject = new JsonObject();
            		jsonObject.addProperty("tableName", rs.getString("tableName"));
            		jsonObject.addProperty("columns", rs.getString("columns"));
            		jsonObject.addProperty("dataType", rs.getString("dataType"));
            		jsonArray.add(jsonObject);
            	}
         
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

}
