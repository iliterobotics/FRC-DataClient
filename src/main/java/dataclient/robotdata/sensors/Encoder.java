package dataclient.robotdata.sensors;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

/**
 * Java object to store simple encoder data
 * @author Michael Kelly
 */
public class Encoder extends RobotDataObject{

	/**
	 * sets up the schema which all encoder objects will follow.
	 * <p><ul>
	 * <li>delta_ticks: the number of ticks that passed since the last update
	 * </ul></p>
	 */
	public static final Schema ENCODER_SCHEMA = new Schema("encoder", new SchemaAttribute("delta_ticks", Schema.NUMBER));
		
	private int deltaTicks;
	
	/**
	 * Builds a new encoder object that can push data to a web server client periodically
	 * @param id the identifier for each encoder
	 * @param client the server client that will push the encoder's data
	 * @param TDR the ratio between encoder ticks and whatever unit will be used to measure distance
	 * @param units the name of the units that will be measured on this encoder
	 */
	public Encoder(Object id, DataServerWebClient client) {
		super(ENCODER_SCHEMA, id, client);
		deltaTicks = 0;
	}
	
	/**
	 * @param dticks adds the specified number of ticks to the encoder and calculates the distance traversed
	 */
	public void addTicks(int dticks){
		deltaTicks = dticks;
		updateJSON();
	}
	
	public int getTicks(){
		return deltaTicks;
	}
	
	@Override
	public void update(JSONObject object){
		try {
			addTicks((Integer)object.get("delta_ticks"));
		} catch (JSONException e) {
			System.out.println(object);
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateJSON(){
		set("delta_ticks", deltaTicks);
	}

	@Override
	public void reset() {
		addTicks(-deltaTicks);
	}

}
