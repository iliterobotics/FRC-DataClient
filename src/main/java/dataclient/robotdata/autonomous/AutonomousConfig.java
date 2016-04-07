package dataclient.robotdata.autonomous;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataClient;
import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class AutonomousConfig extends RobotDataObject{
	public static final Schema AUTO_CONFIG_SCHEMA = new Schema("autonomouscfg", new SchemaAttribute("position", Schema.NUMBER),
																				new SchemaAttribute("defense", Schema.NUMBER),
																				new SchemaAttribute("goal", Schema.STRING),
																				new SchemaAttribute("delay_millis", Schema.NUMBER),
																				new SchemaAttribute("doing_nothing", Schema.BOOLEAN),
																				new SchemaAttribute("is_shooting", Schema.BOOLEAN),
																				new SchemaAttribute("returns", Schema.BOOLEAN));
	//GOALS
	public static final int HIGH_LEFT_GOAL = 0,
							HIGH_CENTER_GOAL = 1,
							HIGH_RIGHT_GOAL = 2,
							LOW_LEFT_GOAL = 3,
							LOW_RIGHT_GOAL = 4,
							NONE = 5;
	
	public static final int LEFT = -1,
							CENTER = 0,
							RIGHT = 1;
	public static final boolean UP = true, DOWN = false;
	//DEFENSES
	public static final int PORTCULLIS = 0,
							CHEVAL_DE_FRISE = 1,
							MOAT = 2,
							RAMPARTS = 3,
							DRAWBRIDGE = 4,
							SALLYPORT = 5,
							ROCK_WALL = 6,
							ROUGH_TERRAIN = 7,
							LOW_BAR = 8,
							NOTHING = 9;
	
	private int position;
	private int defense;
	private int goal;
	private int delay;
	private boolean doingNothing;
	private boolean isShooting;
	private boolean returns;
	private DataClient client;
	
	public AutonomousConfig(DataClient client, int position, int defense, int goal, int delay){
		super(AUTO_CONFIG_SCHEMA, "0", client);
		
		this.client = client;
		
		setPosition(position);
		setDefense(defense);
		setGoal(goal);
		setDelay(delay);
		setDoingNothing(true);
		setShooting(false);
		setReturns(false);
	}
	
	public AutonomousConfig(DataClient client){
		this(client, 1, LOW_BAR, HIGH_CENTER_GOAL, 0);
	}
	
	public void pull(){
		try {
			update(client.getDirect(getCollection(), getID()).getJSONArray("docs").getJSONObject(0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(JSONObject object) {
		try {
			setPosition(object.getInt("position"));
			setDefense(object.getInt("defense"));
			setGoal(object.getInt("goal"));
			setDelay(object.getInt("delay_millis"));
			setDoingNothing(object.getBoolean("doing_nothing"));
			setShooting(object.getBoolean("is_shooting"));
			setReturns(object.getBoolean("returns"));
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
			getJSON().put("delay_millis", delay);
			getJSON().put("doing_nothing", doingNothing);
			getJSON().put("is_shooting", isShooting);
			getJSON().put("returns", returns);
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
		if(def < PORTCULLIS || def > NOTHING){
			throw new IllegalArgumentException("DEFENSE# IS INVALID:" + def);
		}
		System.out.println(def);
		defense = def;
		updateJSON();
	}

	public void setGoal(int g){
		if(g < HIGH_LEFT_GOAL || g > NONE){
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
	
	public void setDoingNothing(boolean does){
		doingNothing = does;
		updateJSON();
	}
	
	public void setShooting(boolean shooting){
		isShooting = shooting;
		updateJSON();
	}
	
	public void setReturns(boolean returns){
		this.returns = returns;
		updateJSON();
	}
	
	public static String getGoalName(int defense){
		switch(defense){
			case 0:
				return "High Left";
			case 1:
				return "High Center";
			case 2:
				return "High Right";
			case 3:
				return "Low Left";
			case 4:
				return "Low Right";
			case 5:
				return "Don't Shoot";
		}
		return "NAG";
	}
	
	public static Map<String, Integer> getGoalNameMap(){
		Map<String, Integer> goalNameMap = new TreeMap<String, Integer>();
		for(int i = 0; i <= NONE; i++){
			goalNameMap.put(getGoalName(i), i);
		}
		return goalNameMap;
	}
	
	public static Map<String, Integer> getDefenseNameMap(){
		Map<String, Integer> defenseNameMap = new TreeMap<String, Integer>();
		for(int i = 0; i <= NOTHING; i++){
			defenseNameMap.put(getDefenseName(i), i);
		}
		return defenseNameMap;
	}
	
	public static String getDefenseName(int defense){
		switch(defense){
			case 0:
				return "Portcullis";
			case 1:
				return "Cheval de Frise";
			case 2:
				return "Moat";
			case 3:
				return "Ramparts";
			case 4:
				return "Drawbridge";
			case 5:
				return "Sally Port";
			case 6:
				return "Rock Wall";
			case 7:
				return "Rough Terrain";
			case 8:
				return "Low Bar";
			case 9:
				return "Do Nothing";
		}
		return "NAD";
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
	
	public boolean doesNothing(){
		return doingNothing;
	}
	
	public boolean getGoalElevation(){
		return goal < LOW_LEFT_GOAL;
	}
	
	public boolean returns(){
		return returns;
	}
	
	public int getGoalPosition(){
		switch(goal){
			case 0:
			case 3:
				return LEFT;
			case 1: 
				return CENTER;
		}
		return RIGHT;
	}
	
	public boolean isShooting(){
		return isShooting;
	}
	
}
