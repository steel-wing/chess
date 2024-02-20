package dataAccess;
import model.UserData;

public class UserDAO {
    public static UserData SELECT(String username) {
        return MemoryUserDAO.SELECT(username);
    }

    public static void INSERT(String username, String password, String email) {
        UserData data = new UserData(username, password, email);
        MemoryUserDAO.INSERT(username, data);
    }

}
