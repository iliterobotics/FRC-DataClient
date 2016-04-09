package dataclient;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.localDataManagement.Schema;
import dataclient.robotdata.RobotDataChangeListener;
import dataclient.robotdata.RobotDataObject;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

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
			netTable.putString(object.getString("collection_name") + object.getString("id"), object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean watch(RobotDataObject object, RobotDataChangeListener listener){
		netTable.addTableListener(object.getCollection() + object.getID(), new ITableListener() {
			public void valueChanged(ITable table, String key, Object theirObject, boolean bool) {
				try {
					object.update(new JSONObject(table.getString(key, "{}")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listener.fire(object);
			}
		}, true);
		return true;
	}
		
	public void pushSchema(Schema scema) {
//		System.out.println("this dataclient does not support pushing schemas!");
	}

}
