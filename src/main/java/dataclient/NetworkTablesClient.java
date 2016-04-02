package dataclient;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.localDataManagement.Schema;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesClient implements DataClient{

	private NetworkTable netTable;
	private static final String ROBOIP = "10.18.85.2";
	
	public NetworkTablesClient(String tableName, boolean client){
		if(client){
			NetworkTable.setClientMode();
			NetworkTable.setIPAddress(ROBOIP);
		}
		netTable = NetworkTable.getTable(tableName);
	}
	
	public JSONObject get(String collection, String id) {
		return getDirect(collection, id);
	}

	public JSONObject getDirect(String collection, String id) {
		String doc = netTable.getString(collection + id, "{}");
		try {
			return new JSONObject(doc);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void postObject(JSONObject object) {
		try {
			netTable.putString(object.getString("collection") + object.getString("id"), object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void pushSchema(Schema scema) {
		System.err.println("this dataclient does not support pushing schemas!");
	}

}
