package application;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.swing.event.ChangeListener;

import application.EditviewController.EditListViewPane;
import application.animation.AnimationControle;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.beans.value.ObservableValue;

/*
 * EditListViewPane for ListView
 * AnchorPane
 *  + Pane - Anchor (0,20,0,20) (Top,Bottom,Right,Left) 
 *     + ImageView bind PANE 
 *  + Label of No	- Anchor (-,20,-,0)
 *  + Label of Delay - Anchor (-,20,20,0)
 */


/*
 * EditviewController is a form for Animation Frame Edit.
 * This version only view frames data and Animation preview.
 *
 * 
 */


public class EditviewController {
	@FXML private Button editOK;
	@FXML private ImageView imageView2;
	@FXML private ListView<EditListViewPane> listView;
	@FXML private SplitPane splitPane;
	@FXML AnchorPane imagePane2;

	ObservableList<EditListViewPane> items=FXCollections.observableArrayList ();
	boolean recreating = false;
	double aspect;
	private Stage stage;
	private AnimationControle ac;
	@FXML Button rebuildButton;

	
	/*
	 * Click Edit OK button(stub)
	 * 
	 * 
	 */
	
	
	class TextFiledInList extends TextField{
		public int index;
		public EditviewController controller;
		
		public void fromAction(ActionEvent event) {
			/*
			int newDelay;
			EditListViewPane pane = selectedPane;
			try {
				newDelay = Integer.valueOf(pane.textDelay.getText());
				System.out.println(newDelay);
			} catch (Exception e){
				pane.textDelay.setText(selectedDelayStr);
			}
			*/
		}
		
	}


	class EditListViewPane extends AnchorPane{
		Label labelNo;
		TextFiledInList textDelay;
		Label labelDelay;
		Pane imagePane;
		ImageView imageView;
		
		EditListViewPane() {
//			super();
			labelNo = new Label();
			labelDelay = new Label();
			textDelay = new TextFiledInList();
			textDelay.setPrefSize(20, 20);
			textDelay.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
			textDelay.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
			textDelay.setAlignment(Pos.CENTER_RIGHT);
			textDelay.setEditable(false);
			imageView = new ImageView();
			imagePane = new Pane(imageView);
			setTopAnchor(imagePane, (double) 0);
			setRightAnchor(imagePane, (double) 20);
			setLeftAnchor(imagePane, (double) 0);
			setBottomAnchor(imagePane, (double) 20);

			setLeftAnchor(labelNo, (double) 0);
			setBottomAnchor(labelNo, (double) 0);	
			setBottomAnchor(labelDelay, (double) 0);	
			setRightAnchor(labelDelay, (double) 20);	
			setBottomAnchor(textDelay, (double) 0);	
			setRightAnchor(textDelay, (double) 40);
			getChildren().addAll(imagePane,labelNo,textDelay,labelDelay);
		}
		
		
		
	}

	
	@FXML
	public void clickEditOK(ActionEvent event) {
		hide();
	}
	
	/*
	 *  initialize is called by create instance.
	 *  1.Create Pane for ListView;
	 *  2.Create event handler for window show and hide.
	 *  3.Create event handler for SplitPane resize.
	 */
	
	@FXML
	public void initialize() {
		imageView2.fitHeightProperty().bind(imagePane2.heightProperty());
		imageView2.fitWidthProperty().bind(imagePane2.widthProperty());
		if(stage != null) {
			setEvent();
		}
	}
	

