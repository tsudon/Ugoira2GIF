/*
 *  Ugoira 2 GIF preview Ugoira@PIXIV export Animation GIF and APNG
 *   version 0.0.1 2018/08/24 Created Preview Animation and export GIF and APNG
 *   version 0.0.2 2018/08/24 Add Animation Frame and Edit View.
 *   
 *   require ZIP with JSON file. JSON file is ANKPIXIV JSON or original.
 *   If JSON file do not have,use default 1frame is 125ms.
 *   
 *   Features 
 *   1. Export JSON with ZIP file and more
 *   2. Edit Frame time and order.
 *   3. Import Folder in PNG,GIF,JPEG... 
 *   
 */

package application;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class UgoiraTrans extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("application.UgoiraTrans");
			Image icon = new Image("icon.png", false);
			primaryStage.getIcons().add(icon);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"),bundle); 		
			Parent root = loader.load();
			Scene scene = new Scene(root); 
			MainController mc = loader.getController();
			mc.setStage(primaryStage);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			String title = bundle.getString("application.name") + " " + bundle.getString("apllication.version");		
			primaryStage.setTitle(title);
				
			primaryStage.show();				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
