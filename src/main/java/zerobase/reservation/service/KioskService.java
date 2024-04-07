package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.reservation.domain.Reservation;
import zerobase.reservation.exception.ReservationException;
import zerobase.reservation.repository.ReservationRepository;

import java.time.LocalDateTime;

import static zerobase.reservation.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KioskService {
    private final ReservationRepository reservationRepository;

    /**
     * 예약 방문 확인
     */
    public String confirmReservation(String reservationNum) {
        Reservation reservation = reservationRepository.findByReservationNum(reservationNum)
                .orElseThrow(() -> new ReservationException(RESERVE_NOT_FOUND));

        validate(reservation);

        reservation.setVisited(true);
        Reservation save = reservationRepository.save(reservation);
        return save.getReservationNum() + " 방문확인 되었습니다.";
    }

    /**
     * 예약 방문 확인 시 유효한지 확인
     */
    private void validate(Reservation reservation) {
        if (LocalDateTime.now().isAfter(reservation.getReservedAt().plusMinutes(10))) {
            throw new ReservationException(RESERVE_CANCELED);
        }

        if (!reservation.isConfirm()) {
            throw new ReservationException(RESERVE_NOT_ALLOWED);
        }
    }
}
