package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import zerobase.reservation.domain.Member;
import zerobase.reservation.domain.Reservation;
import zerobase.reservation.domain.Review;
import zerobase.reservation.dto.MemberDto;
import zerobase.reservation.dto.ReservationDto;
import zerobase.reservation.dto.ReviewDto;
import zerobase.reservation.exception.MemberException;
import zerobase.reservation.repository.MemberRepository;
import zerobase.reservation.repository.ReservationRepository;
import zerobase.reservation.repository.ReviewRepository;
import zerobase.reservation.repository.StoreRepository;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.type.Authority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static zerobase.reservation.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ReservationRepository reserveRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;


    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        return this.memberRepository.findByMail(mail)
                .orElseThrow(RuntimeException::new);
    }

    /**
     * 이용자 회원가입
     */
    @Transactional
    public MemberDto.RegisterResponse register(MemberDto.RegisterRequest member) {
        validate(member);

        member.setRole(String.valueOf(Authority.ROLE_MEMBER));
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return Member.toResponse(memberRepository.save(member.toEntity()));
    }

    /**
     * 회원가입 유효한지 확인
     */
    private void validate(MemberDto.RegisterRequest member) {
        boolean exists = memberRepository.existsByMail(member.getMail());
        if (exists) {
            throw new MemberException(USER_DUPLICATED);
        }
    }

    /**
     * 로그인 유효한지 확인
     */
    public String authenticate(MemberDto.LoginRequest member) {
        var user = memberRepository.findByMail(member.getMail())
                .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new MemberException(PASSWORD_UNMATCHED);
        }
        return this.tokenProvider.generateToken(member.getMail(), user.getRole());
    }

    /**
     * 매장 예약
     */
    @Transactional
    public ReservationDto addReservation(String token, ReservationDto reserve) {
        //회원 존재하는지 확인
        Member member = this.getMemberEntity(token);

        //매장 존재하는지 확인
        var store = storeRepository.findByStoreName(reserve.getStoreName())
                .orElseThrow(() -> new MemberException(STORE_NOT_FOUND));

        //예약이 가능한지 확인 (동시간에 예약이 5건이면 안 됨)
        if (reserveRepository.countByReservedAt(reserve.getReservedAt()) == 5) {
            throw new MemberException(RESERVE_IS_FULL);
        }

        //해당 회원 예약 중에 같은 매장에 예약한 게 있고 방문이 되지 않았으면 안 됨
        Optional<List<Reservation>> reserveStore = reserveRepository.findByStore(store);
        if (reserveStore.isPresent() && reserveStore.get().size() > 0) {
            List<Reservation> reserveList = reserveStore.get();
            reserveList.stream().forEach(item -> {
                if (Objects.equals(item.getStore().getId(), store.getId()) &&  !item.isVisited()) {
                    throw new MemberException(RESERVE_DUPLICATED);
                }
            });
        }

        String reservationNum = this.getReserveNum(member.getId(), reserve.getReservedAt(), store.getId());

        reserveRepository.save(
                Reservation.builder()
                        .reservationNum(reservationNum)
                        .reservedAt(reserve.getReservedAt())
                        .member(member)
                        .store(store)
                        .build()
        );
        return ReservationDto.builder()
                .storeName(reserve.getStoreName())
                .reservedAt(reserve.getReservedAt())
                .reservationNum(reservationNum)
                .build();
    }

    /**
     * email로 해당 회원 Entity를 찾는 메소드
     * - private 메소드 getMailFromToken 이용해서 mail을 가져와서
     *  manager 테이블에 있는지 확인.
     */
    private Member getMemberEntity(String token) {
        String mail = getMailFromToken(token);

        Optional<Member> optionalMember = memberRepository.findByMail(mail);
        if (optionalMember.isEmpty()) {
            throw new MemberException(MEMBER_NOT_FOUND);
        }

        return optionalMember.get();
    }

    /**
     * 토큰에서 email 꺼내오는 메소드
     * - TokenProvider의 getMail 메소드를 이용해서 토큰에서 mail값을 꺼내옴.
     */

    private String getMailFromToken(String token) {
        if (!ObjectUtils.isEmpty(token) && token.startsWith("Bearer")) {
            token =  token.substring("Bearer".length());
        }
        return tokenProvider.getMail(token);
    }

    /**
     * 예약번호 발급
     * 회원 아이디 + 예약 년월일 + 매장 아이디를 붙여서 반환
     */
    private String getReserveNum(Long memberId, LocalDateTime reservedAt, Long storeId) {
        return memberId +
                Integer.toString(reservedAt.getYear()) +
                reservedAt.getMonth() +
                reservedAt.getDayOfMonth() +
                storeId;
    }

    /**
     * 리뷰 작성
     */
    @Transactional
    public ReviewDto.Response addReview(ReviewDto.Request review, String token) {
        //token으로 회원확인
        Member member = this.getMemberEntity(token);

        //존재하는 예약인지 확인
        Reservation reservation = reserveRepository.findByReservationNum(review.getReservationNum())
                .orElseThrow(() -> new MemberException(RESERVE_NOT_FOUND));
        //해당 예약건에 예약한 사람이 맞는지 확인
        if (!Objects.equals(member.getId(), reservation.getMember().getId())) {
            throw new MemberException(UNMATCHED_MEMBER_RESERVE);
        }
        //방문 여부가 true인지 확인
        validateVisited(reservation);

        Review savedReview = reviewRepository.save(
                Review.builder()
                        .store(reservation.getStore())
                        .member(reservation.getMember())
                        .text(review.getText())
                        .build()
        );
        return ReviewDto.Response.builder()
                .storeName(savedReview.getStore().getStoreName())
                .memberName(savedReview.getMember().getName())
                .text(savedReview.getText())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }
    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewDto.Response updateReview(Long reviewId, ReviewDto.Request reviewRequest, String token) {
        Member member = this.getMemberEntity(token);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new MemberException(REVIEW_NOT_FOUND));

        if (!Objects.equals(member.getId(), review.getMember().getId())) {
            throw new MemberException(UNMATCHED_MEMBER_REVIEW);
        }

        review.setText(reviewRequest.getText());

        Review updatedReview = reviewRepository.save(review);
        return ReviewDto.Response.builder()
                .storeName(updatedReview.getStore().getStoreName())
                .memberName(updatedReview.getMember().getName())
                .text(updatedReview.getText())
                .updatedAt(updatedReview.getUpdatedAt())
                .build();
    }
    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, String token) {
        Member member = this.getMemberEntity(token);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new MemberException(REVIEW_NOT_FOUND));

        if (!Objects.equals(member.getId(), review.getMember().getId()) &&
        !member.getRole().equals("ROLE_MANAGER")) {
            throw new MemberException(UNMATCHED_MEMBER_REVIEW);
        }

        reviewRepository.delete(review);
    }

    /**
     * 리뷰 작성하기 위해 방문했는지 확인
     * - 방문여부가 false이면 예외 발생.
     */
    private void validateVisited(Reservation reservation) {
        if (!reservation.isVisited()) {
            throw new MemberException(REVIEW_NOT_ALLOWED);
        }
    }
}
