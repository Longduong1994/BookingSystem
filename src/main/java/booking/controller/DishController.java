package booking.controller;

import booking.dto.request.DishRequest;
import booking.dto.response.DishResponse;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import booking.service.impl.dish.IDishService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes")
@AllArgsConstructor
public class DishController {
    private final IDishService dishService;

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "0") double min,
                                     @RequestParam(defaultValue = "" + Double.MAX_VALUE) double max){
        return new ResponseEntity<>(dishService.findAll(name,page,size,min,max), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> findAllByStatus(@RequestParam(defaultValue = "") String name,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     @RequestParam(defaultValue = "0") double min,
                                     @RequestParam(defaultValue = "" + Double.MAX_VALUE) double max){
        return new ResponseEntity<>(dishService.findAllByStatus(name,page,size,min,max), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DishResponse> create(@ModelAttribute @Valid DishRequest dishRequest) throws ExistsException {
        return new ResponseEntity<>(dishService.create(dishRequest),HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<DishResponse> update(@ModelAttribute @Valid DishRequest dishRequest,@PathVariable Long id) throws ExistsException {
        return new ResponseEntity<>(dishService.update(dishRequest,id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> lock(@PathVariable Long id) throws NotFoundException {
        return new ResponseEntity<>(dishService.delete(id),HttpStatus.OK);
    }

}
