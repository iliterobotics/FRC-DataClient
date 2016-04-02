package test.vision;

import java.net.URL;

import test.MockUtilityArm;
import dataclient.DataServerWebClient;
import dataclient.robotdata.arm.ArmStatus;
import dataclient.robotdata.shooter.ShooterStatus;

public class Fbot implements Runnable{
	
	private DataServerWebClient client;
	public static final String URL = "http://localhost:8083";
	private boolean running;
	private MockUtilityArm mockArm;
	
	public void run(){
		running = true;
		try {
			
			client = new DataServerWebClient(new URL(URL));
			
			ArmStatus status = new ArmStatus(client);
			mockArm = new MockUtilityArm();
			
			ShooterStatus sstatus = new ShooterStatus(client);
			
			client.pushSchema(ArmStatus.ARM_STATUS_SCHEMA);
			client.pushSchema(ShooterStatus.SHOOTER_SCHEMA);
			
			//Encoder listeningEncoder = new Encoder(1, client);
			
			//client.watch(listeningEncoder, observable -> {System.out.println(listeningEncoder.getTicks());});
			
			//client.pushSchema(encoder.getSchema());
			
			while(running){
				Thread.sleep(1000);
				double x = Math.random() * 80 - 40;
				double y = Math.random() * 60 - 20;
				mockArm.goTo(x, y, status);
				System.out.println("a:" + status.getAlpha());
				System.out.println("b:" + status.getBeta());
				System.out.println("x:" + status.getDestX());
				System.out.println("y:" + status.getDestY());
				sstatus.setTilt(Math.random() * 90);
				sstatus.setTwist(Math.random() * 90 - 45);
				status.push();
				sstatus.push();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	public static void main(String[] args){
		new Fbot().run();
	}
	
	public void close(){
		running = false;
	}

}
