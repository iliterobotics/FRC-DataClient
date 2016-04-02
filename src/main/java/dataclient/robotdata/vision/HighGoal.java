package dataclient.robotdata.vision;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class HighGoal extends RobotDataObject{

	private static final Schema HIGHGOAL_SCHEMA = new Schema("high_goal", new SchemaAttribute("distance", Schema.NUMBER), new SchemaAttribute(
			"angle_of_elevation", Schema.NUMBER), new SchemaAttribute("alignment", Schema.STRING), new SchemaAttribute("goal_found", Schema.BOOLEAN));
	private double distance;
	private double angleOfElevation;
	private String alignment;
	private boolean isGoalFound;
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
			if (object.has("goal_found")) {
				isGoalFound = object.getBoolean("goal_found");
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
	
	public boolean isGoalFound(){
		return isGoalFound;
	}
	
	public double getAzimuth(){
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
	
	public void setIsGoalFound(boolean found){
		isGoalFound = found;
		updateJSON();
	}

	@Override
	public void updateJSON() {
		set("distance", distance);
		set("angle_of_elevation", angleOfElevation);
		set("alignment", alignment);
		set("goal_found", isGoalFound);
	}

	@Override
	public void reset() {
		setDistance(0.0);
		setAngleOfElevation(0.0);
		setAlignment(null);
		setIsGoalFound(false);
	}

}
