package wat.tomasz.dsk;

import java.io.IOException;

import Surveys.SurveyHolder;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SurveyCheckController {
	private static Survey survey = null;
	
	@FXML
	private GridPane grid;
	
	@FXML
	private void initialize() {	
		if(survey == null) {
			System.out.println("Survey is null");
			return;
		}
		
		int idx = 1;
		for(SurveyHolder sur : survey.getSurveysManager().getSurveys()) {
			Label idlabel = new Label();
			idlabel.setText(sur.getId() + "");
			
			Label autorlabel = new Label();
			autorlabel.setText(sur.getAuthor() + "");
			
			Label titlelabel = new Label();
			titlelabel.setText(sur.getQuestion());
			
			Button button = new Button();
			button.setText("Przegl¹daj");
			button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				private int index = sur.getId();
				
				@Override
				public void handle(MouseEvent event) {
				
					try {
						FXMLLoader fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(getClass().getResource("VoteSurvey.fxml"));
						
						SurveyVoteController controller = new SurveyVoteController(index);
						fxmlLoader.setController(controller);
						
						Scene scene = new Scene(fxmlLoader.load());
						Stage stage = new Stage();
						stage.setTitle("Przegl¹daj ankiety");
						stage.setScene(scene);
						stage.show();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			});
			
			Label yesnolabel = new Label();
			if(survey.getAnswersManager().isValidAuthor(survey.getConfigManager().getSelfId(), sur.getId()))
				yesnolabel.setText("Nie");
			else
				yesnolabel.setText("Tak");
			
			grid.add(idlabel, 0, idx);
			grid.add(autorlabel, 1, idx);
			grid.add(titlelabel, 2, idx);
			grid.add(yesnolabel, 3, idx);
			grid.add(button, 4, idx);
			idx++;
		}
	}
	
	public static void setSurvey(Survey surv) {
		survey = surv;
	}
}
