package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import application.animation.AnimationControle;
import application.animation.AnimationControle.FormatListEnum;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

/*
 * MainController is controller handler for Main Form.
 * 
 * 
 */


public class MainController {
	@FXML
	private Button browse;
	@FXML
	private Button transrate;
	@FXML
	private ImageView image;
	@FXML
	private ComboBox<FormatListEnum> format;

	private String transFilename = "";
	private JSONArray ugoira = null;
	private Stage stage;
	private Stage editStage;
	
	@FXML Button editButton;
	@FXML Pane imagePane;

	private AnimationControle ac;
	
	ResourceBundle bundle = ResourceBundle.getBundle("application.UgoiraTrans");
	
	@FXML
	private void initialize() {
		ac = new AnimationControle();
		ac.setImageView(image);
		ObservableList<FormatListEnum> list = FXCollections.observableArrayList(FormatListEnum.values());
		format.getItems().addAll(list);
		format.setValue(FormatListEnum.gif);
		image.fitHeightProperty().bind(imagePane.heightProperty());
		image.fitWidthProperty().bind(imagePane.widthProperty());
	}

	private void openImage(String path) {
		String notExtPath = path.substring(0, path.lastIndexOf("."));
		File file = new File(notExtPath + ".json");
		ZipFile zf;
		try {
			zf = new ZipFile(path);
		} catch (IOException e) {
			e.printStackTrace();
			
//			Alert alert = new Alert(AlertType.NONE, "ZipFileが読み込ません。", ButtonType.OK);
			Alert alert = new Alert(AlertType.NONE, bundle.getString("alert.unreadzipfile"), ButtonType.OK);
			alert.show();
			return;
		}

		
		try {
			String str = "";
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			do {
				line = reader.readLine();
				str += line;
			} while (line != null);
			JSONObject json = new JSONObject(str);
			String jsonCreator;
			
			try {
				jsonCreator = json.get("application").toString();
			} catch (JSONException e) {
				jsonCreator = "";
			}
			
			if (jsonCreator.compareTo("IVWINAnimation")==0) {	//IVWINAnimation JSON it's construction NOW!!
				try {
					float framerate;
					try {
						framerate = json.getJSONObject("animation").getFloat("framerate");
					} catch (JSONException e) {
						framerate = 30;		// 30fps
					}
					ugoira = json.getJSONObject("animation").getJSONArray("files");
					if (framerate != 1000.0) {
						float t = 0;
						for (int i=0;i<ugoira.length();i++) {
							float d = ugoira.getJSONObject(i).getFloat("d");
							d = (d / framerate) * 1000;
							float next_t = t + d;
							ugoira.getJSONObject(i).remove("d");
							ugoira.getJSONObject(i).put("d",(int)Math.round(next_t - Math.round(t)));
							t = next_t;
						}
					}
				} catch (Exception e) {
					System.err.println("Cannot support format");
				}
			} else {	//AnkPixiv JSON format
				ugoira = json.getJSONObject("info").getJSONArray("path").getJSONObject(0).getJSONArray("frames");
			}
			ac.load(path, ugoira);
			this.transFilename = path;
			return;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		JSONArray jsonarray = new JSONArray();
		Enumeration<? extends ZipEntry> entries = zf.entries();
		int deray = 125;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String f = entry.getName();
			JSONObject json = new JSONObject();
			json.put("f", f);
			json.put("d",deray);
			jsonarray.put(json);
		}

		try {
			zf.close();
		} catch (IOException e) {
			e.printStackTrace();
			return ;
		}
		ugoira =jsonarray;
		
//		Alert alert = new Alert(AlertType.NONE, "jsonファイルが読めませんのでデフォルトの設定(1frame = 125ms)を使います。", ButtonType.OK);
		Alert alert = new Alert(AlertType.NONE, bundle.getString("alert.useDefault"), ButtonType.OK);
		alert.show();
		ac.load(path, ugoira);
		this.transFilename = path;
	}

	// Event Listener on Button[#browse].onAction
	@FXML
	public void clickBrowse(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
//		fileChooser.setTitle("変換するうごイラのzipファイルを選択してください。");
		fileChooser.setTitle(bundle.getString("alert.selectZIP"));
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("zip Files", "*.zip"));

		File zipfile = fileChooser.showOpenDialog(null);
		if(zipfile != null) {
			openImage(zipfile.getPath());
		}
	}

	@FXML
	public void clickTrans(ActionEvent event) {
		if (new File(transFilename).exists()) {
//			System.out.println(format.getValue());
			String notExtPath = transFilename.substring(0, transFilename.lastIndexOf("."));		
			ac.save(transFilename, notExtPath + "." + format.getValue(), ugoira, format.getValue());
		} else {
//			Alert alert = new Alert(AlertType.NONE, "ファイルが存在しません", ButtonType.OK);
			Alert alert = new Alert(AlertType.NONE,bundle.getString("alert.filenotfound"), ButtonType.OK);
			alert.show();
		}
	}

	@FXML
	public void dropFile(DragEvent event) {
		String path = event.getDragboard().getFiles().get(0).getPath();
		String extend = path.substring(path.lastIndexOf("."), path.length());

		if (extend.compareToIgnoreCase(".zip") == 0) {
			openImage(path);
			event.setDropCompleted(true);
		} else {
//			Alert alert = new Alert(AlertType.NONE, "zipファイルではありません", ButtonType.OK);
			Alert alert = new Alert(AlertType.NONE,bundle.getString("alert.filenotZIP"), ButtonType.OK);
			alert.show();
			event.setDropCompleted(false);
		}
	}

	@FXML
	public void dropFileEnable(DragEvent event) {
		if (event.getDragboard().hasFiles()) {
			event.acceptTransferModes(TransferMode.ANY);
		}
	}

	@FXML
	public void clickEdit(ActionEvent event) throws IOException {
		editButton.setDisable(true);
		if(editStage == null) editStage = getEditStage();
	
		editStage.showAndWait();
		editButton.setDisable(false);
	}

	public Stage getEditStage() throws IOException {
		if (editStage == null ) {
			editStage = new Stage();			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("editview.fxml"),bundle);
			Parent editRoot = loader.load();
			Scene editScene = new Scene(editRoot);
			EditviewController ec = loader.getController();
			ec.setAnimationConrtole(ac);
			ec.setStage(editStage);
			editScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			editStage.initModality(Modality.APPLICATION_MODAL);		
			editStage.setScene(editScene);
//			editStage.setTitle("Edit画面");
			editStage.setTitle(bundle.getString("window.edit"));
		}
		return editStage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

}
