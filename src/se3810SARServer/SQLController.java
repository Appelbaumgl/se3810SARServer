package se3810SARServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.servlet.ServletContext;

import org.json.JSONException;
import org.json.JSONObject;

public class SQLController {
	
	private java.sql.Connection connection;
	private ServletContext servletContext;
	
	public SQLController(ServletContext servletContext) {
		this.servletContext = servletContext;
		try {
			String[] login = readDBConfig();
			Class.forName("com.mysql.jdbc.Driver");
			connection = java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/sar", login[0], login[1]);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createTagsTable() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS tags ("
	    			+ "id INT NOT NULL AUTO_INCREMENT,"
	    			+ "latitude DOUBLE NOT NULL,"
	    			+ "longitude DOUBLE NOT NULL,"
	    			+ "altitude DOUBLE NOT NULL,"
	    			+ "creator VARCHAR(255) NOT NULL,"
	    			+ "title VARCHAR(255) NOT NULL,"
	    			+ "content VARCHAR(255) NOT NULL,"
	    			+ "PRIMARY KEY (id))";
	    	Statement statement;
			statement = connection.createStatement();
			statement.executeUpdate(query);
	    	statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String[] readDBConfig() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(servletContext.getRealPath("/WEB-INF/lib/DB_Config.txt")));
			String[] login = {scanner.nextLine(), scanner.nextLine()};
			scanner.close();
			return login;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void saveTagToDatabase(JSONObject tag) throws JSONException {
		try {
			String query = "INSERT INTO tags (" + tag.getString("latitude") + ", " +
					tag.getString("longitude") + ", " + 
					tag.getString("altitude") + ", " +
					tag.getString("creator") + ", " +
					tag.getString("title") + ", " +
					tag.getString("content") + ")";
			Statement statement = connection.createStatement();
			statement.executeQuery(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
