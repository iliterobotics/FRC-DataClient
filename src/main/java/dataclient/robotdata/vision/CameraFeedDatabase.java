package dataclient.robotdata.vision;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.fauge.robotics.towertracker.ITowerListener;
import com.fauge.robotics.towertracker.TowerMessage;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

import dataclient.DataClient;

/**
 * class for taking in new frames from the camera when 
 * @author Michael
 */
public class CameraFeedDatabase implements ITowerListener{
	
	/** In bytes */
	private static final int CHUNK_SIZE = 200000;
	
	private MongoClient mongoClient;
	private GridFSUploadOptions UPLOAD_OPTIONS;
	private MongoDatabase mongodb;
	private GridFSBucket bucket;
	
	private BufferedImage mostRecentFrame;
	private List<CameraFeedUpdateListener> updateListeners;
	private final String session;
	private int frameNumber;
	private HighGoal metaData;
	
	private String mongoDBURI;
	private String dbName;
	
	private String alignmentX;
	private String alignmentY;
	private double distance;
	private double azimuthX;
	private double azimuthY;
	
	public CameraFeedDatabase(DataClient client, String mongodbURI, String dbname, String session){
		this.session = session;
		frameNumber = 0;
		
		metaData = new HighGoal(client);
		client.watch(metaData, setVals -> {
			alignmentX = metaData.getXAlignment();
			alignmentY = metaData.getYAlignment();
			distance = metaData.getDistance();
			azimuthX = metaData.getAzimuthX();
			azimuthY = metaData.getAzimuthY();
		});
		mongoDBURI = mongodbURI;
		dbName = dbname;
	}
	
	public void setUpMongo(){
		mongoClient = new MongoClient(mongoDBURI);
		mongodb = mongoClient.getDatabase(dbName);
		bucket = GridFSBuckets.create(mongodb, session);
		
		updateListeners = new ArrayList<CameraFeedUpdateListener>();
		
		UPLOAD_OPTIONS = new GridFSUploadOptions();
		UPLOAD_OPTIONS.chunkSizeBytes(CHUNK_SIZE);	
	}
	
	public long getFrameTimeLength(int frame){
		GridFSDownloadStream downloadStream = bucket.openDownloadStreamByName(session + "_" + frame);
		return downloadStream.getGridFSFile().getUploadDate().getTime();
	}
	
	//**NOTE: has to be synchronized because then multiple frames are pushed simultaneously with the same frame number*/
	public synchronized void pushFrame(BufferedImage image){
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", byteStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		byte[] bytes = byteStream.toByteArray();
		
		GridFSUploadStream uploadStream = bucket.openUploadStream(session + "_" +  frameNumber, UPLOAD_OPTIONS);
		uploadStream.write(bytes);
		uploadStream.close();
		
		System.out.println("WROTE FRAME " + frameNumber + " to " + session);
		
		frameNumber++;
		mostRecentFrame = image;
	}
	
	
	public BufferedImage pullFrame(int n){
		GridFSDownloadStream downloadStream = bucket.openDownloadStreamByName(session + "_" + n);
		byte[] bytes = new byte[(int)downloadStream.getGridFSFile().getLength()];
		int parsed = 0;
		while(parsed < bytes.length){
			parsed += downloadStream.read(bytes, parsed, Math.min(downloadStream.getGridFSFile().getChunkSize(), bytes.length));
		}
		downloadStream.close();
		
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			mostRecentFrame = img;
			return img;
		} catch (IOException e) {
			e.printStackTrace();
		}
		frameNumber = n;
		return null;
	}
	
	public BufferedImage pullMostRecentFrame(){
		if(bucket.find(Filters.eq("filename", session + "_" + (frameNumber + 1))).iterator().hasNext()){
			mostRecentFrame = pullFrame(frameNumber + 1);
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

	@Override
	public void fire(TowerMessage message) {
		//pushFrame(message.bImage);
		
		metaData.setXAlignment(message.alignmentX);
		metaData.setYAlignment(message.alignmentY);
		metaData.setAngleOfElevationX(message.aoe_X);
		metaData.setAngleOfElevationY(message.aoe_Y);
		metaData.setDistance(message.distance);
		metaData.setIsGoalFound(!(message.aoe_X == 0 && message.distance == 0));
		System.out.println("GOAL FOUND?:" + metaData.isGoalFound());
		try {
			metaData.push();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
