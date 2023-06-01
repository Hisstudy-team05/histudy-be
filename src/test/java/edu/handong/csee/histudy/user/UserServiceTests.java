package edu.handong.csee.histudy.user;

import edu.handong.csee.histudy.domain.*;
import edu.handong.csee.histudy.dto.UserDto;
import edu.handong.csee.histudy.interceptor.AuthenticationInterceptor;
import edu.handong.csee.histudy.repository.ChoiceRepository;
import edu.handong.csee.histudy.repository.CourseRepository;
import edu.handong.csee.histudy.repository.TeamRepository;
import edu.handong.csee.histudy.repository.UserRepository;
import edu.handong.csee.histudy.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class UserServiceTests {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ChoiceRepository choiceRepository;
    @MockBean
    AuthenticationInterceptor interceptor;

    @BeforeEach
    void setup() throws IOException {
        when(interceptor.preHandle(any(), any(), any())).thenReturn(true);
        Course course = Course.builder()
                .name("기초전자공학실험")
                .code("ECE20007")
                .courseYear(2023)
                .semester(1)
                .build();
        courseRepository.save(course);
        Course courseB = Course.builder()
                .name("데이타구조")
                .code("ECE20010")
                .professor("김호준")
                .courseYear(2023)
                .semester(1)
                .build();
        courseRepository.save(courseB);
        Course courseC = Course.builder()
                .name("자바프로그래밍언어")
                .code("ECE20017")
                .professor("남재창")
                .courseYear(2023)
                .semester(1)
                .build();
        courseRepository.save(courseC);
    }

    @DisplayName("전체 유저 목록을 불러올 수 있어야 한다")
    @Test
    public void userListTest() {
        User userA = User.builder()
                .id("123")
                .sid("22000329")
                .name("배주영")
                .accessToken("1234")
                .email("a@a.com")
                .role(Role.USER)
                .build();
        User userB = User.builder()
                .id("124")
                .sid("22000330")
                .name("오인혁")
                .accessToken("1235")
                .email("a@b.com")
                .role(Role.USER)
                .build();
        User userC = User.builder()
                .id("125")
                .sid("22000332")
                .name("한시온")
                .accessToken("1236")
                .email("a@c.com")
                .role(Role.USER)
                .build();
        User savedA = userRepository.save(userA);
        User savedB = userRepository.save(userB);
        User savedC = userRepository.save(userC);
        savedA.add(savedB);
        savedB.getReceivedRequests().stream().findAny().ifPresent(Friendship::accept);
        List<Long> courseIdxList = List.of(1L,2L);
        List<Course> courses = courseIdxList.stream()
                .map(courseRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        List<Choice> choices = courses.stream().map(c -> choiceRepository.save(Choice.builder()
                .user(savedA)
                .course(c)
                .build())).toList();
        savedA.getChoices().addAll(choices);
        List<Long> courseIdxList2 = List.of(1L,2L,3L);
        List<Course> courses2 = courseIdxList2.stream()
                .map(courseRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        List<Choice> choices2 = courses2.stream().map(c -> choiceRepository.save(Choice.builder()
                .user(savedB)
                .course(c)
                .build())).toList();
        savedB.getChoices().addAll(choices2);
        Team team = teamRepository.save(new Team(111));
        savedA.belongTo(team);
        savedB.belongTo(team);
        List<UserDto.Info> users = userService.getUsers("");
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(3);
        System.out.println("users = " + users);
    }
}