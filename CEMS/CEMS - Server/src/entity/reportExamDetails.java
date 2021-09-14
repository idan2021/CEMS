package entity;
/**
 * used to fill the lines in table view of exam report statistics
 * @author DvirVahav
 *
 */
public class reportExamDetails {
	private String examID, examSubject, examCourse,avg,median;
	
	//for calculations
	private int avgNum,medianNum;

	public int getAvgNum() {
		return avgNum;
	}

	public void setAvgNum(int avgNum) {
		this.avgNum = avgNum;
	}

	public int getMedianNum() {
		return medianNum;
	}

	public void setMedianNum(int medianNum) {
		this.medianNum = medianNum;
	}

	public reportExamDetails(String examID, String examSubject, String examCourse,  String median,String avg) {
		super();
		this.examID = examID;
		this.examSubject = examSubject;
		this.examCourse = examCourse;
		this.avg = avg;
		this.median = median;
		this.avgNum=Integer.parseInt(avg);
		this.medianNum=Integer.parseInt(median);
	}

	public String getExamID() {
		return examID;
	}

	public void setExamID(String examID) {
		this.examID = examID;
	}

	public String getExamSubject() {
		return examSubject;
	}

	public void setExamSubject(String examSubject) {
		this.examSubject = examSubject;
	}

	public String getExamCourse() {
		return examCourse;
	}

	public void setExamCourse(String examCourse) {
		this.examCourse = examCourse;
	}

	public String getAvg() {
		return avg;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}

	public String getMedian() {
		return median;
	}

	public void setMedian(String median) {
		this.median = median;
	}

	
	
}
