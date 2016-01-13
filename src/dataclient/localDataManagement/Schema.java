package dataclient.localDataManagement;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple storage class that holds the Objects that map the different data stru
 * @author Michael
 *
 */
public class Schema {
	
	public static final String NUMBER = "Number", STRING = "String", BOOLEAN = "Boolean";
	
	private JSONObject schemaObject;
	private String name;
	
	public Schema(String name){
		this.name = name;
		schemaObject = new JSONObject();
	}
	
	public String getName(){
		return name;
	}
	
	public void add(String name, String type){
		try {
			schemaObject.append(name, type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject getJSONObject(){
		return schemaObject;
	}
}
