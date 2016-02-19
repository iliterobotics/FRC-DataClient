package dataclient.robotdata.vision;

import org.ilite.vision.camera.CameraConnectionFactory;
import org.ilite.vision.camera.ICameraConnection;
import org.ilite.vision.camera.opencv.OpenCVUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.fauge.robotics.towertracker.ITowerListener;
import com.fauge.robotics.towertracker.TowerMessage;
import com.fauge.robotics.towertracker.TowerTracker1885;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class HighGoal extends RobotDataObject implements ITowerListener{

	private static final Schema HIGHGOAL_SCHEMA = new Schema("high_goal", new SchemaAttribute("distance_units", Schema.STRING), new SchemaAttribute("distance", Schema.NUMBER), new SchemaAttribute(
			"angle_of_elevation", Schema.NUMBER), new SchemaAttribute("quadrant", Schema.NUMBER));
	private double distance;
	private String units;
	private double angleOfElevation;
	private int quadrant;
	public static final int I = 1, II = 2, III = 3, IV = 4;

	public HighGoal(Object id, DataServerWebClient client) {
		super(HIGHGOAL_SCHEMA, id, client);
	}

	@Override
	public void update(JSONObject object) {
		try {
			if (object.has("distance_units"))
				units = object.get("distance_units").toString();
			if (object.has("distance"))
				distance = object.getDouble("distance");
			if (object.has("angle_of_elevation"))
				angleOfElevation = object.getDouble("angle_of_elevation");
			if (object.has("quadrant")) {
				int newQuad = object.getInt("quadrant");
				if (newQuad >= I && newQuad <= IV) {
					quadrant = newQuad;
				} else {
					// TODO when invalid quadrant is given
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setDistance(double dist){
		this.distance = dist;
		updateJSON();
	}
	
	public void setUnits(String units){
		this.units = units;
		updateJSON();
	}
	
	public void setAngleOfElevation(double aoe){
		this.angleOfElevation = aoe;
		updateJSON();
	}
	
	public void setQuadrant(int quadrant){
		if(quadrant >= I && quadrant <= IV){
			this.quadrant = quadrant;
		}
		updateJSON();
	}

	@Override
	public void updateJSON() {
		set("distance_units", units);
		set("units", distance);
		set("angle_of_elevation", angleOfElevation);
		set("quadrant", quadrant);
	}

	@Override
	public void reset() {
		setQuadrant(I);
		setDistance(0.0);
		setAngleOfElevation(0.0);
		setUnits("u");
	}

	@Override
	public void fire(TowerMessage message) {
		distance = message.distance;
		angleOfElevation = message.AoE;
		updateJSON();
	}
	
	public static void main(String[] args){
		OpenCVUtils.init();
		ICameraConnection cameraConnection = CameraConnectionFactory.getCameraConnection(null);
		TowerTracker1885 aTracker = new TowerTracker1885(cameraConnection);
		aTracker.addTowerListener(new HighGoal(aTracker, null));
		aTracker.start();
	}

}
