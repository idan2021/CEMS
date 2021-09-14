package entity;

/**
 * <p>
 * <b>Explanation:</b> The Question class represents questions in the question bank.
 * <p>
 * it contains variables such as ID and content, standard getters and setters, a custom toString method and an editQuestion method that changes its contents.
 * <p>
 * @author Everyone
 */
public class Question {

	protected String id;
	protected String content;
	protected String correctAnswer;
	protected String wrongAnswer1;
	protected String wrongAnswer2;
	protected String wrongAnswer3;
	private String subject;
	private String composer;
	private String courses;

	// constructor
	public Question(String composer, String subject, String content, String answer0, String answer1, String answer2,
			String correctAnswer, String courses, String id) {

		this.id = id;
		this.content = content;
		this.subject = subject;
		this.courses = courses;
		this.correctAnswer = correctAnswer;
		this.wrongAnswer1 = answer0;
		this.wrongAnswer2 = answer1;
		this.wrongAnswer3 = answer2;
		this.composer = composer;
	}

	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public String getSubject() {
		return subject;
	}

	public String getCourses() {
		return courses;
	}

	public String getId() {
		return id;
	}

	// NOT TO BE USED IN CODE!!! ONLY FOR TESTS
	/*
	 * public void setId(String id) { this.id = id; }
	 */

	public String getContent() {
		return content;
	}

	public void setDescription(String description) {
		this.content = description;
	}

	public String getWrongAnswer1() {
		return wrongAnswer1;
	}

	public String getWrongAnswer2() {
		return wrongAnswer2;
	}

	public String getWrongAnswer3() {
		return wrongAnswer3;
	}

	public void setRightAnswer(String ra) {
		correctAnswer = ra;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

// 
	public void editQuestion(Question q) {
		this.subject = q.getSubject();
		this.courses = q.getCourses();
		this.content = q.getContent();
		this.wrongAnswer1 = q.getWrongAnswer1();
		this.wrongAnswer2 = q.getWrongAnswer2();
		this.wrongAnswer3 = q.getWrongAnswer3();
	}

	public String toString() {
		return "composer: " + this.getComposer() + "subject: " + this.getSubject() + "content: " + this.getContent()
				+ "WrongAnswer1: " + this.getWrongAnswer1() + "WrongAnswer2: " + this.getWrongAnswer2()
				+ "WrongAnswer3: " + this.getWrongAnswer3() + "CorrectAnswer: " + this.getCorrectAnswer() + "courses: "
				+ this.getCourses() + "questionID: " + this.getId();
	}

}
