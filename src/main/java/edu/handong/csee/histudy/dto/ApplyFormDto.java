package edu.handong.csee.histudy.dto;

import edu.handong.csee.histudy.domain.Choice;
import edu.handong.csee.histudy.domain.Friendship;
import edu.handong.csee.histudy.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplyFormDto {

    @Schema(description = "List of friend added to apply form", type = "array")
    private List<UserDto.UserBasic> friends;

    @Schema(description = "List of course added to apply form", type = "array")
    private List<CourseDto.CourseInfo> courses;

    public ApplyFormDto(User entity) {
        this.friends = entity.getFriendships()
                .stream()
                .map(Friendship::getReceived)
                .map(UserDto.UserBasic::new)
                .toList();
        this.courses = entity.getChoices()
                .stream()
                .map(Choice::getCourse)
                .map(CourseDto.CourseInfo::new)
                .toList();
    }
}
