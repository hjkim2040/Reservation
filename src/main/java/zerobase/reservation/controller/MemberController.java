package zerobase.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.reservation.dto.MemberDto;
import zerobase.reservation.dto.ReservationDto;
import zerobase.reservation.dto.ReviewDto;
import zerobase.reservation.service.MemberService;

@RequestMapping("/customer")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    /**
     * 이용자 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid MemberDto.RegisterRequest request) {

        return ResponseEntity.ok(memberService.register(request));
    }

    /**
     * 이용자 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid MemberDto.LoginRequest request) {

        return ResponseEntity.ok(memberService.authenticate(request));
    }

    /**
     * 매장 예약
     */
    @PostMapping("/store/reserve")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> addReserve(
            @RequestHeader("Authorization") String token,
            @RequestBody ReservationDto request) {

        return ResponseEntity.ok(memberService.addReservation(token, request));

    }

    /**
     * 리뷰작성
     */
    @PostMapping("/store/review")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> addReview(
            @RequestHeader("Authorization") String token,
            @RequestBody ReviewDto.Request request
    ) {
        return ResponseEntity.ok(memberService.addReview(request, token));
    }
    /**
     * 리뷰 수정
     */
    @PutMapping("/store/review/{reviewId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ReviewDto.Response> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDto.Request request,
            @RequestHeader("Authorization") String token
    ) {
        ReviewDto.Response updatedReview = memberService.updateReview(reviewId, request, token);
        return ResponseEntity.ok(updatedReview);
    }
    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/store/review/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token
    ) {
        memberService.deleteReview(reviewId, token);
        return ResponseEntity.ok().build();
    }
}
