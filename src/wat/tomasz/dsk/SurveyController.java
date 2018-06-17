package wat.tomasz.dsk;

import java.io.IOException;
import java.net.InetAddress;

import Nodes.Node;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wat.tomasz.dsk.Files.FileManager;
import wat.tomasz.dsk.Utils.Utils;

public class SurveyController {
	private static Survey survey;
	
	@FXML
	private TextField fieldAddress;
	
	@FXML
	private TextField fieldPort;
	
	@FXML
	private TextField fieldListenPort;
	
	@FXML
	private Label labelAllAnswers;
	
	@FXML
	private Label labelAllQuestions;
	
	@FXML
	private Label labelAllNodes;
	
	@FXML
	private VBox connectionParameters;
	
	@FXML
	private VBox surveyParameters;
	
	@FXML
	private void initialize() {	
		updateMainWindow();
	}
	
	@FXML
	private void createNetworkClicked() {	
		setSurveyView();
		
		int listenPort = 0;
		if(Utils.isValidPort(fieldListenPort.getText()))
			listenPort = Utils.getPort(fieldListenPort.getText());
		else
		{
			getSurvey().showDialogError("B³¹d", "Z³y format nas³uchiwanego portu");
			return;
		}
		
		FileManager.writeParameters(0, listenPort);
		getSurvey().getConfigManager().loadParameters();
		
		getSurvey().getNodesManager().clear();
		getSurvey().getNodesManager().setNode(0, new Node(Utils.getAddress("127.0.0.1"), 
				getSurvey().getConfigManager().getListenPort(), getSurvey().getConfigManager().getPublicKey()), true );
		
		updateMainWindow();
		getSurvey().getSocketManager().startNode(listenPort);
	}
	
	@FXML
	private void joinNetworkClicked() {		
		String sAddress = fieldAddress.getText();
		String sPort = fieldPort.getText();
		String sListenPort = fieldListenPort.getText();
		
		InetAddress address = null;
		int port = 0;
		int listenPort =  0;
		
		if(Utils.isValidAddress(sAddress))
			address = Utils.getAddress(sAddress);
		else 
		{
			getSurvey().showDialogError("B³¹d", "Z³y format adresu IPV4");
			return;
		}
		
		if(Utils.isValidPort(sPort))
			port = Utils.getPort(sPort);
		else
		{
			getSurvey().showDialogError("B³¹d", "Z³y format portu");
			return;
		}
		
		if(Utils.isValidPort(sListenPort))
			listenPort = Utils.getPort(sListenPort);
		else
		{
			getSurvey().showDialogError("B³¹d", "Z³y format nas³uchiwanego portu");
			return;
		}
		
		if(!getSurvey().getSocketManager().requestPing(address, port, listenPort)) {
			getSurvey().showDialogError("B³¹d", "Nie mo¿na po³¹czyæ siê z wêz³em");
		} 
		else {
			int id = getSurvey().getSocketManager().requestId( 
					getSurvey().getConfigManager().getPublicKey(), 
					address, port, listenPort);
			
			if(id <= 0) {
				getSurvey().showDialogError("B³¹d", "Nie uzyskano akceptacji od wêz³a");
			}
			else {
				getSurvey().getConfigManager().setSelfId(id);
				
				getSurvey().getNodesManager().setNode(id, new Node(Utils.getAddress("127.0.0.1"), 
						getSurvey().getConfigManager().getListenPort(), getSurvey().getConfigManager().getPublicKey()), true);
				
				updateMainWindow();
				FileManager.writeParameters(id,  listenPort);
				getSurvey().getController().setSurveyView();
				getSurvey().getSocketManager().startNode(listenPort);
			}
		}
	}
	
	@FXML
	public void onSurveyCreate() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("CreateSurvey.fxml"));
			Scene scene = new Scene(fxmlLoader.load());
			
			Stage stage = new Stage();
			stage.setTitle("Twórz ankiete");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onCheckSurveys() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("SurveyCheck.fxml"));
			Scene scene = new Scene(fxmlLoader.load());
			
			Stage stage = new Stage();
			stage.setTitle("Przegl¹daj ankiety");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateMainWindow() {
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				labelAllAnswers.setText("" + survey.getAnswersManager().getAnwerersSize());
				labelAllQuestions.setText("" + survey.getSurveysManager().getSurveysSize());
				labelAllNodes.setText("" + survey.getNodesManager().getNodesSize());
				
			}
		});
	}
	
	public void setConnectionView() {
		connectionParameters.setDisable(false);
		surveyParameters.setDisable(true);
	}
	
	public void setSurveyView() {
		connectionParameters.setDisable(true);
		surveyParameters.setDisable(false);
	}
	
	public void setListenPort(int port) {
		fieldListenPort.setText("" + port);
	}

	public static Survey getSurvey() {
		return survey;
	}

	public static void setSurvey(Survey surv) {
		survey = surv;
	}
}
