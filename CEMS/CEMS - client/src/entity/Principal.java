package entity;

/**
 * <p>
 * <b>Explanation:</b> The Principal class is the subclass of the User class.
 * <p>
 * it contains a single standard constructor that adds a role in addition to the superclass constructor.
 * <p>
 * @author Everyone
 */
public class Principal extends User {

	public Principal(String id, String firstName, String lastName, String userName, String password, String email,
			String role) {
		super(id, firstName, lastName, userName, password, email);
		this.role = Role.principal;
	}
}
