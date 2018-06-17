package Surveys;

import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import wat.tomasz.dsk.SurveyController;
import wat.tomasz.dsk.Files.FileManager;

public class SurveysManager {
	ArrayList<SurveyHolder> surveys = new ArrayList<SurveyHolder> ();
	
	public SurveysManager() {
		
	}
	
	public int addSurvey(SurveyHolder holder, boolean save) {
		surveys.add(holder);	
		int id = surveys.size();
		holder.setId(id);
		SurveyController.getSurvey().getController().updateMainWindow();
		
		if(save)
			FileManager.writeText("surveys.txt", holder.toPacket() + String.format("%n"), StandardOpenOption.APPEND);
		
		return id;
	}
	
	public int setSurvey(int id, SurveyHolder holder, boolean save) {
		surveys.add(holder);	
		holder.setId(id);
		SurveyController.getSurvey().getController().updateMainWindow();
		
		if(save)
			FileManager.writeText("surveys.txt", holder.toPacket() + String.format("%n"), StandardOpenOption.APPEND);
		
		return id;
	}
	
	public SurveyHolder getSurvey(int id) {
		for(SurveyHolder survey : surveys) {
			if(survey.getId() == id)
				return survey;
		}
		return null;
	}
	
	public boolean surveyExists(int id) {
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
