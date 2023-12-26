package booking.controller;

import booking.dto.request.CategoryRequest;
import booking.dto.response.CategoryResponse;
import booking.service.impl.category.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll(){
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest categoryRequest){
      return new ResponseEntity<>(categoryService.create(categoryRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@RequestBody CategoryRequest categoryRequest,@PathVariable Long id){
        return new ResponseEntity<>(categoryService.update(categoryRequest,id), HttpStatus.OK);
    }

    @PatchMapping("/lock/{id}")
    public ResponseEntity<?> lock(@PathVariable Long id){
        return new ResponseEntity<>(categoryService.lock(id), HttpStatus.OK);
    }
}
