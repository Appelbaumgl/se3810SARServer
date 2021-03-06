package se3810SARServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SARServer1
 */
@WebServlet("/SARServer")
public class SARServer extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	SQLController sqlController;
	       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SARServer() {
        super();
    }
    
    @Override
    public void init() {
    	sqlController = new SQLController(getServletContext());
    }
    
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("GET");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	try {
    		System.out.println("POST");
    		char[] cbuf = new char[request.getContentLength()];
        	request.getReader().read(cbuf);
        	String jsonString = new String(cbuf);
        	System.out.println(jsonString);
			JSONObject json = new JSONObject(jsonString);
			if(json.has("tag")) {
				sqlController.saveTagToDatabase((JSONObject) json.get("tag"));
			} else if(json.has("request")) {
				sqlController.doRequest((JSONObject) json.get("request"), response);
			}
    	} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
