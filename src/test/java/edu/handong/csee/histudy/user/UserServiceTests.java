package edu.handong.csee.histudy.user;

import edu.handong.csee.histudy.controller.form.ApplyForm;
import edu.handong.csee.histudy.controller.form.ReportForm;
import edu.handong.csee.histudy.domain.*;
import edu.handong.csee.histudy.dto.ReportDto;
import edu.handong.csee.histudy.dto.UserDto;
import edu.handong.csee.histudy.interceptor.AuthenticationInterceptor;
import edu.handong.csee.histudy.repository.*;
import edu.handong.csee.histudy.service.ReportService;
import edu.handong.csee.histudy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("dev")
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
    @Autowired
    ReportService reportService;
    @MockBean
    AuthenticationInterceptor interceptor;
    @Autowired
    private ReportRepository reportRepository;

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
                .sid("22000329")
                .name("배주영")
                .email("a@a.com")
                .role(Role.USER)
                .build();
        User userB = User.builder()
                .sid("22000330")
                .name("오인혁")
                .email("a@b.com")
                .role(Role.USER)
                .build();
        User userC = User.builder()
                .sid("22000332")
                .name("한시온")
                .email("a@c.com")
                .role(Role.USER)
                .build();
        User savedA = userRepository.save(userA);
        User savedB = userRepository.save(userB);
        User savedC = userRepository.save(userC);
        savedA.add(List.of(savedB));
        savedB.getFriendships().stream().findAny().ifPresent(Friendship::accept);
        List<Long> courseIdxList = List.of(1L, 2L);
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
        List<Long> courseIdxList2 = List.of(1L, 2L, 3L);
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
        ReportForm form = ReportForm.builder()
                .title("title")
                .content("content")
                .totalMinutes(60L)
                .participants(List.of("22000329"))
                .courses(List.of(1L, 2L, 3L))
                .build();
        ReportDto.ReportInfo report = reportService.createReport(form, "a@a.com");
        List<UserDto.UserInfo> users = userService.getUsers("");
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isEqualTo(3);
        List<Report> reports = reportRepository.findAll();
        System.out.println("reports = " + reports);
        System.out.println("users = " + users);
        assertThat(users.get(0).getTotalMinutes()).isEqualTo(60L);
        System.out.println("users = " + users);
    }

    @DisplayName("신청한 유저들의 리스트를 받을 수 있어야 한다")
    @Test
    public void testAppliedUsersTest() {
        User userA = User.builder()
                .sid("22000329")
                .name("배주영")
                .email("a@a.com")
                .role(Role.USER)
                .build();
        User userB = User.builder()
                .sid("22000330")
                .name("오인혁")
                .email("a@b.com")
                .role(Role.USER)
                .build();
        User userC = User.builder()
                .sid("22000332")
                .name("한시온")
                .email("a@c.com")
                .role(Role.USER)
                .build();
        User savedA = userRepository.save(userA);
        User savedB = userRepository.save(userB);
        User savedC = userRepository.save(userC);
        savedA.add(List.of(savedB));
        savedB.getFriendships().stream().findAny().ifPresent(Friendship::accept);
        List<Long> courseIdxList = courseRepository.findAll().stream().map(Course::getId).toList();
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
        List<Long> courseIdxList2 = courseRepository.findAll().stream().map(Course::getId).toList();
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
        List<UserDto.UserInfo> users = userService.getAppliedUsers();
        assertThat(users.size()).isEqualTo(2);
        System.out.println("users = " + users);
    }

    @DisplayName("유저의 신청서를 지울 수 있어야 한다")
    @Test
    public void deleteFormTest() {
        User userA = User.builder()
                .sid("22000329")
                .name("배주영")
                .email("a@a.com")
                .role(Role.USER)
                .build();
        User userB = User.builder()
                .sid("22000330")
                .name("오인혁")
                .email("a@b.com")
                .role(Role.USER)
                .build();
        User userC = User.builder()
                .sid("22000332")
                .name("한시온")
                .email("a@c.com")
                .role(Role.USER)
                .build();
        User savedA = userRepository.save(userA);
        User accepted = userRepository.save(userB);
        User pending = userRepository.save(userC);

        ApplyForm form = ApplyForm.builder()
                .friendIds(List.of(accepted.getSid(), pending.getSid()))
                .courseIds(List.of(1L, 1L, 2L))
                .build();
        ApplyForm form2 = ApplyForm.builder()
                .friendIds(List.of(savedA.getSid()))
                .courseIds(List.of(2L, 2L, 2L))
                .build();
        userService.apply(form, savedA.getEmail());
        userService.apply(form2, accepted.getEmail());
        assertThat(accepted.getFriendships()
                .stream()
                .filter(Friendship::isAccepted)
                .toList()
                .size()
        ).isNotZero();
        userService.deleteUserForm(savedA.getSid());
        assertThat(accepted.getFriendships()
                .stream()
                .filter(Friendship::isAccepted)
                .toList()
                .size()
        ).isZero();
        assertThat(accepted.getFriendships()
                .stream()
                .filter(f -> f.getStatus() == FriendshipStatus.PENDING)
                .toList()
                .size()
        ).isNotZero();
        assertThat(savedA.getChoices().size()).isZero();
    }
    @DisplayName("유저의 정보를 수정할 수 있어야한다")
    @Test
    public void userEditTest() {
        User user = User.builder()
                .sid("22000329")
                .name("배주영")
                .email("a@a.com")
                .role(Role.USER)
                .build();
        User saved = userRepository.save(user);
        Team team = teamRepository.save(new Team(111));
        Team newTeam = teamRepository.save(new Team(222));
        saved.belongTo(team);
        UserDto.UserEdit dto = UserDto.UserEdit.builder()
                                                .id(saved.getId())
                                                .sid("12345678")
                                                .name("조용히해라")
                                                .team(222)
                                                .build();
        UserDto.UserInfo edited = userService.editUser(dto);
        assertThat(edited.getName()).isEqualTo("조용히해라");
        assertThat(edited.getSid()).isEqualTo("12345678");
        assertThat(edited.getGroup()).isEqualTo(newTeam.getTag());
    }
}
