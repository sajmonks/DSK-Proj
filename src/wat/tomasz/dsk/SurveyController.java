package wat.tomasz.dsk;

import java.net.InetAddress;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import wat.tomasz.dsk.Utils.Utils;

public class SurveyController {
	private Survey survey;
	
	@FXML
	private TextField fieldAddress;
	
	@FXML
	private TextField fieldPort;
	
	@FXML
	private PasswordField fieldPassword;
	
	@FXML
	private void initialize() {
		
	}
	
	@FXML
	private void createNetworkClicked() {	
		System.out.println("CLICKED2");
	}
	
	@FXML
	private void joinNetworkClicked() {		
		String sAddress = fieldAddress.getText();
		String sPort = fieldPort.getText();
		String sPassword = fieldPassword.getText();
		
		InetAddress address = null;
		int port = 0;
		
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
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
}
