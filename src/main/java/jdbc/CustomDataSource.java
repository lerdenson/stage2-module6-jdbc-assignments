package jdbc;

import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;
    private Connection connection = null;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            Properties appProps = new Properties();
            try {
                appProps.load(new FileInputStream("src/main/resources/app.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            instance = new CustomDataSource(
                    appProps.getProperty("postgres.driver"),
                    appProps.getProperty("postgres.url"),
                    appProps.getProperty("postgres.password"),
                    appProps.getProperty("postgres.name")
            );
        }

        return instance;
    }

    @Override
    public Connection getConnection(String username, String password) {
        if (connection == null) {

            try {
                Class.forName(driver);
                connection = new CustomConnector().getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


    @Override
    public PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {

    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public void setLoginTimeout(int seconds) {

    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                Class.forName(driver);
                connection = new CustomConnector().getConnection(url, name, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}
