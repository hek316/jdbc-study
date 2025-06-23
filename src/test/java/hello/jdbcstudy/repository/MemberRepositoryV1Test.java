package hello.jdbcstudy.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbcstudy.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import static hello.jdbcstudy.connection.ConnectionsConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

       // HikariDataSource dataSource = new HikariDataSource();
       // dataSource.setJdbcUrl(URL);
       // dataSource.setUsername(USERNAME);
       // dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);

    }

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("memberV103", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);



        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}