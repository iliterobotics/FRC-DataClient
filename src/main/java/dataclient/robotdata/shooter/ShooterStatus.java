package dataclient.robotdata.shooter;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class ShooterStatus extends RobotDataObject{

	public static final Schema SHOOTER_SCHEMA = new Schema("shooter", new SchemaAttribute("tilt", Schema.NUMBER),
																	  new SchemaAttribute("twist", Schema.NUMBER));
	
	private double tilt, twist;
	
	public ShooterStatus(DataServerWebClient client) {
		super(SHOOTER_SCHEMA, "mainShooter", client);
	}

	@Override
	public void update(JSONObject object) {
		try{
			setTilt(object.getDouble("tilt"));
			setTwist(object.getDouble("twist"));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

	@Override
	public void updateJSON() {
		set("tilt", tilt);
		set("twist", twist);
	}

	@Override
	public void reset() {
		setTilt(0);
		setTwist(0);
	}
	
	public void setTilt(double theta){
		tilt = theta;
		set("tilt", theta);
	}
	
	public void setTwist(double theta){
		twist = theta;
		set("twist", theta);
	}
	
	public double getTilt(){
		return tilt;
	}
	
	public double getTwist(){
		return twist;
	}
	

}
