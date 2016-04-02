package dataclient.robotdata.vision;

import java.awt.image.BufferedImage;

public class ImagePusher implements Runnable{

	private CameraFeedDatabase db;
	private BufferedImage frame;
	
	public ImagePusher(CameraFeedDatabase database, BufferedImage frame){
		this.db = database;
		this.frame = frame;
	}
	
	@Override
	public void run() {
		db.pushFrame(frame);
	}

}
