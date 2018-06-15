package Answers;

public class Answer {
	private int author;
	private int question;
	private int answer;
	private String signature;
	
	public Answer(int author, int question, int answer, String signature) {
		this.setAuthor(author);
		this.setQuestion(question);
		this.setAnswer(answer);
		this.setSignature(signature);
	}
	
	public String toPacket() {
		String packet = author + " ";
		packet += question + " ";
		packet += answer + " ";
		packet += signature;
		return packet;	
	}


	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}


	public int getAuthor() {
		return author;
	}


	public void setAuthor(int author) {
		this.author = author;
	}
}
