package dataclient;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.localDataManagement.Schema;

/**
 * Test class for development of the RWS
 * @author Michael Kelly
 *
 */
public class DataServerClientTest {
	public static void main(String[] args){
		try {
			DataServerWebClient client = new DataServerWebClient(new URL("http://169.254.200.130:8083"));

			Schema schema = new Schema("robit");
			schema.add("Name", Schema.NUMBER);
			schema.add("Id", Schema.STRING);
			client.pushSchema(schema);
			
			client.watch("test");
			client.watch("robit");
			
			Thread.sleep(1000);
			
			DataServerWebClient postclient = new DataServerWebClient(new URL("http://169.254.200.130:8083/test"));
			postclient.post(new JSONObject("{\"name\" = \"bob\", \"count\" = 42, \"is\" = true }"));
			postclient.post(new JSONObject("{\"name\" = \"daniel\", \"count\" = 99, \"is\" = false }"));
			postclient.post(new JSONObject("{\"name\" = \"chris\", \"count\" = 9001, \"is\" = true }"));
			
			DataServerWebClient postclienter = new DataServerWebClient(new URL("http://169.254.200.130:8083/robit"));
			postclienter.post(new JSONObject("{\"Name\" = 20, \"Id\" = \"red\"}"));
			postclienter.post(new JSONObject("{\"Name\" = 42, \"Id\" = \"green\"}"));
			
			Thread.sleep(5000);
			client.close();
			
		} catch (MalformedURLException | JSONException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
