package Answers;

import java.util.ArrayList;

import wat.tomasz.dsk.SurveyController;

public class AnswersManager {
	ArrayList<Answer> answers = new ArrayList<Answer>();
	
	public AnswersManager() {
		
	}
	
	public void addAnswer(Answer answer) {
		answers.add(answer);
		SurveyController.getSurvey().getController().updateMainWindow();
	}
	
	public boolean isValidAuthor(int author, int question) {
		for (Answer ans : answers) {
			if(ans.getAuthor() == author) {
				if(ans.getQuestion() == question) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Answer getAnswer(int author, int question) {
		for (Answer ans : answers) {
			if(ans.getAuthor() == author) {
				if(ans.getQuestion() == question) {
					return ans;
				}
			}
		}
		return null;
	}
	
	public int getAnswerCount(int question, int answer) {
		int count = 0;
		for (Answer ans : answers) {
			if(ans.getAnswer() == answer) {
				if(ans.getQuestion() == question) {
					count ++ ;
				}
			}
		}
		return count;
	}
	
	public int getAnwerersSize() {
		return answers.size();
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
}
