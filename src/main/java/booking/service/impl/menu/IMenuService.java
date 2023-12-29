package booking.service.impl.menu;

import booking.dto.request.MenuRequest;
import booking.dto.request.ServedMenuDto;
import booking.dto.request.UpdateMenuRequest;
import booking.dto.response.MenuResponse;
import booking.dto.response.StatisticsDto;
import booking.entity.Reservation;
import booking.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.Date;

public interface IMenuService {
     Page<MenuResponse> findAll(String code ,Long id,int page,int size);

     MenuResponse addToReservation(MenuRequest menuRequest, Authentication authentication) throws LoginException, NotFoundException, AddMenuException, AccessDeniedException;

     MenuResponse updateMenu(Long id, UpdateMenuRequest updateMenuRequest, Authentication authentication) throws NotFoundException, AccessDeniedException;

     String delete(Authentication authentication,Long id,Long reservationId) throws LoginException, NotFoundException, AccessDeniedException, OutOfDateException;

     String clear(Authentication authentication,Long id) throws LoginException, EmptyException, NotFoundException, OutOfDateException, AccessDeniedException;

     MenuResponse served( ServedMenuDto servedMenuDto) throws NotFoundException, InvalidException, OverCapacityException, NoSuchElementException;

     StatisticsDto statisticsByDate(Date date);

     double totalPriceReservation(Long id) throws NotFoundException;
}
