package booking.service.mapper;

import booking.dto.request.DishRequest;
import booking.dto.response.DishResponse;
import booking.entity.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface DishMapper {

    Dish toEntity(DishRequest dishRequest);
    @Mapping(target = "categoryName", source = "dish.category.categoryName")
    DishResponse toResponse(Dish dish);

}
