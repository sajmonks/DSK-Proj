package Surveys;

import java.util.ArrayList;

public class SurveysManager {
	ArrayList<SurveyHolder> surveys = new ArrayList<SurveyHolder> ();
	
	public SurveysManager() {
		
	}
	
	public int addSurvey(SurveyHolder holder) {
		surveys.add(holder);
		
		int id = surveys.size();
		holder.setId(id);
		
		return id;
	}
	
	public boolean containId(int id) {
		for(SurveyHolder survey : surveys) {
			if(survey.getId() == id)
				return true;
		}
		return false;
	}
	
	public int getSurveysSize() {
		return surveys.size();
	}
	
	public ArrayList<SurveyHolder> getSurveys() {
		return surveys;
	}
}
