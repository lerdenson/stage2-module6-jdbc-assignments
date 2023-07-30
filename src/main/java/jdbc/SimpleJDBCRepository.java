package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private static final String createUserSQL = "INSERT INTO MYUSERS(FIRSTNAME, LASTNAME, AGE) VALUES(?, ?, ?)";
    private static final String updateUserSQL = "UPDATE MYUSERS SET FIRSTNAME=?, LASTNAME=?, AGE=? WHERE ID=?";
    private static final String deleteUser = "DELETE FROM MYUSERS WHERE ID=?";
    private static final String findUserByIdSQL = "SELECT * FROM MYUSERS WHERE ID=?";
    private static final String findUserByNameSQL = "SELECT * FROM MYUSERS WHERE FIRSTNAME=?";
    private static final String findAllUserSQL = "SELECT * FROM MYUSERS";
    private PreparedStatement ps = null;
    private Statement st = null;

    public Long createUser(User user) {
        Long id = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public User findUserById(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            return new User(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getInt(4)
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public User findUserByName(String userName) {
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            return new User(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getInt(4)
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(findAllUserSQL);
            while (rs.next()) {
                users.add(new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findUserById(user.getId());
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection()){
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
