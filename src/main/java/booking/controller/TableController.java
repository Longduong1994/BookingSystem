package booking.controller;

import booking.dto.request.TableRequest;
import booking.dto.response.TableResponse;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import booking.service.impl.table.ITableService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

@RestController
@RequestMapping("api/v1/admin/tables")
@AllArgsConstructor
public class TableController {
    private final ITableService tableService;

    @GetMapping
    public ResponseEntity<Page<TableResponse>> findAll(@RequestParam(defaultValue = "") String name,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size){
        return new ResponseEntity<>(tableService.findAll(name,page,size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> findById(@PathVariable Long id) throws NotFoundException{
        return new ResponseEntity<>(tableService.findById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TableResponse> create(@Valid @RequestBody TableRequest tableRequest) throws ExistsException{
        return new ResponseEntity<>(tableService.create(tableRequest),HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<TableResponse> update(@Valid @RequestBody TableRequest tableRequest, @PathVariable Long id)throws ExistsException{
        return new ResponseEntity<>(tableService.update(tableRequest, id),HttpStatus.OK);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) throws NotFoundException {
        return new ResponseEntity<>(tableService.changeStatus(id),HttpStatus.OK);
    }




}
