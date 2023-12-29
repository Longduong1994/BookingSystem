package booking.controller;

import booking.dto.request.MenuRequest;
import booking.dto.request.ServedMenuDto;
import booking.dto.response.MenuResponse;
import booking.exception.*;
import booking.service.impl.menu.IMenuService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("api/v1/admin/menus")
@AllArgsConstructor
public class MenuController {
    private final IMenuService menuService;

    @GetMapping
    public ResponseEntity<Page<MenuResponse>> findByReservation(@RequestParam(required = false) Long id,
                                                                @RequestParam(defaultValue = "")String code,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size) {
        return new ResponseEntity<>(menuService.findAll(code,id,page,size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addToReservation(@RequestBody MenuRequest menuRequest, Authentication authentication) throws AddMenuException, LoginException, NotFoundException, AccessDeniedException {
        return new ResponseEntity<>(menuService.addToReservation(menuRequest,authentication), HttpStatus.CREATED);
    }

    @PatchMapping("/served")
    public ResponseEntity<?> served(@RequestBody @Valid ServedMenuDto servedMenuDto) throws NotFoundException, InvalidException, OverCapacityException, NoSuchElementException {
        return new  ResponseEntity<>(menuService.served(servedMenuDto),HttpStatus.OK);
    }

    @GetMapping("/statistical")
    public ResponseEntity<?> getStatistical(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
        if (date==null){
            date = new Date();
        }
        return new ResponseEntity<>(menuService.statisticsByDate(date), HttpStatus.OK);
    }

//    @GetMapping("/total/{id}")
//    public ResponseEntity<?> getTotalByReservation(@PathVariable Long id){
//        return
//    }
}
