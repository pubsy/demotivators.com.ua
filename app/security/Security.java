package security;

import controllers.Secure;
import models.User;

public class Security extends Secure.Security{

	static boolean authenticate(String username, String password) {
        User user = User.find("byEmail", username).first();
        return user != null && user.getPassword().equals(password);
    }
}
