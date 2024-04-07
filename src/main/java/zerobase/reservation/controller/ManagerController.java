package zerobase.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.reservation.dto.ManagerDto;
import zerobase.reservation.dto.ReservationConfirm;
import zerobase.reservation.dto.StoreDto;
import zerobase.reservation.service.ManagerService;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    /**
     * 점장 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid ManagerDto.RegisterRequest request) {
        return ResponseEntity.ok(managerService.register(request));
    }

    /**
     * 점장 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid ManagerDto.LoginRequest request) {
        return ResponseEntity.ok(this.managerService.authenticate(request));
    }

    /**
     * 매장 등록
     */
    @PostMapping("/store/add")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> addStore(@RequestBody @Valid StoreDto.AddStoreRequest store,
                                      @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(managerService.addStore(store, token));
    }
    /**
     * 매장 수정
     */
    @PutMapping("/store/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateStore(@PathVariable Long storeId, @RequestBody @Valid StoreDto.UpdateStoreRequest store,
                                      @RequestHeader("Authorization") String token) {

        StoreDto.StoreResponse updatedStore = managerService.updateStore(storeId, store, token);
        return ResponseEntity.ok(updatedStore);
    }
    /**
     * 매장 삭제
     */
    @DeleteMapping("/store/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteStore(@PathVariable Long storeId,
                                      @RequestHeader("Authorization") String token) {

        managerService.deleteStore(storeId, token);
        return ResponseEntity.ok().build();
    }

    /**
     * 내 매장 찾기
     */
    @GetMapping("/store/search")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> searchStore(@RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(managerService.searchStore(token));
    }

    /**
     * 매장에 등록된 예약확인
     */
    @GetMapping("/reserve/search/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> searchReserve(
            @PathVariable Long storeId,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(managerService.searchReservation(storeId, token));
    }

    /**
     * 예약 승인/거절
     */
    @PatchMapping("/reservation/confirm")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> confirmReserve(
            @RequestHeader("Authorization") String token,
            @RequestBody ReservationConfirm reservationConfirm
    ) {
        return ResponseEntity.ok(managerService.confirmReservation(reservationConfirm, token)
                + "번 예약을 " + reservationConfirm.isConfirmYn() + " 하였습니다.");
    }
}
