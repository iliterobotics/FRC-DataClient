package dataclient;

import org.json.JSONObject;

import dataclient.localDataManagement.Schema;

public interface DataClient {
	
	JSONObject get(String collection, String id);
	JSONObject getDirect(String collection, String id);
	void postObject(JSONObject object);
	void pushSchema(Schema scema);

}
