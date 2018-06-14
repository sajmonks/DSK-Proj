package wat.tomasz.dsk;

import java.net.InetAddress;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import wat.tomasz.dsk.ConfigManager.MissingFiles;
import wat.tomasz.dsk.Files.FileManager;
import wat.tomasz.dsk.Utils.Utils;

public class SurveyController {
	private Survey survey;
	
	@FXML
	private TextField fieldAddress;
	
	@FXML
	private TextField fieldPort;
	
	@FXML
	private TextField fieldListenPort;
	
	@FXML
	private VBox connectionParameters;
	
	@FXML
	private VBox surveyParameters;
	
	@FXML
	private void initialize() {	
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
		
		//TODO ZMIANA STANU
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
				//TODO ZMIANA STANU
			}
		}
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

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
}