	private void setEvent() {
		stage.setOnShown(event -> { show();});
		stage.setOnCloseRequest(event -> { close();});
		DoubleProperty property = splitPane.getDividers().get(0).positionProperty();
		property.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			recreateListView() ;
		});

		listView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends EditListViewPane> observable, EditListViewPane oldValue,EditListViewPane newValue) -> {
					selectListView(newValue,oldValue);
		});
		
		
	}


	
	private void selectListView(EditListViewPane pane,EditListViewPane oldPane) {
		/*
		if(oldPane != null)	oldPane.textDelay.setEditable(false);
		pane.textDelay.setEditable(true);
		
		selectedNumber = listView.getFocusModel().getFocusedIndex();
		selectedPane = pane;
		selectedDelayStr = pane.textDelay.getText();
		System.out.println("select:" + selectedNumber);	
		*/
		int selectedNumber = listView.getFocusModel().getFocusedIndex();
		System.out.println("select:" + selectedNumber);	
		
	}


	/*
  * recreateListView 
  * If SplitPane resize,this method recalculate and resize ListView items.
  * 
  * 
  */

	void recreateListView() {
		if(recreating) return;
		recreating = true;
		ObservableList<EditListViewPane> items = listView.getItems();
		double width = listView.getWidth() - 20;
		double height = aspect * width;

		int i=0;
		try {
			for(i=0;i < items.size();i++) {
				EditListViewPane pane = items.get(i);
				pane.setPrefWidth(width);
				pane.setPrefHeight(height+20.0);
			}
		} catch(Exception e) {
			//
		}
		recreating = false;
	}
	
	/*
	 * show method call from form show event
	 * 
	 */
	void show() {
		runImageView();
		listCreate();
	}


	/*
	 * hide method call from push form close Buttons.
	 * call close event;
	 */
	
	void hide() {
		close();
		stage.close();	
	}

	/*
	 * close method call from close event
	 * 
	 */
	
	void close() {
		if(ac==null) return;
		if(ac.isLoaded()) {
			ac.stop();	
			ac.createAnimation();
			ac.run();
		}
	}
	
	/*
	 *  listCreate is Crate ListView from Animation Frame Data.
	 *  1. Create Pane List with a frame image , frame No and delay time;
	 *  2. width and height of list field Size auto calculate.
	 *  3. Attached ListView from create List
	 */

	
	public void listCreate() {
		if (ac == null) return ;
		
		if (ac.isLoaded()) {
			items.clear();
			double width = listView.getWidth() - 20;
			double height = 0;
			Image img = ac.getImage(0);
			double w = img.getWidth();
			double h =  img.getHeight();
			aspect = h / w;
			height = aspect * width;
			
			for (int i=0;i<ac.getFrameLength();i++) {
				img = ac.getImage(i);
				
		
				EditListViewPane pane = new EditListViewPane();
				ImageView iv = pane.imageView;
				iv.setImage(img);
				Pane imagePane = pane.imagePane;
				imagePane.setPrefWidth(width);
				imagePane.setPrefHeight(height);
				iv.fitWidthProperty().bind(imagePane.widthProperty());
				iv.fitHeightProperty().bind(imagePane.heightProperty());
				pane.imageView = iv;
				pane.labelNo.setText(Integer.toString(i) + " " + ac.getName(i));
				pane.textDelay.setText(Integer.toString(ac.getDelay(i)));
				pane.textDelay.setPrefSize(100, 20);
				pane.textDelay.index = i;				
				pane.textDelay.controller = this;
				pane.textDelay.setOnAction(event -> { pane.textDelay.fromAction(event);});
				pane.labelDelay.setText("ms");;			
				pane.setPrefWidth(width);
				pane.setPrefHeight(height + 20.0); 
				items.add(pane);
			}

			listView.setItems(items);
		}
		
	}
	

	/*
	 *  runIMageView is ImageView for Animation move Main Form to Edit Form
	 *  Main Form Animation -> stop 
	 *  ImageVIew Animation -> build
	 *  Edit Form Animation -> start 
	 */
	
	public void runImageView() {
		ac.stop();	
		ac.createAnimation(imageView2);
		ac.run();
	}

	/*
	 *  clickListView is Edit Animation Frames
	 *  but this version is stub.
	 */
	
	@FXML public void clickListView(MouseEvent event) {
/*
		int number = listView.getFocusModel().getFocusedIndex();
		if (event.getClickCount() == 2) {
			
		}
*/		
	}



	
	public void setStage(Stage stage) {
		this.stage = stage;
		if (stage != null) setEvent();
	}

	public Stage getStage() {
		return stage;
	}

	public void setAnimationConrtole(AnimationControle ac) {
		this.ac = ac;
	}

	@FXML public void delayChanged() {
		ac.stop();
		ac.createAnimation(imageView2);
		ac.run();
	}

}
