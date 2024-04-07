package zerobase.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Request{
        private String reservationNum;
        private String text;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Response{
        private String memberName;
        private String storeName;
        private String text;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
