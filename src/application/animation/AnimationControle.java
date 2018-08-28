/**
 * This package is load from Ugoira ZIP File and animate ImageView and save another format.
 *
 */
package application.animation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.ZipFile;
import org.json.JSONArray;

import javafx.animation.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import tsudon.image.GIFSaver;
import tsudon.image.PNGSaver;

/**
 * This class is keeping Animation Frame data.
 *
 */
class AnimationFrame {

	int delay;
	Image image;
	String name = "";

	AnimationFrame(Image image, int delay) {
		this.image = image;
		this.delay = delay;
	}

	AnimationFrame(Image image, String name) {
		this.image = image;
		this.name = name;
		this.delay = 0;
	}

	AnimationFrame(Image image,String name, int delay) {
		this.image = image;
		this.name = name;
		this.delay = delay;
	}

	AnimationFrame(Image image) {
		this.image = image;
		this.delay = 0;
	}

	AnimationFrame(InputStream stream) {
		this.image = new Image(stream);
		this.delay = 0;
	}

	AnimationFrame(InputStream stream,String name) {
		this.image = new Image(stream);
		this.name =name;
		this.delay = 0;
	}

	AnimationFrame(InputStream stream, int delay) {
		this.image = new Image(stream);
		this.delay = delay;
	}

	AnimationFrame(InputStream stream,String name , int delay) {
		this.image = new Image(stream);
		this.name = name;
		this.delay = delay;
	}

}

/**
 * This class is load and save Ugoira ,main controller
 * 
 */

public class AnimationControle {
	ResourceBundle bundle = ResourceBundle.getBundle("application.animation.AnimationControle");


	/**
	 * This enum is save method for select option.
	 * 
	 */
	public enum FormatListEnum {

	gif, // GIF transfer
	png, // APNG transfer
	mjpeg; // Motion JPEG transfer -- not available
	;
	}

	private ImageView imageView;
	private AnimationFrame[] frames;
//	private boolean loop = true;
	private Timeline timeline;
	private boolean isLoadedFlag = false;

	
	
	public Image getImage(int index) {
		if(index<frames.length) {
			return frames[index].image;
		}
		return null;
	}

	public void setImage(int index,Image img) {
		if(index<frames.length) {
			frames[index].image = img;
		}
	}

	public int getDelay(int index) {
		if(index<frames.length) {
			return frames[index].delay;
		}
		return 0;
	}

	public void setDelay(int index,int delay) {
		if(index<frames.length) {
			frames[index].delay = delay;
		}
	}

	/**
	 * ImageView setter.
	 * 
	 * @param ImageView
	 * 
	 */
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	/**
	 * ImageView getter.
	 * 
	 * @return ImageView
	 * 
	 */
	public ImageView getImageView() {
		return this.imageView;
	}

	/**
	 * This method is load ugoira and animate ImageView
	 * 
	 * @param String    ZIP filename
	 * @param JSONArray frame data from JSON
	 * 
	 */

	public void run () {
		if(timeline != null ) {
			timeline.play();
		}
	}

	public void pause () {
		if(timeline != null ) {
			timeline.pause();
		}
	}


	public void stop() {
		if(timeline != null) {
			timeline.stop();	
		}
	}

	public void load(String zipname, JSONArray framejson) {
		@SuppressWarnings("resource")
		ZipFile zipFile;
		int maxframe;
//Open ZIP file and create Frames.
		try {
			zipFile = new ZipFile(zipname);
			maxframe = framejson.length();
			this.frames = new AnimationFrame[maxframe];

//Read image data from image files include ZIP file.

			for (int i = 0; i < maxframe; i++) {
				InputStream stream;
				String filename = framejson.getJSONObject(i).getString("f");
				int delay = framejson.getJSONObject(i).getInt("d");
				stream = zipFile.getInputStream(zipFile.getEntry(filename));
				this.frames[i] = new AnimationFrame(stream,filename, delay);
			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

//Create Animation and play
		createAnimation(imageView);
		isLoadedFlag = true;
		run();
	}

	public boolean isLoaded() {
		return isLoadedFlag;
	}
	
	public void createAnimation() {
		createAnimation(this.imageView);
	}

	public void createAnimation(ImageView imageView) {
		if (imageView == null || this.frames == null)
			return; // null check;
		int maxframe = this.frames.length;
		imageView.setImage(this.frames[0].image);
		KeyFrame keyframes[] = new KeyFrame[maxframe];
		int frameTime = 0;
		for (int i = 0; i < maxframe; i++) {
			KeyValue key = new KeyValue(imageView.imageProperty(), frames[i].image);
			keyframes[i] = new KeyFrame(new Duration(frameTime), key);
			frameTime += this.frames[i].delay;
		}

		if(timeline != null) timeline.stop();  // for possibility resource leak
		timeline = new Timeline(keyframes);
		timeline.setCycleCount(maxframe);
	}

	/**
	 * @param String         ZIP filename
	 * @param Sttring        save filename
	 * @param JSONArray      frame data from JSON
	 * @param FormatListEnum convert Graphic format type
	 * 
	 * @return 0 is success,other is error.
	 */
	public int save(String zipname, String outpath, JSONArray framejson, FormatListEnum fmt) {
		boolean ret = false;
//		String alartText = outpath + "にファイルを出力しますか？";
		String alartText = outpath + bundle.getString("alert.fileout");
		Alert alert = new Alert(AlertType.CONFIRMATION, alartText);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() != ButtonType.OK) {
//			System.err.println("CANCELされました。");
			return -1;
		}

		switch (fmt) {
		case gif:
			ret = saveGIF(outpath, this.frames);
			break;
		case png:
			ret = saveAPNG(outpath, this.frames);
			break;
//		case mjpeg:
		default:
//			alert = new Alert(AlertType.NONE, "このフォーマットは現在サポートしていません。", ButtonType.OK);
			alert = new Alert(AlertType.NONE, bundle.getString("alert.notSupport"), ButtonType.OK);
			alert.show();
			ret = false;
			break;
		}

		if (ret == false) {		
//			System.out.println(outpath + " <= output error.");
			return -2;
		}
		return 0;
	}


	private boolean saveGIF(String outpath, AnimationFrame[] frames) {
		Image[] imgs = new Image[frames.length];
		int[] delays = new int[frames.length];

		for (int i = 0; i < frames.length; i++) {
			imgs[i] = frames[i].image;
			delays[i] = frames[i].delay;
		}

		boolean ret = false;
		try {
			GIFSaver.AnimationGIFWriteFile(outpath, imgs, delays);
			ret = true;
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.NONE, bundle.getString("alert.falsesave"), ButtonType.OK);
			alert.show();
			ret = false;
		}
		return ret;
	}

	private boolean saveAPNG(String outpath, AnimationFrame[] frames) {
		Image[] imgs = new Image[frames.length];
		int[] delays = new int[frames.length];

		for (int i = 0; i < frames.length; i++) {
			imgs[i] = frames[i].image;
			delays[i] = frames[i].delay;
		}

		boolean ret = false;
		try {
			PNGSaver.APNGWriteFile(outpath, imgs, delays);
			ret = true;
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.NONE, bundle.getString("alert.falsesave"), ButtonType.OK);
			alert.show();
		
			ret = false;
		}
		return ret;
	}

	public int getFrameLength() {
		if(frames != null) return frames.length;
		return 0;
	}

	public String getName(int i) {
		return frames[i].name;
	}

}
