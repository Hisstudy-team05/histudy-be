package edu.handong.csee.histudy.dto;

import edu.handong.csee.histudy.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportDto {

    @Schema(description = "List of reports", type = "array")
    private List<ReportInfo> reports;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReportInfo {

        public ReportInfo(Report entity) {
            this.id = entity.getId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.totalMinutes = entity.getTotalMinutes();
            this.participants = entity.getParticipants().stream()
                    .map(Participates::getUser)
                    .map(UserDto.UserBasic::new)
                    .toList();
            this.courses = entity.getStudies().stream()
                    .map(Study::getCourse)
                    .map(Course::getName)
                    .toList();
            this.images = entity.getImages().stream()
                    .map(ImageDto::new)
                    .toList();
            this.regDate = entity.getLastModifiedDate().toString();
        }

        @Schema(description = "Report ID", type = "number", example = "1")
        private Long id;

        @Schema(description = "Report Title", example = "Week 15 Report")
        private String title;

        @Schema(description = "Report Content", example = "This is a report for week 15")
        private String content;

        @Schema(description = "Total minutes of the report", type = "number", example = "60")
        private long totalMinutes;

        @Schema(description = "Participant SIDs of the report", type = "array")
        private List<UserDto.UserBasic> participants;

        @Schema(description = "Course names of the report", type = "array", example = "[\"OOP\", \"OS\"]")
        private List<String> courses;

        @Schema(description = "Images of the report", type = "array")
        private List<ImageDto> images;

        @Schema(description = "Report Last Modified Date", example = "2021-06-01 00:00:00")
        private String regDate;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReportBasic {
        public ReportBasic(Report report) {
            this.id = report.getId();
            this.title = report.getTitle();
            this.regDate = report.getLastModifiedDate().toString();
            this.totalMinutes = report.getTotalMinutes();
            this.thumbnail = report.getImages()
                    .stream()
                    .findFirst()
                    .map(Image::getPath)
                    .orElse(null);
        }

        @Schema(description = "Report ID", type = "number", example = "1")
        private long id;

        @Schema(description = "Report Title", example = "Week 15 Report")
        private String title;

        @Schema(description = "Report Last Modified Date", example = "2021-06-01 00:00:00")
        private String regDate;

        @Schema(description = "Total minutes of the report", type = "number", example = "60")
        private long totalMinutes;

        @Schema(description = "Thumbnail of the report", example = "https://histudy.s3.ap-northeast-2.amazonaws.com/2021-06-01-00-00-00-1")
        private String thumbnail;
    }
}
