package dataclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import dataclient.events.DataRecievedEvent;

public class ChangeListener implements Runnable {

	HttpURLConnection connection;
	DataServerWebClient client;
	String collection;
	Thread thread;

	public ChangeListener(DataServerWebClient client, String collection) {
		this.client = client;
		this.collection = collection;
		thread = new Thread(this);
	}

	public void launch() {
		thread.start();
	}

	public void run() {
		try {
			while (true) {
				connection = (HttpURLConnection)(new URL(client.getURL() + "/" + collection).openConnection());
				JSONObject jsonObject = client.get(connection);
				client.fire(new DataRecievedEvent(collection, jsonObject));
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

}
