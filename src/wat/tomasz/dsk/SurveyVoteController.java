package wat.tomasz.dsk;

import Answers.Answer;
import Surveys.SurveyHolder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import wat.tomasz.dsk.Utils.Utils;

public class SurveyVoteController {
	
	private static Survey survey = null;
	private int question = -1;
	private int activeCheck = -1;
	
	@FXML
	private GridPane grid;
	
	@FXML
	Label title;
	
	
	public SurveyVoteController(int question) {
		this.question = question;
	}
	
	@FXML
	private void initialize() {
		SurveyHolder sur = survey.getSurveysManager().getSurvey(question);
		if(sur != null) {
			title.setText(sur.getQuestion());
			
			int idx = 1;
			final ToggleGroup group = new ToggleGroup();
			for(String ans : sur.getAnswers()) {
				final int id = idx;
				Label lans = new Label();
				lans.setText(ans);
				
				RadioButton radio = new RadioButton();
				radio.setToggleGroup(group);
				radio.setSelected(false);
				
				radio.selectedProperty().addListener(new ChangeListener<Boolean>() {
					private int index = id;
					
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						if(oldValue != newValue) {
							if(newValue == true) {
								System.out.println("CHECKED NR " + index);
								activeCheck = index;
							}
						}
					}
				});
			
				grid.add(lans, 1, idx);
				grid.add(radio, 0, idx);
				idx++;
			}		
			
		}	
	}
	
	@FXML
	public void onVoted() {
		if(question < 0 || activeCheck < 0) {
			survey.showDialogError("Musisz wybraæ opcje przed zag³osowaniem", "Musisz wybraæ opcje przed zag³osowaniem");
		}
		
		int myid = survey.getConfigManager().getSelfId();
		String signText = myid + "" + question + "" + activeCheck;
		String siganture = Utils.getSignString(signText, survey.getConfigManager().getPrivateKey());
		
		Answer answer = new Answer(myid, question, activeCheck, siganture);
		survey.getSocketManager().getNodeSocket().broadcastAnswer(answer);
		
	}
	
	public static void setSurvey(Survey surv) {
		survey = surv;
	}
}
