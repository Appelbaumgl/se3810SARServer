package se3810SARServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
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
		createTagsTable();
	}
	
	private void createTagsTable() {
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
			String query = "INSERT INTO tags (latitude, longitude, altitude, creator, title, content)"
					+ " VALUES ('" + tag.getDouble("latitude") + "', '" +
					tag.getDouble("longitude") + "', '" + 
					tag.getDouble("altitude") + "', '" +
					tag.getString("creator") + "', '" +
					tag.getString("title") + "', '" +
					tag.getString("content") + "')";
			System.out.println(query);
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doRequest(JSONObject request, HttpServletResponse response) throws JSONException, IOException {
		try {
			double latitude = request.getDouble("latitude");
			double longitude = request.getDouble("longitude");
			double distance = request.getDouble("distance");
			String query = "select *, POWER(SIN((" + latitude + "-latitude)*PI()/360),2) + COS(latitude*PI()/180) * COS(" + latitude + "*PI()/180) * POWER(SIN((" + longitude + "-longitude)*PI()/360),2) as a " + 
					"from tags " + 
					"having 2 * 6371000 * ATAN2(SQRT(a), SQRT(1-a)) <= " + distance;
			System.out.println(query);
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			JSONObject result = new JSONObject();
			JSONArray resultArray = new JSONArray();
			while(rs.next()) {
				JSONObject tag = new JSONObject();
				tag.put("latitude", rs.getDouble("latitude"));
				tag.put("longitude", rs.getDouble("longitude"));
				tag.put("altitude", rs.getDouble("altitude"));
				tag.put("creator", rs.getString("creator"));
				tag.put("title", rs.getString("title"));
				tag.put("content", rs.getString("content"));
				resultArray.put(tag);
			}
			result.put("tags", resultArray);
			result.write(response.getWriter());
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
