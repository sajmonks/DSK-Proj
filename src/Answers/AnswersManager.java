package Answers;

import java.util.ArrayList;

public class AnswersManager {
	ArrayList<Answer> answers = new ArrayList<Answer>();
	
	public AnswersManager() {
		
	}
	
	public void addAnswer(Answer answer) {
		answers.add(answer);
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
	
	public int getAnwerersSize() {
		return answers.size();
	}
	
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
}
