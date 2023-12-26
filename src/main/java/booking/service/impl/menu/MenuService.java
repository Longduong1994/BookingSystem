package booking.service.impl.menu;

import booking.dto.request.MenuRequest;
import booking.dto.request.UpdateMenuRequest;
import booking.dto.response.MenuResponse;
import booking.entity.*;
import booking.exception.AccessDeniedException;
import booking.exception.AddMenuException;
import booking.exception.LoginException;
import booking.exception.NotFoundException;
import booking.repository.DishRepository;
import booking.repository.MenuRepository;
import booking.repository.ReservationRepository;
import booking.security.user_principle.UserPrincipal;
import booking.service.mapper.MenuMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MenuService implements IMenuService{
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final ReservationRepository reservationRepository;
    private final DishRepository dishRepository;
    @Override
    public Page<MenuResponse> findAll(String code, Long id,int page, int size) {
        return menuRepository.findAllByReservationCodeOrReservationId(code,id, PageRequest.of(page, size)).map(menuMapper::toResponse);
    }

    @Override
    public MenuResponse addToReservation(MenuRequest menuRequest, Authentication authentication) throws LoginException, NotFoundException, AddMenuException {
        Reservation reservation = reservationRepository.findById(menuRequest.getReservationId()).get();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal == null) {
            throw new LoginException("You need to login to use service");
        }
        if (reservation==null) {
            throw new NotFoundException("Reservation not found");
        }
        if (!reservation.getUser().equals(userPrincipal.getUser())){
            throw new AddMenuException("You cannot add menus to this table");
        }
        if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)|| reservation.getReservationStatus().equals(ReservationStatus.CANCEL) ){
            throw new AddMenuException("Order has been completed");
        }
        Dish dish = dishRepository.findById(menuRequest.getDishId()).get();
        if (dish==null) {
            throw new NotFoundException("Dish not found");
        }
        List<Menu> menus = menuRepository.findAllByReservationId(menuRequest.getReservationId());
        Menu menu = menuMapper.toEntity(menuRequest);
        if (menus.isEmpty()) {
            menu.setDish(dish);
            menu.setReservation(reservation);
            menu.setMenuStatus(MenuStatus.NOT_SERVED);
            return menuMapper.toResponse(menuRepository.save(menu));
        }

        for (Menu m : menus) {
            if (m.getDish().getId().equals(menuRequest.getDishId())) {
                m.setQuantityOrdered(menuRequest.getQuantity()+m.getQuantityOrdered());
                return menuMapper.toResponse(menuRepository.save(m));
            }
        }

        return menuMapper.toResponse(menu);
    }

    @Override
    public MenuResponse updateMenu(Long id, UpdateMenuRequest updateMenuRequest, Authentication authentication) throws NotFoundException, AccessDeniedException {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found menu"));


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (!menu.getReservation().getUser().equals(userPrincipal.getUser())) {
            throw new AccessDeniedException("You do not have permission to update this menu");
        }
        Long reservationId = menu.getReservation().getId();

        if (!reservationId.equals(updateMenuRequest.getReservationId())) {
            throw new AccessDeniedException("Menu does not belong to the specified reservation");
        }

        menu.setQuantityOrdered(updateMenuRequest.getQuantity());
        return menuMapper.toResponse(menu);
    }


    @Override
    public String Delete(Long id) {
        return null;
    }
}
