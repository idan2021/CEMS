package entity;

public class Student extends User {

	public Student(String id, String firstName, String lastName, String userName, String password, String email,
			String role) {
		super(id, firstName, lastName, userName, password, email);
		this.role = Role.student;
	}
}
