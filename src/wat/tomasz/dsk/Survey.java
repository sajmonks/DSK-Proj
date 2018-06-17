package wat.tomasz.dsk;

import java.util.List;

import Answers.AnswersManager;
import Nodes.Node;
import Nodes.NodesManager;
import Surveys.SurveysManager;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wat.tomasz.dsk.ConfigManager.MissingFiles;
import wat.tomasz.dsk.Files.FileManager;
import wat.tomasz.dsk.Sockets.SocketManager;
import wat.tomasz.dsk.Utils.Utils;

public class Survey extends Application {
	

	private ConfigManager configManager 			= new ConfigManager();
	private NodesManager nodesManager				= new NodesManager();
	private AnswersManager answersManager			= new AnswersManager();
	private SurveysManager surveysManager 			= new SurveysManager();
	private SocketManager socketManager				= new SocketManager(this);
	private SurveyController surveyController 		= null;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Survey() {	
		SurveyController.setSurvey(this);
		CreateSurveyController.setSurvey(this);
		SurveyCheckController.setSurvey(this);
		SurveyVoteController.setSurvey(this);
	}

	@Override
	public void start(Stage stage) throws Exception {		
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("Survey.fxml").openStream());
		surveyController = loader.getController();
		stage.setTitle("Diagnostyka systemów komputerowych");
		stage.setScene(new Scene(root ));
		
		stage.setOnHiding(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				getSocketManager().closeThread();
			}
			
		});
		
		stage.show();

		
		boolean inNetwork = true;
		List<MissingFiles> missing = getConfigManager().createMissingFiles();
		for(MissingFiles miss : missing) {
			if(miss == MissingFiles.Keys) {
				showDialogInfo( "Generowanie kluczy", "Usuniêcie z sieci oraz generowanie kluczy" );
				refreshDirectory();
				inNetwork = false;
			}
			
			if(miss == MissingFiles.Local || miss == MissingFiles.Nodes) {
				inNetwork = false;
			}
		}
		if(inNetwork) {
			configManager.loadParameters();
			getController().setListenPort(getConfigManager().getListenPort());
			getController().setSurveyView();
			getNodesManager().clear();
			getNodesManager().setNode(0, new Node(Utils.getAddress("127.0.0.1"), //TODO
			getConfigManager().getListenPort(), getConfigManager().getPublicKey()), true );
			getController().updateMainWindow();
			
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
	
	public void refreshDirectory() {
		FileManager.removeIfExists("private.key");
		FileManager.removeIfExists("public.key");
		FileManager.removeIfExists("local.txt");
		FileManager.removeIfExists("nodes.txt");
		FileManager.removeIfExists("surveys.txt");
		FileManager.removeIfExists("answers.txt");
		
		configManager.generateKeys();
		FileManager.createFile("nodes.txt");
		FileManager.createFile("surveys.txt");
		FileManager.createFile("answers.txt");
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

	public AnswersManager getAnswersManager() {
		return answersManager;
	}

	public void setAnswersManager(AnswersManager answersManager) {
		this.answersManager = answersManager;
	}

	public SurveysManager getSurveysManager() {
		return surveysManager;
	}

	public void setSurveysManager(SurveysManager surveysManager) {
		this.surveysManager = surveysManager;
	}
}
