package dataclient.robotdata.arm;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

public class ArmStatus extends RobotDataObject{

	private static final double DEF_ALPHA = 0;
	private static final double DEF_BETA = 170;
	private static final double DEF_X = 0;
	private static final double DEF_Y = Math.sin(DEF_BETA) * 18;
	public static final Schema ARM_STATUS_SCHEMA = new Schema("arm_status", new SchemaAttribute("arm_alpha_angle", Schema.NUMBER),
																			 new SchemaAttribute("arm_beta_angle", Schema.NUMBER),
																			 new SchemaAttribute("arm_destination_x", Schema.NUMBER),
																			 new SchemaAttribute("arm_destination_y", Schema.NUMBER));
	private double alpha;
	private double beta;
	private double destX;
	private double destY;
	
	public ArmStatus(DataServerWebClient client) {
		super(ARM_STATUS_SCHEMA.getName(), ARM_STATUS_SCHEMA, "arm", client);
	}
	
	@Override
	public void update(JSONObject object) {
		try {
			setAlpha(object.getDouble("arm_alpha_angle"));
			setBeta(object.getDouble("arm_beta_angle"));
			setDestX(object.getDouble("arm_destination_x"));
			setDestY(object.getDouble("arm_destination_y"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateJSON() {	
		set("arm_alpha_angle", alpha);
		set("arm_beta_angle", beta);
		set("arm_destination_x", destX);
		set("arm_destination_y", destY);
	}

	@Override
	public void reset() {
		setAlpha(DEF_ALPHA);
		setBeta(DEF_BETA);
		setDestX(DEF_X);
		setDestY(DEF_Y);
	}

	public double getAlpha() {
		return alpha;
	}
	
	public double getBeta() {
		return beta;
	}
	
	public double getDestX() {
		return destX;
	}
	
	public double getDestY() {
		return destY;
	}

	public void setDestY(double destY) {
		this.destY = destY;
		updateJSON();
	}
	
	public void setAlpha(double alpha) {
		this.alpha = alpha;
		updateJSON();
	}
	
	public void setBeta(double beta) {
		this.beta = beta;
		updateJSON();
	}
	
	public void setDestX(double destX) {
		this.destX = destX;
		updateJSON();
	}

}
