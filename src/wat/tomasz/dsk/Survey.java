package wat.tomasz.dsk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import wat.tomasz.dsk.ConfigManager.MissingFiles;

public class Survey extends Application {
	

	private ConfigManager configManager 			= new ConfigManager();
	private SurveyController surveyController 		= null;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Survey() {	
		if( configManager.createMissingFiles() == MissingFiles.Keys ) {
			showDialogInfo( "Generowanie kluczy", "Usuniêcie z sieci oraz generowanie kluczy" );
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		//Loading class
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("Survey.fxml").openStream());
		
		//Setting controller
		surveyController = loader.getController();
		surveyController.setSurvey(this);
		
		//Setting up window
		stage.setTitle("Diagnostyka systemów komputerowych");
		stage.setScene(new Scene(root ));
		stage.show();
	}
	
	public void showDialogError(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public void showDialogInfo(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
