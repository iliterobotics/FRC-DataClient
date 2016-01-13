package dataclient.localDataManagement;

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
		webClient.pushSchema(schema);
	}
	
}
