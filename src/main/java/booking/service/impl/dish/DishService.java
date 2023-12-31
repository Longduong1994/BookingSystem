package booking.service.impl.dish;

import booking.dto.request.DishRequest;
import booking.dto.response.DishResponse;
import booking.entity.Category;
import booking.entity.Dish;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import booking.repository.CategoryRepository;
import booking.repository.DishRepository;
import booking.service.impl.upload_file.UploadFileService;
import booking.service.mapper.DishMapper;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DishService  implements IDishService{
    private final DishRepository dishRepository;
    private final DishMapper dishMapper;
    private final UploadFileService uploadFileService;
    private final CategoryRepository categoryRepository;
    @Override
    public Page<DishResponse> findAll(String name, int page, int size, double min, double max) {
        return dishRepository.findAllByProductNameContainingAndPriceBetween(name,min,max,PageRequest.of(page, size)).map(dishMapper::toResponse);
    }

    @Override
    public Page<DishResponse> findAllByStatus(String name,String field,String by, int page, int size, double min, double max) {
        Sort sort = Sort.by(field);

        if (by.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return dishRepository.findAllByProductNameContainingAndPriceBetweenAndStatus(name,min,max,PageRequest.of(page, size).withSort(sort)).map(dishMapper::toResponse);
    }

    @Override
    public DishResponse create(DishRequest dishRequest) throws ExistsException {
        if (dishRepository.existsByDishName(dishRequest.getDishName())) {
            throw new ExistsException("Dish name already exists");
        }
        String image = uploadFileService.uploadFile(dishRequest.getFile());
        Category category = categoryRepository.findById(dishRequest.getCategoryId()).get();
        Dish dish = dishMapper.toEntity(dishRequest);
        dish.setImage(image);
        dish.setCategory(category);
        dish.setStatus(true);
        return dishMapper.toResponse(dishRepository.save(dish)) ;
    }

    @Override
    public DishResponse update(DishRequest dishRequest, Long id) throws ExistsException {
        if (dishRepository.existsByDishName(dishRequest.getDishName())) {
            throw new ExistsException("Dish name already exists");
        }
        String image = uploadFileService.uploadFile(dishRequest.getFile());
        Category category = categoryRepository.findById(dishRequest.getCategoryId()).get();
        Dish dish = dishMapper.toEntity(dishRequest);
        dish.setImage(image);
        dish.setCategory(category);
        dish.setId(id);
        return dishMapper.toResponse(dish);
    }

    @Override
    public String delete(Long id) throws NotFoundException {
        Dish dish = dishRepository.findById(id).get();
        if (dish != null) {
            dishRepository.deleteById(id);
            return "Delete successfully";
        }
        throw  new NotFoundException("Item not found");
    }

    @Override
    public String changeStatus(Long id) throws NotFoundException {
        Dish dish = dishRepository.findById(id).get();
        if (dish != null) {
            dish.setStatus(!dish.isStatus());
            dishRepository.save(dish);
            return "Change status successfully";
        }
        throw  new NotFoundException("Item not found");
    }

    @Override
    public List<DishResponse> findTopFive() {
        return dishRepository.findDishTopFive().stream().map(dishMapper::toResponse).collect(Collectors.toList());
    }
}
