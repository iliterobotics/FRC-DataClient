package dataclient.localDataManagement;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dataclient.DataServerWebClient;

public class RoboDatabase {
	
	private List<Schema> schemas;
	private DataServerWebClient webClient;
	
	public RoboDatabase(URL url){
		webClient = new DataServerWebClient(url);
		schemas = new ArrayList<Schema>();
	}
	
	public void addSchema(Schema schema){
		schemas.add(schema);
		try {
			webClient.pushSchema(schema);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
