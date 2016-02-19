package dataclient.robotdata;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.localDataManagement.Schema;

public abstract class RobotDataObject {
	private final String ID;
	private final String COLLECTION;
	private Schema schema;
	private JSONObject object;
	private DataServerWebClient client;
	
	public RobotDataObject(String collection, Schema schema, Object id, DataServerWebClient client){
		COLLECTION = collection;
		ID = id.toString();
		this.client = client;
		this.schema = schema;
		
		object = new JSONObject();
		Iterator<?> keys = schema.getJSONObject().keys();
		while(keys.hasNext()){
			try {
				object.put(keys.next().toString(), JSONObject.NULL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		set("collection_name", collection);
		set("id", id.toString());
		
		reset();
	}
	
	public RobotDataObject(Schema schema, Object id, DataServerWebClient client){
		this(schema.getName(), schema, id, client);
	}
	
	public String getID(){
		return ID;
	}
	
	public String getCollection(){
		return COLLECTION;
	}
	
	public JSONObject getJSON(){
		return object;
	}
	
	public Schema getSchema(){
		return schema;
	}
	
	protected void set(String key, Object value){
		try {
			object.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Object get(String key){
		try{
			if(object.has(key)){
				return object.get(key);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void push(){
		client.postObject(object);
	}
	
	public abstract void update(JSONObject object);
	public abstract void updateJSON();
	public abstract void reset();
	
	@Override
	public boolean equals(Object other){
		return other instanceof RobotDataObject && (((RobotDataObject)other).schema + ((RobotDataObject)other).ID).equals(schema + ID);
	}
	
	@Override
	public int hashCode(){
		return (schema + ID).hashCode();
	}
}