package dataclient.robotdata.autonomous;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class AutonomousConfig extends RobotDataObject{
	public static final Schema AUTO_CONFIG_SCHEMA = new Schema("autonomouscfg", new SchemaAttribute("position", Schema.NUMBER),
																				new SchemaAttribute("defense", Schema.NUMBER),
																				new SchemaAttribute("goal", Schema.STRING),
																				new SchemaAttribute("delay_millis", Schema.NUMBER));
	//GOALS
	public static final int HIGH_LEFT_GOAL = 0,
							 HIGH_CENTER_GOAL = 1,
							 HIGH_RIGHT_GOAL = 2,
							 LOW_LEFT_GOAL = 3,
							 LOW_RIGHT_GOAL = 4;
	//DEFENSES
	public static final int PORTCULLIS = 0,
							CHEVAL_DE_FRISE = 1,
							MOAT = 2,
							RAMPARTS = 3,
							DRAWBRIDGE = 4,
							SALLYPORT = 5,
							ROCK_WALL = 6,
							ROUGH_TERRAIN = 7,
							LOW_BAR = 8;
	
	
	
	private int position;
	private int defense;
	private int goal;
	private int delay;
	
	public AutonomousConfig(DataServerWebClient client, int position, int defense, int goal, int delay){
		super(AUTO_CONFIG_SCHEMA, "0", client);
		setPosition(position);
		setDefense(defense);
		setGoal(goal);
		setDelay(delay);
	}

	@Override
	public void update(JSONObject object) {
		try {
			setPosition(object.getInt("position"));
			setDefense(object.getInt("defense"));
			setGoal(object.getInt("goal"));
			setDelay(object.getInt("delay"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateJSON() {
		try {
			getJSON().put("position", position);
			getJSON().put("defense", defense);
			getJSON().put("goal", goal);
			getJSON().put("delay", delay);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		setPosition(0);
	}
	
	public void setPosition(int pos){
		if(pos < 0 || pos > 5){
			throw new IllegalArgumentException("POSITION IS INVALID:" + pos);
		}
		position = pos;
		updateJSON();
	}

	public void setDefense(int def){
		if(def < PORTCULLIS || def > LOW_BAR){
			throw new IllegalArgumentException("DEFENSE# IS INVALID:" + def);
		}
		defense = def;
		updateJSON();
	}

	public void setGoal(int g){
		if(g < HIGH_LEFT_GOAL || g > LOW_RIGHT_GOAL){
			throw new IllegalArgumentException("goal# IS INVALID:" + g);	
		}
		goal = g;
		updateJSON();
	}
	
	public void setDelay(int d){
		if(d < 0){
			d = 0;
		}
		delay = d;
		updateJSON();
	}

	public int getPosition(){
		return position;
	}
	
	public int getDefense(){
		return defense;
	}
	
	public int getGoal(){
		return goal;
	}
	
	public int getDelay(){
		return delay;
	}
	
}
