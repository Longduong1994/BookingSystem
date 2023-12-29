package booking.service.impl.menu;

import booking.dto.request.MenuRequest;
import booking.dto.request.ServedMenuDto;
import booking.dto.request.UpdateMenuRequest;
import booking.dto.response.MenuResponse;
import booking.dto.response.StatisticsDto;
import booking.entity.*;
import booking.exception.*;
import booking.repository.DishRepository;
import booking.repository.MenuRepository;
import booking.repository.ReservationRepository;
import booking.security.user_principle.UserPrincipal;
import booking.service.impl.user.IUserService;
import booking.service.mapper.MenuMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuService implements IMenuService{
    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final ReservationRepository reservationRepository;
    private final DishRepository dishRepository;
    private final IUserService userService;
    @Override
    public Page<MenuResponse> findAll(String code, Long id,int page, int size) {
        return menuRepository.findAllByReservationCodeOrReservationId(code,id, PageRequest.of(page, size)).map(menuMapper::toResponse);
    }

    @Override
    public MenuResponse addToReservation(MenuRequest menuRequest, Authentication authentication) throws LoginException, NotFoundException, AddMenuException, AccessDeniedException {
        Optional<Reservation> reservation = reservationRepository.findById(menuRequest.getReservationId());
        User user = userService.getUser(authentication);
        if (!reservation.isPresent()) {
            throw new NotFoundException("Reservation not found");
        }
        if (!user.equals(reservation.get().getUser()) && !userService.hasAdminRole(user)){
            throw new AccessDeniedException("You do not have the right to delete");
        }
        if (!reservation.get().getUser().equals(user)){
            throw new AddMenuException("You cannot add menus to this table");
        }
        if (reservation.get().getReservationStatus().equals(ReservationStatus.COMPLETED)|| reservation.get().getReservationStatus().equals(ReservationStatus.CANCEL) ){
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
            menu.setReservation(reservation.get());
            return menuMapper.toResponse(menuRepository.save(menu));
        }

        for (Menu m : menus) {
            if (m.getDish().getId().equals(menuRequest.getDishId())) {
                m.setQuantityOrdered(menuRequest.getQuantity()+m.getQuantityOrdered());
                return menuMapper.toResponse(menuRepository.save(m));
            }
        }

        menu.setDish(dish);
        menu.setReservation(reservation.get());
        return menuMapper.toResponse(menuRepository.save(menu));
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
    public String delete(Authentication authentication, Long id, Long reservationId) throws LoginException, NotFoundException, AccessDeniedException, OutOfDateException {
        User user = userService.getUser(authentication);
        Reservation reservation = reservationRepository.findById(reservationId).get();
        if (reservation == null){
            throw new NotFoundException("Reservation not found");
        }
        if (reservation.getReservationStatus().equals(ReservationStatus.COMPLETED)|| reservation.getReservationStatus().equals(ReservationStatus.CANCEL)){
            throw new OutOfDateException("Reservation service expiration");
        }
        if (!user.equals(reservation.getUser())&& !userService.hasAdminRole(user)){
            throw new AccessDeniedException("You do not have the right to delete");
        }
        Menu menu = menuRepository.findById(id).get();
        if (menu == null){
            throw new NotFoundException("The menu not found");
        }
        menuRepository.deleteById(id);
        return "Delete successfully";
    }

    @Override
    @Transactional
    public String clear(Authentication authentication, Long id) throws LoginException, EmptyException, NotFoundException, OutOfDateException, AccessDeniedException {
        User user = userService.getUser(authentication);
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (!reservation.isPresent()) {
            throw new NotFoundException("Reservation not found");
        }
        if (reservation.get().getReservationStatus().equals(ReservationStatus.COMPLETED)|| reservation.get().getReservationStatus().equals(ReservationStatus.CANCEL)){
            throw new OutOfDateException("Reservation service expiration");
        }
        if (!user.equals(reservation.get().getUser())&& !userService.hasAdminRole(user)){
            throw new AccessDeniedException("You do not have the right to delete");
        }
        List<Menu> menus = menuRepository.findAllByReservationId(id);
        if (menus.isEmpty()){
            throw new EmptyException("There is no menu to delete");
        }
        menuRepository.deleteAllByReservation(reservation.get());
        return "Clear all menus successfully";
    }

    @Override
    public MenuResponse served( ServedMenuDto servedMenuDto) throws NotFoundException, InvalidException, OverCapacityException, NoSuchElementException {
        Optional<Menu> menu = menuRepository.findById(servedMenuDto.getMenuId());
        if (!menu.isPresent()){
            throw new NoSuchElementException("Menu not found");
        }
        Reservation reservation = reservationRepository.findById(servedMenuDto.getReservationId()).get();
        if (reservation==null){
            throw new NotFoundException("Reservation not found");
        }
        if (!menu.get().getReservation().equals(reservation)){
            throw new InvalidException("There is no such dish in this reservation");
        }
        if (servedMenuDto.getQuantity()>menu.get().getQuantityOrdered()){
            throw new OverCapacityException("Quantity too much.Quantity remaining: " + menu.get().getQuantityOrdered());
        }
        menu.get().setQuantityDelivered(menu.get().getQuantityDelivered()+servedMenuDto.getQuantity());
        menu.get().setQuantityOrdered(menu.get().getQuantityOrdered()-servedMenuDto.getQuantity());

        return menuMapper.toResponse(menuRepository.save(menu.get()));
    }

    @Override
    public StatisticsDto statisticsByDate(Date date) {
        return menuRepository.getStatisticsByDate(date);
    }

    @Override
    public double totalPriceReservation(Long id) throws NotFoundException {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (!reservation.isPresent()) {
            throw new NotFoundException("Not found reservation");
        }
        return menuRepository.totalReservation(reservation.get());
    }
}
