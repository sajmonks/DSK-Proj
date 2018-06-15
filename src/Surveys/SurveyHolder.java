package Surveys;
import java.util.List;

public class SurveyHolder {
	private int id;
	private int author;
	private int type;
	private String question;
	private List<String> answers;
	private String signature;
	
	public SurveyHolder(int author, int type, String question, List<String> answers, String signature) {
		this.setAuthor(author);
		this.setType(type);
		this.setQuestion(question);
		this.setAnswers(answers);
		this.setSignature(signature);
	}

	public String toPacket() {
		String packet = id + " ";
		packet += author + " ";
		packet += type + " ";
		packet += question + "|";
		
		for(int i = 0; i < answers.size(); i++) {
			packet += answers.get(i) + "|";
		}
		
		packet += "END_PACK" + " ";
		packet += signature;
		
		return packet;	
	}

	public int getAuthor() {
		return author;
	}

	public void setAuthor(int author) {
		this.author = author;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	private void setSignature(String signature2) {
		this.signature = signature2;
	}
	
	public String getSignature() {
		return signature;
	}

	public void setString(String signature) {
		this.signature = signature;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
