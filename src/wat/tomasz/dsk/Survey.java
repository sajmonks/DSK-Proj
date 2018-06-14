package wat.tomasz.dsk;

import java.util.List;

import Nodes.NodesManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import wat.tomasz.dsk.ConfigManager.MissingFiles;
import wat.tomasz.dsk.Sockets.SocketManager;

public class Survey extends Application {
	

	private ConfigManager configManager 			= new ConfigManager();
	private NodesManager nodesManager				= new NodesManager();
	private SocketManager socketManager				= new SocketManager(this, nodesManager);
	private SurveyController surveyController 		= null;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Survey() {	
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
		
		boolean inNetwork = true;
		List<MissingFiles> missing = getConfigManager().createMissingFiles();
		for(MissingFiles miss : missing) {
			if(miss == MissingFiles.Keys) {
				showDialogInfo( "Generowanie kluczy", "Usuniêcie z sieci oraz generowanie kluczy" );
			}
			
			if(miss == MissingFiles.Local) {
				inNetwork = false;
			}
		}
		if(inNetwork) {
			getController().setListenPort(getConfigManager().getListenPort());
			getController().setSurveyView();
			getSocketManager().startNode(getConfigManager().getListenPort());
		}
		else {
			getController().setConnectionView();
		}
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
	
	public SurveyController getController() {
		return surveyController;
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public SocketManager getSocketManager() {
		return socketManager;
	}

	public NodesManager getNodesManager() {
		return nodesManager;
	}

	public void setNodesManager(NodesManager nodesManager) {
		this.nodesManager = nodesManager;
	}
}
