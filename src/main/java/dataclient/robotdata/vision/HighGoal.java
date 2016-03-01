package dataclient.robotdata.vision;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fauge.robotics.towertracker.ITowerListener;
import com.fauge.robotics.towertracker.TowerMessage;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class HighGoal extends RobotDataObject{

	private static final Schema HIGHGOAL_SCHEMA = new Schema("high_goal", new SchemaAttribute("distance", Schema.NUMBER), new SchemaAttribute(
			"angle_of_elevation", Schema.NUMBER), new SchemaAttribute("alignment", Schema.STRING));
	private double distance;
	private double angleOfElevation;
	private String alignment;
	public static final int I = 1, II = 2, III = 3, IV = 4;
	public static final String DEF_ID = "1";
	
	public HighGoal(DataServerWebClient client) {
		super(HIGHGOAL_SCHEMA, DEF_ID, client);
	}

	@Override
	public void update(JSONObject object) {
		try {
			if (object.has("distance"))
				distance = object.getDouble("distance");
			if (object.has("angle_of_elevation"))
				angleOfElevation = object.getDouble("angle_of_elevation");
			if (object.has("alignment")) {
				alignment = object.getString("alignment");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public double getDistance(){
		return distance;
	}
	
	public String getAlignment(){
		return alignment;
	}
	
	public double getAngleOfElevation(){
		return angleOfElevation;
	}
	
	public void setDistance(double dist){
		this.distance = dist;
		updateJSON();
	}
	
	public void setAngleOfElevation(double aoe){
		this.angleOfElevation = aoe;
		updateJSON();
	}
	
	public void setAlignment(String alignment){
		this.alignment = alignment;
		updateJSON();
	}

	@Override
	public void updateJSON() {
		set("distance", distance);
		set("angle_of_elevation", angleOfElevation);
		set("alignment", alignment);
	}

	@Override
	public void reset() {
		setDistance(0.0);
		setAngleOfElevation(0.0);
		setAlignment(null);
	}

}
