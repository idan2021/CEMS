package entity;

public class StudentExam {

	/**
	 * entity studentExam uses for keep track on student grade with the changes able
	 * to come and the approval the teacher need to apply before the grade is valid
	 * 
	 * @author Or steiner
	 */

	
	private String studentID;
	private String examID;
	private String examType;
	private String grade;
	private String approved;
	private String noteForStudent;
	private String reasonForGradeChange;
	private String[] studentAnwers;

	public StudentExam(String studentID, String examID, String examType, String grade, String approved,
			String noteForStudent, String reasonForGradeChange, String[] studentAnwers) {
		this.studentID = studentID;
		this.examID = examID;
		this.examType = examType;
		this.grade = grade;
		this.approved = approved;
		this.noteForStudent = noteForStudent;
		this.reasonForGradeChange = reasonForGradeChange;
		this.studentAnwers = studentAnwers;
	}
	

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getNoteForStudent() {
		return noteForStudent;
	}

	public void setNoteForStudent(String noteForStudent) {
		this.noteForStudent = noteForStudent;
	}

	public String getReasonForGradeChange() {
		return reasonForGradeChange;
	}

	public void setReasonForGradeChange(String reasonForGradeChange) {
		this.reasonForGradeChange = reasonForGradeChange;
	}

	public String[] getStudentAnwers() {
		return studentAnwers;
	}

	public void setStudentAnwers(String[] studentAnwers) {
		this.studentAnwers = studentAnwers;
	}
}
