package dataclient.robotdata.shooter;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class FireStatus extends RobotDataObject{
	
	private static final Schema FIRE_SCHEMA = new Schema("fire_stat", new SchemaAttribute("aim_stat", Schema.NUMBER));
	
	public static final int OFF = 0, ON = 1, AIMED = 42;
	
	private static final String id = "0";
	private int status;
	
	public FireStatus(DataClient client){
		super(FIRE_SCHEMA, id, client);
		setAimStatus(OFF);
	}
	@Override
	public void update(JSONObject object) {
		try {
			setAimStatus(object.getInt("aim_stat"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public int getStatus(){
		return status;
	}
	@Override
	public void updateJSON() {
		set("aim_stat", status);
	}
	@Override
	public void reset() {
		setAimStatus(OFF);
	}
	
	public void setAimStatus(int status){
		this.status = status;
		updateJSON();
	}
}
