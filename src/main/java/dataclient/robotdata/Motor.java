package dataclient.robotdata;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;

public class Motor extends RobotDataObject{
	
	/**Voltage in Volts, Velocity in ticks/s, position in ticks, current in amperes*/
	private static final Schema MOTOR_SCHEMA = new Schema("motor", new SchemaAttribute("voltage", Schema.NUMBER),
																   new SchemaAttribute("velocity", Schema.NUMBER),
																   new SchemaAttribute("position", Schema.NUMBER),
																   new SchemaAttribute("current", Schema.NUMBER));
	private double voltage;
	private double current;
	private int velocity;
	private int position;
	
	public Motor(Object id, DataServerWebClient client) {
		super(MOTOR_SCHEMA, id, client);
	}


	@Override
	public void update(JSONObject object) {
		try {
			setVelocity(object.getInt("velocity"));
			setCurrent(object.getInt("current"));
			setPosition(object.getInt("position"));
			setVoltage(object.getInt("voltage"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void updateJSON() {
		set("velocity", velocity);
		set("position", position);
		set("current", current);
		set("voltage", voltage);
	}

	@Override
	public void reset() {
		setVoltage(0);
		setCurrent(0);
		setPosition(0);
		setVelocity(0);
	}


	public double getVoltage() {
		return voltage;
	}


	public double getCurrent() {
		return current;
	}


	public int getVelocity() {
		return velocity;
	}


	public int getPosition() {
		return position;
	}


	public void setVoltage(double voltage) {
		this.voltage = voltage;
		updateJSON();
	}


	public void setCurrent(double current) {
		this.current = current;
		updateJSON();
	}


	public void setVelocity(int velocity) {
		this.velocity = velocity;
		updateJSON();
	}


	public void setPosition(int position) {
		this.position = position;
		updateJSON();
	}

}
