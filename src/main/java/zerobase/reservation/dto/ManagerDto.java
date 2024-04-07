package zerobase.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.reservation.domain.Manager;

import java.time.LocalDateTime;

public class ManagerDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String mail;
        @NotBlank
        private String password;

        private String role;

        public Manager toEntity() {
            return Manager.builder()
                    .name(this.getName())
                    .password(this.getPassword())
                    .mail(this.getMail())
                    .role(this.getRole())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RegisterResponse {
        private String mail;
        private LocalDateTime registeredAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String mail;
        @NotBlank
        private String password;
    }
}
