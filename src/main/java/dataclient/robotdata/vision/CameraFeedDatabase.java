package dataclient.robotdata.vision;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.ilite.vision.api.messages.RobotVisionMsg;
import org.ilite.vision.api.system.VisionListener;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

/**
 * class for taking in new frames from the camera when 
 * @author Michael
 */
public class CameraFeedDatabase implements VisionListener{
	
	/** In bytes */
	private static final int CHUNK_SIZE = 200000;
	private final GridFSUploadOptions UPLOAD_OPTIONS;
	
	private final MongoClient mongoClient;
	private final MongoDatabase mongodb;
	private final GridFSBucket bucket;
	
	private BufferedImage mostRecentFrame;
	private List<CameraFeedUpdateListener> updateListeners;
	private final String session;
	private int frame;
	
	public CameraFeedDatabase(String mongodbURI, String dbname, String session){
		this.session = session;
		frame = 0;
		
		mongoClient = new MongoClient(mongodbURI);
		mongodb = mongoClient.getDatabase(dbname);
		bucket = GridFSBuckets.create(mongodb, session);
		
		updateListeners = new ArrayList<CameraFeedUpdateListener>();
		
		UPLOAD_OPTIONS = new GridFSUploadOptions();
		UPLOAD_OPTIONS.chunkSizeBytes(CHUNK_SIZE);
	}
	
	public void onVisionDataRecieved(RobotVisionMsg message) {
		pushFrame(message.getRawImage());
	}
	
	public void pushFrame(BufferedImage image){
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", byteStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		byte[] bytes = byteStream.toByteArray();
		
		GridFSUploadStream uploadStream = bucket.openUploadStream(session + frame, UPLOAD_OPTIONS);
		uploadStream.write(bytes);
		uploadStream.close();
		
		frame++;
		mostRecentFrame = image;
	}
	
	
	public BufferedImage pullFrame(int n){
		GridFSDownloadStream downloadStream = bucket.openDownloadStreamByName(session + frame);
		byte[] bytes = new byte[(int)downloadStream.getGridFSFile().getLength()];
		int parsed = 0;
		while(parsed < bytes.length){
			downloadStream.read(bytes, parsed, downloadStream.getGridFSFile().getChunkSize());
		}
		downloadStream.close();
		
		try {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame = n;
		return null;
	}
	
	public BufferedImage pullMostRecentFrame(){
		if(bucket.find(Filters.eq("filename", session + (frame + 1))).iterator().hasNext()){
			mostRecentFrame = pullFrame(frame + 1);
			for(CameraFeedUpdateListener updateListener : updateListeners){
				updateListener.onNextFrame(mostRecentFrame);
			}
		}
		return mostRecentFrame;
	}
	
	
	
	public List<CameraFeedUpdateListener> getUpdateListeners(){
		return updateListeners;
	}
	
	public void close(){
		mongoClient.close();
	}

}
