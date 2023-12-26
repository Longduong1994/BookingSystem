package booking.service.mapper;

import booking.dto.request.ReservationRequest;
import booking.dto.response.ReservationResponse;
import booking.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReservationMapper {

    Reservation toEntity(ReservationRequest reservationRequest);
    @Mapping(target = "orderBy" ,source = "reservation.user.username")
    @Mapping(target = "tableName", source = "reservation.table.tableName")
    ReservationResponse toResponse(Reservation reservation);
}
