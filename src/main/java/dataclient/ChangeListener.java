package dataclient;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import dataclient.robotdata.RobotDataChangeListener;
import dataclient.robotdata.RobotDataObject;

public class ChangeListener implements Runnable {

	private HttpURLConnection connection;
	private DataServerWebClient client;
	private String collection;
	private Thread thread;
	private Object ID;
	private RobotDataChangeListener listener;
	private RobotDataObject object;

	public ChangeListener(DataServerWebClient client, String collection, Object ID, RobotDataObject object, RobotDataChangeListener listener){
		this.client = client;
		this.collection = collection;
		this.ID = ID;
		this.listener = listener;
		this.object = object;
		thread = new Thread(this, collection + "/" + ID);
	}

	public void launch() {
		thread.start();
	}

	public void run() {
		try {
			while (true) {
				connection = (HttpURLConnection)(new URL(client.getURL() + "/" + collection + "/" + ID)).openConnection();
				JSONObject jsonObject = client.get(connection);
				object.update((JSONObject) jsonObject.getJSONArray("docs").get(0));
				listener.fire(object);
			}
		} catch (Exception e) {
			System.out.println("Exception caught");
			thread.interrupt();
		}
	}

	public void end() {
		System.out.println("Thread ended for " + collection + " watcher");
		if(connection != null)connection.disconnect();
		thread.interrupt();
	}
	
	public void restart(){
		end();
		launch();
	}

}
