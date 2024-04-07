package zerobase.reservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.reservation.dto.ErrorResponse;

import static zerobase.reservation.type.ErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleUserException(MemberException e) {
        log.error("{} is occurred from {}", e.getErrorCode(), e.getClass());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
    @ExceptionHandler(ManagerException.class)
    public ErrorResponse handleManagerException(ManagerException e) {
        log.error("{} is occurred from {}", e.getErrorCode(), e.getClass());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ReservationException.class)
    public ErrorResponse handleReserveException(ReservationException e) {
        log.error("{} is occurred from {}", e.getErrorCode(), e.getClass());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(ReviewException.class)
    public ErrorResponse handleReviewException(ReviewException e) {
        log.error("{} is occurred from {}", e.getErrorCode(), e.getClass());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(StoreException.class)
    public ErrorResponse handleStoreException(StoreException e) {
        log.error("{} is occurred from {}", e.getErrorCode(), e.getClass());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error ("Exception is occurred.", e);

        return new ErrorResponse(INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }
}
