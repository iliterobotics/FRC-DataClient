package dataclient.robotdata.vision;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class HighGoal extends RobotDataObject{

	private static final Schema HIGHGOAL_SCHEMA = new Schema("high_goal", new SchemaAttribute("distance", Schema.NUMBER), new SchemaAttribute(
			"angle_of_elevation_X", Schema.NUMBER),new SchemaAttribute(
					"angle_of_elevation_Y", Schema.NUMBER), new SchemaAttribute("alignment", Schema.STRING), new SchemaAttribute("goal_found", Schema.BOOLEAN));
	private double distance;
	private double angleOfElevationX;
	private double angleOfElevationY;
	private String alignmentX;
	private String alignmentY;
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
			if (object.has("angle_of_elevation_X"))
				angleOfElevationX = object.getDouble("angle_of_elevation_X");
			
			if(object.has("angle_of_elevation_Y")) {
				angleOfElevationY = object.getDouble("angle_of_elevation_Y");

			}
			if (object.has("alignmentX")) {
				alignmentX = object.getString("alignmentX");
			}
			if(object.has("alignmentY")) {
				alignmentY = object.getString("alignmentY");
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
	
	public String getXAlignment(){
		return alignmentX;
	}
	
	public String getYAlignment() {
		return alignmentY;
	}
	
	public boolean isGoalFound(){
		return isGoalFound;
	}
	
	public double getAzimuthX(){
		return angleOfElevationX;
	}
	
	public double getAzimuthY() {
		return angleOfElevationY;
	}
	
	public void setDistance(double dist){
		this.distance = dist;
		updateJSON();
	}
	
	public void setAngleOfElevationX(double aoe){
		this.angleOfElevationX = aoe;
		updateJSON();
	}
	
	public void setAngleOfElevationY(double aoe){
		this.angleOfElevationY = aoe;
		updateJSON();
	}
	
	public void setXAlignment(String alignmentX){
		this.alignmentX = alignmentX;
		updateJSON();
	}
	
	public void setYAlignment(String aligmentY) {
		this.alignmentY = aligmentY;
		updateJSON();
	}
	
	public void setIsGoalFound(boolean found){
		isGoalFound = found;
		updateJSON();
	}

	@Override
	public void updateJSON() {
		set("distance", distance);
		set("angle_of_elevation_X", angleOfElevationX);
		set("angle_of_elevation_Y", angleOfElevationY);
		set("alignmentX", alignmentX);
		set("alignmentY", alignmentY);
		set("goal_found", isGoalFound);
	}

	@Override
	public void reset() {
		setDistance(0.0);
		setAngleOfElevationX(0.0);
		setAngleOfElevationY(0.0);
		setXAlignment(null);
		setYAlignment(null);
		setIsGoalFound(false);
	}

}
