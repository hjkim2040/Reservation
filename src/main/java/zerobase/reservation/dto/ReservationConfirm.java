package zerobase.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReservationConfirm {
    private Long reservationId;
    private Long storeId;
    private boolean confirmYn;
}
