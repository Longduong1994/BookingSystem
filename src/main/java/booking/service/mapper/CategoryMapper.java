package booking.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import booking.dto.request.CategoryRequest;
import booking.dto.response.CategoryResponse;
import booking.entity.Category;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    @Mapping(target = "status", constant = "true")
    Category toEntity(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);
}
