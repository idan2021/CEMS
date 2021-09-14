package entity;

public class Course {

	String subjectID, courseID, subjectName, courseName;

	// constructor
	public Course(String subjectID, String courseID, String subjectName, String courseName) {

		this.subjectID = subjectID;
		this.courseID = courseID;
		this.subjectName = subjectName;
		this.courseName = courseName;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

}
