package test;

import java.net.URL;

import dataclient.DataServerWebClient;
import dataclient.robotdata.arm.ArmStatus;

public class Fbot implements Runnable{
	
	private DataServerWebClient client;
	public static final String URL = "http://localhost:8083";
	private boolean running;
	
	public void run(){
		running = true;
		try {
			
			client = new DataServerWebClient(new URL(URL));
			
			ArmStatus status = new ArmStatus("test_arm", client);
			
			client.pushSchema(ArmStatus.ARM_STATUS_SCHEMA);
			
			//Encoder listeningEncoder = new Encoder(1, client);
			
			//client.watch(listeningEncoder, observable -> {System.out.println(listeningEncoder.getTicks());});
			
			//client.pushSchema(encoder.getSchema());
			
			while(running){
				Thread.sleep(200);
				status.setDestX(status.getDestX() - 1);
				status.setDestY(status.getDestY() + 1);
				status.setAlpha(status.getAlpha() + 1);
				status.setBeta(status.getBeta() - 1);
				status.push();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void close(){
		running = false;
	}

}
