package entity;
/**
 * <p>
 * <b>Explanation:</b> The entity StudentGradeInfo contains the students details, their subjects, course, grades, the check of the grade by the teachers and their notes.
 * <p>
 * @author Everyone
 */
public class StudentsGradeInfo extends Student {

	String dateOfExam;
	String examID;
	String grade;
	String course;
	String approve;
	String notes;
	String changeReason;

	public StudentsGradeInfo(String id, String firstName, String lastName, String userName, String password,
			String email, String role, String dateOfExam, String examID, String grade, String course, String approve, String notes, String changeReason) {
		super(id, firstName, lastName, userName, password, email, role);
		this.dateOfExam = dateOfExam;
		this.examID = examID;
		this.grade = grade;
		this.course = course;
		this.approve=approve;
		this.notes=notes;
		this.changeReason=changeReason;
		
	}

	public String getDateOfExam() {
		return dateOfExam;
	}

	public void setDateOfExam(String dateOfExam) {
		this.dateOfExam = dateOfExam;
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}
	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getChangeReason() {
		return changeReason;
	}

	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

}




