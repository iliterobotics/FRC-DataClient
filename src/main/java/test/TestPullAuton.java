package test;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import dataclient.DataServerWebClient;
import dataclient.robotdata.autonomous.AutonomousConfig;

public class TestPullAuton {
	
	public static void main(String[] args){
		try {
			DataServerWebClient client = new DataServerWebClient(new URL("http://localhost:8083"));
			AutonomousConfig auton = new AutonomousConfig(client, 0, 0, 0, 0);
			client.pushSchema(AutonomousConfig.AUTO_CONFIG_SCHEMA);
			auton.push();
			
			System.out.println((JSONObject) client.getDirect(auton.getCollection(), auton.getID()));
			auton.update((JSONObject) client.getDirect(auton.getCollection(), auton.getID()));
			
			System.out.println(auton.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
