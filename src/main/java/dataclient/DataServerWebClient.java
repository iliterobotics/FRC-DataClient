package dataclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.events.DataRecievedEvent;
import dataclient.events.DataRecievedEventListener;
import dataclient.localDataManagement.Schema;
import dataclient.robotdata.RobotDataChangeListener;
import dataclient.robotdata.RobotDataObject;

/**
 * Handles HTTP connections to the RDS (Robot Data Server) over a LAN or Internet connection
 * Processes get and push requests of robot data and new schemas you would like to be added to the server
 * 
 * <p>ATTENTION, WE NOW USE EXCLUSIVELY PORT 5807</p>
 * @author Michael
 *
 */
public class DataServerWebClient implements DataRecievedEventListener, DataClient {

	private Map<String, ChangeListener> collectionThreads;
	private Set<Schema> pushedSchemas;
	private String RR_URL;

	public DataServerWebClient(URL url) {
		this(url.toString());
	}
	
	public DataServerWebClient(String url){
		if(url.contains("8083")){
			url = url.replace("8083", "5807");
		}
		this.RR_URL = url;
		pushedSchemas = new HashSet<Schema>();
	}

	public JSONObject get(String collection, String _id) {
		HttpURLConnection defaultConnection;
		try {
			defaultConnection = (HttpURLConnection) (new URL(RR_URL + "/" + collection + "/" + _id).openConnection());
			return get(defaultConnection);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject getDirect(String collection, String _id) {
		HttpURLConnection defaultConnection;
		try {
			defaultConnection = (HttpURLConnection) (new URL(RR_URL + "/direct/" + collection + "/" + _id).openConnection());
			defaultConnection.setConnectTimeout(500);
			return (JSONObject) get(defaultConnection);//.getJSONArray("docs").get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject get(HttpURLConnection httpLink) {
		JSONObject object = null;
		try {
			httpLink.setRequestMethod("GET");

			BufferedReader read = new BufferedReader(new InputStreamReader(httpLink.getInputStream()));
			String line = null;
			StringBuilder builder = new StringBuilder();

			while ((line = read.readLine()) != null){
				builder.append(line);
			}
			read.close();
			object = new JSONObject(builder.toString());

		} catch (IOException | JSONException e) {
			if(!(e instanceof SocketException))e.printStackTrace();
		} finally {
			httpLink.disconnect();
		}
		return object;
	}

//TODO add watching capabilities on entire collections
	
//	public void watch(String collection, RobotDataChangeListener listener) {
//		ChangeListener cConnection = new ChangeListener(this, collection, listener);
//		if (collectionThreads == null) {
//			collectionThreads = new HashMap<String, ChangeListener>();
//		}
//		if (!collectionThreads.containsKey(collection)) {
//			collectionThreads.put(collection, cConnection);
//			cConnection.launch();
//		}
//	}
	
	public boolean watch(RobotDataObject object, RobotDataChangeListener listener){
		ChangeListener cConnection = new ChangeListener(this, object.getCollection(), object.getID(), object, listener);
		if(collectionThreads == null){
			collectionThreads = new HashMap<String, ChangeListener>();
		}
		if(!collectionThreads.containsKey(object.getCollection() + object.getID())){
			collectionThreads.put(object.getCollection() + object.getID(), cConnection);
			cConnection.launch();
			return true;
		}
		return false;
	}

	/**
	 * Takes a JSON object and posts it to the RoboWebServer url specified by this client
	 * @param object the JSONObject to be posted MUST include the 'collection' and 'id' attributes
	 * @throws IOException 
	 */
	public void postObject(JSONObject object){
		try {
			post(object, "/" + object.getString("collection_name") + "/" + object.get("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void post(JSONObject object, String uri){
		HttpURLConnection httpLink = null;
		try {
			httpLink = (HttpURLConnection) new URL(RR_URL.toString() + uri).openConnection();
			httpLink.setRequestMethod("POST");
			httpLink.setDoOutput(true);
			httpLink.setDoInput(true);
			httpLink.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpLink.setRequestProperty("Accept", "application/json");

			OutputStreamWriter write = new OutputStreamWriter(httpLink.getOutputStream());
			write.write("{ \"timestamp\" : \"" + new Timestamp(System.currentTimeMillis()) + "\", \"robotData\":" + object + "}");
			write.flush();

			BufferedReader read = new BufferedReader(new InputStreamReader(httpLink.getInputStream()));
			String line = null;
			StringBuilder builder = new StringBuilder();

			while ((line = read.readLine()) != null)
				builder.append(line);

			System.out.println(builder.toString());
			read.close();
			write.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpLink != null) {
				httpLink.disconnect();
				httpLink = null;
			}
		}
	}
	
	public void pushSchema(Schema schema){
		if(!hasPushedSchema(schema)){
			post(schema.getJSONObject(), "/add_schema/" + schema.getName());
			pushedSchemas.add(schema);
		}
	}
	
	public boolean hasPushedSchema(Schema schema){
		return pushedSchemas.contains(schema);
	}

	public String getURL() {
		return RR_URL;
	}

	@Override
	public void fire(DataRecievedEvent event) {

	}

	public void close() {
		for (Entry<String, ChangeListener> entry : collectionThreads.entrySet()) {
			entry.getValue().end();
		}
	}
	
	public void resetWatchers(){
		for(Entry<String, ChangeListener> entry : collectionThreads.entrySet()){
			entry.getValue().restart();
		}
	}
	
	public void setURL(String url){
		RR_URL = url;
	}
}
