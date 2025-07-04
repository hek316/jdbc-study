package hello.jdbcstudy.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {

    @Test
    void connect() {
        Connection conn = DBConnectionUtil.getConnection();
        assertNotNull(conn);
    }

}