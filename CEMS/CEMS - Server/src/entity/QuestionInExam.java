package entity;

public class QuestionInExam {

	private Question question;
	private int points;
	private String studentComment;
	private String teacherComment;
	private String questionID;
	private String questionContent;
	private String examID;
	
	public QuestionInExam(Question question, int points, String studentComment, String teacherComment, String examID) {
		this.question = question;
		this.questionID = question.getId();
		this.questionContent = question.getContent();
		this.points = points;
		this.studentComment = studentComment;
		this.teacherComment = teacherComment;
		this.examID = examID;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getStudentComment() {
		return studentComment;
	}

	public void setStudentComment(String studentComment) {
		this.studentComment = studentComment;
	}

	public String getTeacherComment() {
		return teacherComment;
	}

	public void setTeacherComment(String teacherComment) {
		this.teacherComment = teacherComment;
	}

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String toString() {
		return "questionID: " + this.questionID + "points: " + this.points + "commentForTeacher: " + this.teacherComment
				+ "commentForStudent: " + this.studentComment + "examID: " + this.examID;
	}

}
