package dataclient.localDataManagement;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple storage class that holds the Objects that map the different data stru
 * @author Michael
 *
 */
public class Schema {
	
	public static final String NUMBER = "Number", STRING = "String", BOOLEAN = "Boolean", ARRAY = "array";
	
	private final JSONObject SCHEMA_OBJECT;
	private final String NAME;
	
	public Schema(String name){
		this.NAME = name;
		SCHEMA_OBJECT = new JSONObject();
		try {
			SCHEMA_OBJECT.put("id", STRING);
			SCHEMA_OBJECT.put("collection_name", STRING);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Schema(String name, SchemaAttribute ... attribs){
		this(name);
		try{
			for(SchemaAttribute attrib : attribs){
				SCHEMA_OBJECT.put(attrib.key, attrib.value);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	
	}
	
	public boolean matches(JSONObject json){
		Iterator<?> iterator = SCHEMA_OBJECT.keys();
		while(iterator.hasNext()){
			if(!json.has(iterator.next().toString())) return false;
		}
		return true;
	}
	
	public String getName(){
		return NAME;
	}
		
	public JSONObject getJSONObject(){
		return SCHEMA_OBJECT;
	}
	
	@Override
	public boolean equals(Object object){
		return object instanceof Schema && ((Schema)object).NAME.equals(NAME);
	}
	
	@Override
	public int hashCode(){
		return NAME.hashCode();
	}
}
