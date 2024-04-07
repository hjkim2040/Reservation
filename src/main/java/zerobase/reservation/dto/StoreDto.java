package zerobase.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zerobase.reservation.domain.Store;

import java.time.LocalDateTime;

public class StoreDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddStoreRequest{
        @NotBlank
        private String storeName;

        private String location;
        private String description;

        private Long managerId;
        public Store toEntity() {
            return Store.builder()
                    .storeName(this.getStoreName())
                    .location(this.getLocation())
                    .description(this.getDescription())
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStoreRequest {
        private String storeName;
        private String location;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreResponse {
        private String storeName;
        private String location;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchStoreResponse{
        private String storeName;
        private String location;
        private String description;
    }
}
