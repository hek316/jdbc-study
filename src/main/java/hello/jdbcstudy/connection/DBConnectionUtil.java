package hello.jdbcstudy.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbcstudy.connection.ConnectionsConst.*;


@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {

            // 데이터베이스 연결을 위해 JDBC 가 제공하는 DriverManager.getConnection() - 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해줌
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
