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

	private boolean isRunning;

	public ChangeListener(DataServerWebClient client, String collection, Object ID, RobotDataObject object,
			RobotDataChangeListener listener) {
		this.client = client;
		this.collection = collection;
		this.ID = ID;
		this.listener = listener;
		this.object = object;
		this.isRunning = false;
		thread = new Thread(this, collection + "/" + ID);
	}

	public void launch() {
		this.isRunning = true;
		thread.start();
	}

	public void run() {
		while (isRunning) {
			try {
				connection = (HttpURLConnection) (new URL(client.getURL() + "/" + collection + "/" + ID))
						.openConnection();
				JSONObject jsonObject = client.get(connection);
				object.update((JSONObject) jsonObject.getJSONArray("docs").get(0));
				listener.fire(object);

			} catch (Exception e) {
				System.err.println("[ChangeListener]Could not connect; error " + e.getMessage());
//				this.isRunning = false;
//				thread.interrupt();
			}
		}

	}
	
	public boolean isRunning() {
		return this.isRunning;
	}

	public void end() {
		System.out.println("Thread ended for " + collection + " watcher");
		this.isRunning = false;
		if (connection != null)
			connection.disconnect();
		thread.interrupt();
	}

	public void restart() {
		end();
		launch();
	}

}
