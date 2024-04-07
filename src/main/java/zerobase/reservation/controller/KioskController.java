package zerobase.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.reservation.service.KioskService;

@RestController
@RequestMapping("/kiosk")
@RequiredArgsConstructor
public class KioskController {
    private final KioskService kioskService;

    /**
     * 키오스크에서 방문 확인
     */
    @PatchMapping("/confirm/{reservationNum}")
    public ResponseEntity<?> confirmReservation(
            @PathVariable String reservationNum
    ) {
        kioskService.confirmReservation(reservationNum);
        return ResponseEntity.ok(kioskService.confirmReservation(reservationNum));
    }
}
