package test;

import java.net.URL;

import dataclient.DataServerWebClient;
import dataclient.robotdata.arm.ArmStatus;

public class Fbot implements Runnable{
	
	private DataServerWebClient client;
	public static final String URL = "http://localhost:8083";
	private boolean running;
	private MockUtilityArm mockArm;
	
	public void run(){
		running = true;
		try {
			
			client = new DataServerWebClient(new URL(URL));
			
			ArmStatus status = new ArmStatus("test_arm", client);
			mockArm = new MockUtilityArm();
			
			client.pushSchema(ArmStatus.ARM_STATUS_SCHEMA);
			
			//Encoder listeningEncoder = new Encoder(1, client);
			
			//client.watch(listeningEncoder, observable -> {System.out.println(listeningEncoder.getTicks());});
			
			//client.pushSchema(encoder.getSchema());
			
			while(running){
				Thread.sleep(1000);
				mockArm.goTo(Math.random() * 20 -10, Math.random() * 20 - 5, status);
				System.out.println("a:" + status.getAlpha());
				System.out.println("b:" + status.getBeta());
				System.out.println("x:" + status.getDestX());
				System.out.println("y:" + status.getDestY());

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
