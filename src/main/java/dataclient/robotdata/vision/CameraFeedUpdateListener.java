package dataclient.robotdata.vision;

import java.awt.image.BufferedImage;

public interface CameraFeedUpdateListener {
	
	void onNextFrame(BufferedImage nextFrame);

}
