package entity;

import java.io.Serializable;

/**
 * <p>
 * <b>Explanation:</b> The MyFile class represents the files that the user insert to the DB.
 * <p>
 * @author Everyone
 */
public class MyFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1851359291537701843L;
	private String studentID = null;
	private String examID = null;
	private String fileName = null;
	private String examType = null;
	private String examSubject;
	private String examCourse;
	private String examDate;
	private int size = 0;
	public byte[] mybytearray;
	private String userType =null;

	public void initArray(int size) {
		mybytearray = new byte[size];
	}

	public MyFile(String studentID, String examID, String examType) {

		this.studentID = studentID;
		this.examID = examID;
		this.examType = examType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getMybytearray() {
		return mybytearray;
	}

	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	public void setMybytearray(byte[] mybytearray) {

		for (int i = 0; i < mybytearray.length; i++)
			this.mybytearray[i] = mybytearray[i];
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

	public String getExamDate() {
		return examDate;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	
	public String getUserType(){
		return userType;
	}
	
	public void setUserType(String userType) {
		this.userType=userType;
	}
}
