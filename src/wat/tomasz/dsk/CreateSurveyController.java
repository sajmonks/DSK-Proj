package wat.tomasz.dsk;

import java.util.ArrayList;

import Surveys.SurveyHolder;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import wat.tomasz.dsk.Utils.Utils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateSurveyController {
	
	private Survey survey = null;
	
	ArrayList<TextField> fields = new ArrayList<TextField>();
	
	@FXML
	TextField title;
	
	@FXML
	private GridPane optionGrid;
	
	@FXML
	private CheckBox annonymous;
	
	@FXML
	private void initialize() {	
	}
	
	@FXML
	public void addOption() {
		if(fields.size() >= 10)
			return;
		
		TextField field = new TextField();	
		
		fields.add(field);
		int id = fields.size();
		
		Label label = new Label();
		label.setText("Opcja " + id);
		
		
		optionGrid.add(label, 0, id);
		optionGrid.add(field, 1, id);
	}
	
	@FXML
	public void sendSurvey() {
		if(survey != null) {
			ArrayList<String> options = new ArrayList<String>();
			String header = title.getText();
			
			if(header.length() <= 1)  {
				survey.showDialogError("B³¹d wpisywania", "Za krótki tekst");
				return;
			}
			
			if(fields.size() == 0) {
				survey.showDialogError("Brak opcji", "Brak opcji");
				return;
			}
				
			for(TextField tf : fields) {
				String opt = tf.getText();
				if(opt.length() <= 2 && opt.length() != 0) {
					survey.showDialogError("B³¹d wpisywania", "Za krótki tekst");
					return;
				}
				
				if(opt.length() > 0)
					options.add(opt);
			}
			
			String sign = header;
			for(String opt : options) sign += opt;
			
			SurveyHolder holder = new SurveyHolder(
					survey.getConfigManager().getSelfId(), 
					(annonymous.isSelected() ? 1 : 0), header, options, Utils.getSignString(sign, survey.getConfigManager().getPrivateKey()));
			
			
			survey.getSurveysManager().addSurvey(holder);
			survey.getSocketManager().getNodeSocket().broadcastSurvey(holder);
			
			Stage stage = (Stage) title.getScene().getWindow();
			stage.close();
		}
	}
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
}
