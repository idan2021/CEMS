package entity;

public class ExamWaitingForApproval{
	
	String newDuration;
	String explanation;
	String id;
	String subject;
	String course;
	
	public ExamWaitingForApproval(String id, String subject, String course, String newDuration, String explanation) {
		this.newDuration = newDuration;
		this.explanation = explanation;
		this.id = id;
		this.subject = subject;
		this.course = course;
	}
	
	public String getNewDuration() {
		return newDuration;
	}
	public void setNewDuration(String newDuration) {
		this.newDuration = newDuration;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return "ExamWaitingForApproval [newDuration=" + newDuration + ", explanation=" + explanation + ", id=" + id
				+ ", subject=" + subject + ", course=" + course + "]";
	}
	
	
}
