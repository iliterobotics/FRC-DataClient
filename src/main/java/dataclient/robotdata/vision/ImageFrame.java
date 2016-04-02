package dataclient.robotdata.vision;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;
import dataclient.localDataManagement.SchemaAttribute;
import dataclient.robotdata.RobotDataObject;

/**
 * @deprecated This class was an old attempt to serialize and store image data. 
 * @see {@link CameraFeedDatabase.java}
 */
public class ImageFrame extends RobotDataObject{
	
	public static final Schema IMAGE_SCHEMA = new Schema("image_frame", new SchemaAttribute("byte_array", Schema.ARRAY));
	
	private byte[] bytes;
	
	public ImageFrame(Object id, DataServerWebClient client) {
		super(IMAGE_SCHEMA, id, client);
	}
	
	@Override
	public void update(JSONObject object) {
		JSONArray array = null;
		try {
			array = object.getJSONArray("byte_array");
			byte[] bytes = new byte[array.length()];
			if(array != null){
				for(int i = 0; i < array.length(); i++){
					bytes[i] = (byte)array.getInt(i);
				}
			}
			setImage(bytes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage toBufferedImage(){
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			return ImageIO.read(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setImage(byte[] bytes){
		this.bytes = bytes;
		updateJSON();
	}

	@Override
	public void updateJSON() {
		try {
			getJSON().put("byte_array", bytes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		byte[] array = {};
		setImage(array);
	}
	
}
