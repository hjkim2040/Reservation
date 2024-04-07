package zerobase.reservation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    USER_DUPLICATED("중복된 회원입니다."),
    USER_NOT_FOUND("해당 아이디에 해당하는 회원이 없습니다."),
    PASSWORD_UNMATCHED("비밀번호가 일치하지 않습니다."),

    MANAGER_NOT_FOUND("해당 점장이 없습니다."),
    MEMBER_NOT_FOUND("해당 회원이 없습니다."),

    STORE_NOT_FOUND("해당 매장이 없습니다."),
    STORE_DUPLICATED("중복된 매장 명입니다."),

    RESERVE_IS_FULL("해당 시간에 예약이 다 찼습니다."),
    RESERVE_NOT_FOUND("예약 건이 없습니다."),
    RESERVE_CANCELED("예약시간 10분 전에 방문하지 않아 예약이 취소되었습니다."),
    RESERVE_NOT_ALLOWED("승인되지 않은 예약 건 입니다."),
    RESERVE_DUPLICATED("같은 매장에 대해 한 건의 예약만 가능합니다."),
    REVIEW_NOT_ALLOWED("방문하지 않은 예약 건에 대해 리뷰 작성이 불가합니다."),

    UNMATCHED_MANAGER_STORE("자신의 매장만 조회할 수 있습니다."),
    UNMATCHED_STORE_RESERVE("해당 매장의 예약건이 아닙니다."),
    UNMATCHED_MEMBER_RESERVE("해당 회원의 예약건이 아닙니다."),
    UNMATCHED_RESERVE_MANAGER("자신의 매장 예약 건만 승인/거절 할 수 있습니다."),

    NO_PERMISSION("권한이 없습니다."),
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다."),
    UNMATCHED_MEMBER_REVIEW("작성자가 일치하지 않습니다.");

    private final String description;
}
