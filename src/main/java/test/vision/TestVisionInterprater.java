//package test.vision;
//
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.embed.swing.SwingFXUtils;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.image.ImageView;
//import javafx.scene.image.WritableImage;
//import javafx.scene.layout.StackPane;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import javax.imageio.ImageIO;
//
//import org.ilite.vision.api.messages.RobotVisionMsg;
//import org.ilite.vision.api.system.VisionListener;
//import org.ilite.vision.api.system.VisionSystemAPI;
//import org.ilite.vision.constants.ECameraType;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.gridfs.GridFSBucket;
//import com.mongodb.client.gridfs.GridFSBuckets;
//import com.mongodb.client.gridfs.GridFSDownloadStream;
//import com.mongodb.client.gridfs.GridFSUploadStream;
//import com.mongodb.client.gridfs.model.GridFSDownloadByNameOptions;
//import com.mongodb.client.gridfs.model.GridFSUploadOptions;
//
//public class TestVisionInterprater extends Application implements VisionListener{
//	
//	private static final MongoClientURI MONGO_URI = new MongoClientURI("mongodb://localhost/ilite");
//	private static final int CHUNK_SIZE_IN_BYTES = 200000;
//	private StackPane stackpane, overlay;
//	private static GridFSUploadOptions options;
//	private static GridFSDownloadByNameOptions doptions;
//	private int frame;
//	private Text frameCounter;
//	
//	private long time;
//	
//	@Override
//	public void onVisionDataRecieved(RobotVisionMsg message) {
//		try{
//			BufferedImage image = message.getRawImage();
//			MongoClient mongo = new MongoClient(MONGO_URI);
//				MongoDatabase db = mongo.getDatabase("test");
//				GridFSBucket bucket = GridFSBuckets.create(db, "videos");
//				
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				ImageIO.write(image, "PNG", baos);
//				byte[] bytesUp = baos.toByteArray();
//				
//				GridFSUploadStream uploadStream = bucket.openUploadStream("video" + frame, options);
//				uploadStream.write(bytesUp);
//				uploadStream.close();
//				
//				baos = new ByteArrayOutputStream();
//				GridFSDownloadStream downloadStream = bucket.openDownloadStreamByName("video" + frame, doptions);
//				byte[] bytesDown = new byte[(int)downloadStream.getGridFSFile().getLength()];
//				int bytesRead = 0;
//				while(bytesRead < bytesUp.length){
//					bytesRead += downloadStream.read(bytesDown, bytesRead, downloadStream.getGridFSFile().getChunkSize());
//				}
//				downloadStream.close();
//				
//				InputStream imgInput = new ByteArrayInputStream(bytesDown);
//
//				BufferedImage newImage = ImageIO.read(imgInput);
//				WritableImage wr = new WritableImage(newImage.getWidth(), newImage.getHeight());
//				SwingFXUtils.toFXImage(newImage, wr);
//				Platform.runLater(new Runnable(){
//					public void run() {
//						stackpane.getChildren().setAll(new ImageView(wr));
//						stackpane.setMinSize(wr.getWidth(), wr.getHeight());
//						overlay.setMinSize(wr.getWidth(), wr.getHeight());
//						long newTime = System.currentTimeMillis();
//						frameCounter.setText(1000/(newTime-time) + "");
//						time = newTime;
//					}
//				});
//				
//			mongo.close();
//			
//		}catch (Throwable e) {
//			e.printStackTrace();
//		}
//		frame++;
//	}
//	
//	public static void main(String[] args){
//		try{
//			Application.launch();
//		}
//		catch(Throwable t){
//			t.printStackTrace();
//		}
//	}
//	
//	
//
//	@Override
//	public void start(Stage stage) throws Exception {
//		try{
//			stackpane = new StackPane();
//			frameCounter = new Text("0");
//			frameCounter.setStyle("{-fx-text-fill:white;-fx-font-size:20;}");
//			overlay = new StackPane();
//			StackPane.setAlignment(frameCounter, Pos.TOP_RIGHT);
//			overlay.getChildren().addAll(stackpane, frameCounter);
//			Scene mainScene = new Scene(overlay);
//			stage.setScene(mainScene);
//			
//			VisionSystemAPI.getVisionSystem(ECameraType.LOCAL_CAMERA).subscribe(this);
//			
//			options = new GridFSUploadOptions();
//			options.chunkSizeBytes(CHUNK_SIZE_IN_BYTES);
//			
//			doptions = new GridFSDownloadByNameOptions();
//			stage.show();
//			stage.setWidth(800);
//			stage.setHeight(600);
//			
//			Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);;
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	
//	public void stop(){
//		System.exit(0);
//	}
//}
