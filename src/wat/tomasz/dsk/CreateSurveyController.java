package wat.tomasz.dsk;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateSurveyController {
	
	private Survey createSurvey = null;
	
	ArrayList<TextField> fields = new ArrayList<TextField>();
	
	@FXML
	private GridPane optionGrid;
	
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
		
	}
	
	public void setSurvey(Survey survey) {
		this.createSurvey = survey;
	}
}
