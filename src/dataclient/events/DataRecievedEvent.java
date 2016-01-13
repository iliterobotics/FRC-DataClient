package dataclient.events;

import org.json.JSONException;
import org.json.JSONObject;

public class DataRecievedEvent{

	private final String collection;
	private final JSONObject[] docs;
	
	public DataRecievedEvent(String collection, JSONObject ... docs) {
		this.collection = collection;
		this.docs = docs;
		try {
			if(docs != null && docs.length > 0)System.out.println("RECIEVED " + docs[0].getJSONArray("docs"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getCollectionName(){
		return collection;
	}
	
	public JSONObject[] getDocs(){
		return docs;
	}
	
}

