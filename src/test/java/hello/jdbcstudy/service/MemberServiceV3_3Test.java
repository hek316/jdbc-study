package hello.jdbcstudy.service;

import hello.jdbcstudy.domain.Member;
import hello.jdbcstudy.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbcstudy.connection.ConnectionsConst.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
@SpringBootTest
// 통합 테스트를 위해 실제 Spring ApplicationContext 를 로딩하고
// 테스트 클래스에 필요한 스프링 빈들을 주입(@Autowired) 받을 수 있게 함
// @TestConfiguration 과 같은 내부 설정 클래스도 함께 적용됨.
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;

    @Autowired
    private MemberServiceV3_3 memberService;

    @Autowired
    private MemberServiceV3_3 memberServiceV3_3;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepository() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberService() {
            return new MemberServiceV3_3(memberRepository());
        }
    }


    @AfterEach
    void afterEach() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    void AopCheck() {
        log.info(" memberServiceV3_3 class = {}",  memberServiceV3_3.getClass());
        log.info(" memberRepository class = {}",  memberRepository.getClass());
        org.assertj.core.api.Assertions.assertThat(AopUtils.isAopProxy(memberServiceV3_3)).isTrue();
        org.assertj.core.api.Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10_000);
        Member memberB = new Member(MEMBER_B, 20_000);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 10_000);

        //then
        Member findMamberA = memberRepository.findById(memberA.getMemberId());
        Member findMamberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertEquals(findMamberA.getMoney(), 0);
        Assertions.assertEquals(findMamberB.getMoney(), 30_000);

    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10_000);
        Member memberEx = new Member(MEMBER_EX, 20_000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then memberA 의 톤이 롤백되어야 함
        Member findMamberA = memberRepository.findById(memberA.getMemberId());
        Member findMamberEx = memberRepository.findById(memberEx.getMemberId());
        Assertions.assertEquals(findMamberA.getMoney(), 10_000);
        Assertions.assertEquals(findMamberEx.getMoney(), 20_000);
    }

}