package zerobase.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.reservation.domain.Member;

import java.time.LocalDateTime;

public class MemberDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest{
        private String name;
        private String mail;
        private String password;

        private String role;

        public Member toEntity() {
            return Member.builder()
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
    public static class LoginRequest{
        @NotBlank
        private String mail;
        @NotBlank
        private String password;

    }
}
