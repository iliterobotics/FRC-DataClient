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
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.events.DataRecievedEvent;
import dataclient.events.DataRecievedEventListener;
import dataclient.localDataManagement.Schema;

/**
 * Handles HTTP connections to the RDS (Robot Data Server) over a LAN or Internet connection
 * Processes get and push requests of robot data and new schemas you would like to be added to the server
 * 
 * @author Michael
 *
 */
public class DataServerWebClient implements DataRecievedEventListener {

	private Map<String, ChangeListener> collectionThreads;
	private final URL RR_URL;

	public DataServerWebClient(URL url) {
		this.RR_URL = url;
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

	public void watch(String collection) {
		ChangeListener cConnection = new ChangeListener(this, collection);
		if (collectionThreads == null) {
			collectionThreads = new HashMap<String, ChangeListener>();
		}
		if (!collectionThreads.containsKey(collection)) {
			collectionThreads.put(collection, cConnection);
			cConnection.launch();
		}
	}

	public void post(JSONObject object, String uri) {
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
	
	public void post(JSONObject object){
		post(object, "");
	}
	
	public void pushSchema(Schema schema){
		post(schema.getJSONObject(), "/add_schema/" + schema.getName());
	}

	public URL getURL() {
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
}
