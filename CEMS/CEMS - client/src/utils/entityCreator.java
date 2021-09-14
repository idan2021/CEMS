package utils;

import entity.Principal;
import entity.Student;
import entity.Teacher;
import entity.User;

/**
 * <p>
 * <b>Explanation:</b> The class tools for creating objects more easily for
 * better reading and understanding
 * <p>
 * 
 * @author DvirVahav
 */
public class entityCreator {

	/**
	 * if login successful, receives the line from DB and save into connectedClients
	 * arrayList which is located in ServerHanlder for accessing data more
	 * easily(instead of addressing DB every time.
	 */


	public static Teacher teacherCreator(String[] str) {
		return new Teacher(str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
	}

	public static Student studentCreator(String[] str) {
		return new Student(str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
	}

	public static Principal principalCreator(String[] str) {
		return new Principal(str[1], str[2], str[3], str[4], str[5], str[6], str[7]);
	}

}
