package com.mono.example.test;

import org.assertj.core.api.SoftAssertions;
import com.mono.example.AssertJ.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class AssertTest {

    @Test
    @DisplayName("Chaining Test -String Type")
    void chainingTestString() {
        assertThat("Hello, World! Nice to meet you.")
                .isNotEmpty()
                .contains("Nice") // nice 포함
                .contains("World")
                .doesNotContain("Give Up") // Give Up 포함안됨
                .startsWith("Hello") // 시작 문자열
                .endsWith("you.") // 끝나는 문자열
                .isEqualTo("Hello, World! Nice to meet you."); // 일치하는지
    }

    @Test
    @DisplayName("Chaining Test -Number")
    void chainingTestNumber() {
        assertThat(3.14d)
                .isPositive() // 양수이고
                .isGreaterThan(3) // 3보다 크며
                .isLessThan(4) // 4보다 작습니다.
                .isEqualTo(3, offset(1d))// 오프셋 1기준으로 3과 같고
                .isEqualTo(3.1, offset(0.1d)) // 오프셋 0.1 기준으로 3.1과 같으며
                .isEqualTo(3.14, offset(3.14d))
                .isEqualTo(3.14); // 오프셋 없이는 3.14와 같습니다.
    }

    @Test
    @DisplayName("Chaining Test -Object")
    void chainingTestUser() {
        User user = new User("Hans", 33);

        assertThat(user.getAge())
                .as("check1 %s's age", user.getName())
                .isEqualTo(100);
    }

    // 나이가 같지 않을때 나오는 오류 메시지, 같으면 통과, [check1 Hans's age] as 설명
    //    org.opentest4j.AssertionFailedError: [check1 Hans's age]
    //    expected: 100
    //    but was: 33
    //    필요:100
    //    실제   :33


    @Test
    @DisplayName("Filtering Test with Lambda")
    void lambdaTest() {
        List<User> userList = new ArrayList<>();
        User admin = new User("admin", 30);
        User user0 = new User("user0", 10);
        User user1 = new User("user1", 20);

        userList.add(admin);
        userList.add(user0);
        userList.add(user1);

        // name이 admin을 필터링하고 그 값을 .contains(admin) 확인
        assertThat(userList)
                .filteredOn(user -> user.getName().contains("admin"))
                .contains(admin);

        // age가 30이 아닌 객체가 user0, user1이 맞는지
        assertThat(userList)
                .filteredOn("age", notIn(30))
                .containsOnly(user0, user1);
    }

    @Test
    @DisplayName("Property 추출 테스트")
    void extractPropertyTest() {
        List<User> userList = new ArrayList<>();
        User admin = new User("admin", 30);
        User user0 = new User("user0", 10);
        User user1 = new User("user1", 20);
        userList.add(admin);
        userList.add(user0);
        userList.add(user1);

        assertThat(userList)
                .extracting("name")
                .contains("admin","user0","user1");
    }

    @Test
    @DisplayName("Property 추출 테스트 : 여러 필드를 한 번에 검증")
    void extractPropertyTupleTest() {
        List<User> userList = new ArrayList<>();
        User admin = new User("admin", 30);
        User user0 = new User("user0", 10);
        User user1 = new User("user1", 20);
        userList.add(admin);
        userList.add(user0);
        userList.add(user1);

        assertThat(userList)
                .extracting("name", "age")
                .contains(tuple("admin", 30),
                        tuple("user0", 10),
                        tuple("user1", 20));
    }

    // 보통 테스트를 진행할 때. 하나의 assertThat()이 실패하면 해당 테스트 자체가 중단이 됩니다.
    // 하지만, Soft assertions을 사용한다면, 모든 assertions을 실행한 후 실패 내역만 확인할 수 있습니다.
    @Test
    @DisplayName("soft assertions 테스트")
    void soft_assertions_test() {

        int num =10, num2=20;
        String str ="abc";

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(num).as("테스트1").isLessThan(20); // 20보다 작은지
        softly.assertThat(num2).as("테스트2").isLessThan(5); // 20은 5보다 작지않다. 실패했어도 아래 테스트 수행함
        softly.assertThat(str).as("테스트3").contains("a"); // a 포함
        softly.assertAll(); // 한꺼번에 모두 실행 시켜준다.
    }

    @Test
    @DisplayName("Exception 테스트")
    // 원하는 Exception이 발생하는지 검증
    // Throwable Assertions : 예외 메시지, 스택 추적 검사, 예외가 이미 throw 되었는지 확인

    void exception_test() {

        Throwable throwable = catchThrowable(()-> {
            //throw new IllegalAccessError("exception");
            throw new ArithmeticException("0으로 나눌수 없습니다.");
        });

//        assertThat(throwable).isInstanceOf(IllegalAccessError.class);
//        assertThat(throwable).hasMessage("exception");
        assertThat(throwable).isInstanceOf(ArithmeticException.class);
        assertThat(throwable).hasMessage("0으로 나눌수 없습니다.");
    }

    @Test
    @DisplayName("StringIndexOutOfBoundsException 테스트")
    // assertThatThrownBy()라는 예외처리를 가독성 있게 테스트할 수 있는 함수가 제공
    // charAt() 함수는 문자열에서 특정 인덱스에 위치하는 유니코드 단일문자를 반환합니다.
    void exception_test1() {
        //given
        String input = "abc";
        System.out.println(input.charAt(input.length()-1));
        /*
        * -> 에러 메시지가 발생된다.
        * */
        // when, then
        assertThatThrownBy(()-> input.charAt(input.length())) // 길이의 범위를 넘어서는...
                .isInstanceOf(StringIndexOutOfBoundsException.class)
                .hasMessageContaining("String index out of range")
                .hasMessageContaining(String.valueOf(input.length()));
    }

    @Test
    @DisplayName("NullPoint 예외 처리")
    void nullPoint() {
        assertThatNullPointerException().isThrownBy(() -> {
                    throw new NullPointerException("null!");
                }).withMessage("%s!", "null")
                .withMessageContaining("nu")
                .withNoCause(); // exception의 에러의 원인을 제공하지 않음

//        Throwable th1 = new ArithmeticException("aaaa");
//        Throwable th2 = th1.getCause();
    }

    @Test
    @DisplayName("잘못된 Argument를 넣었을때")
    void illegalArgs() {
        assertThatIllegalArgumentException().isThrownBy(()-> {
            throw new IllegalArgumentException("Illegal args!");
        })
                .withMessage("%s!", "Illegal args")
                .withMessageContaining("Il")
                .withNoCause();

        // throw new IllegalArgumentException("Illegal args!");
    }

    // 예외처리 안적음
    @Test
    @DisplayName("IO 예외 처리")
    void io() {
        assertThatIOException().isThrownBy(() -> {
            throw new IOException("IO!");
        }) // 오류를 발생시킴킴
                .withMessage("%s!", "IO")
                .withMessageContaining("I")
                .withNoCause();
    }

    @Test
    @DisplayName("BDD 스타일-exception")
    // 조금 더 가독성 있게 작성
    // 준비 실행 검증
    void exception_assertion_example() {
        // given 준비
        // some preconditions

        // when 실행
        Throwable thrown = catchThrowable(() -> { throw new Exception("boom!"); });

        // then 검증
        assertThat(thrown).isInstanceOf(Exception.class)
                .hasMessageContaining("boom");
    }

}