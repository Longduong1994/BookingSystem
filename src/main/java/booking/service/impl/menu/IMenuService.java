package booking.service.impl.menu;

import booking.dto.request.MenuRequest;
import booking.dto.request.UpdateMenuRequest;
import booking.dto.response.MenuResponse;
import booking.exception.AccessDeniedException;
import booking.exception.AddMenuException;
import booking.exception.LoginException;
import booking.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface IMenuService {
     Page<MenuResponse> findAll(String code ,Long id,int page,int size);

     MenuResponse addToReservation(MenuRequest menuRequest, Authentication authentication) throws LoginException, NotFoundException, AddMenuException;

     MenuResponse updateMenu(Long id, UpdateMenuRequest updateMenuRequest, Authentication authentication) throws NotFoundException, AccessDeniedException;

     String Delete(Long id);

}
