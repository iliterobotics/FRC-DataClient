package dataclient;

import org.json.JSONObject;

import dataclient.localDataManagement.Schema;
import dataclient.robotdata.RobotDataChangeListener;
import dataclient.robotdata.RobotDataObject;

public interface DataClient {
	
	JSONObject get(String collection, String id);
	JSONObject getDirect(String collection, String id);
	void postObject(JSONObject object);
	void pushSchema(Schema scema);
	boolean watch(RobotDataObject object, RobotDataChangeListener listener);

}
