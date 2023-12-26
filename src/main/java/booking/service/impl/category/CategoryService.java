package booking.service.impl.category;

import booking.dto.request.CategoryRequest;
import booking.dto.response.CategoryResponse;
import booking.entity.Category;
import booking.repository.CategoryRepository;
import booking.service.mapper.CategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findById(Long id) {
        return categoryMapper.toResponse(categoryRepository.findById(id).get());
    }

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        return categoryMapper.toResponse(categoryRepository.save(categoryMapper.toEntity(categoryRequest)));
    }

    @Override
    public CategoryResponse update(CategoryRequest categoryRequest, Long id) {
        Category category = categoryMapper.toEntity(categoryRequest);
        category.setId(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    public String lock(Long id) {
        Category category = categoryRepository.findById(id).get();
        if (category != null) {
            category.setStatus(!category.isStatus());
            categoryRepository.save(category);
            return "Khóa thành công";
        }
        return null;
    }
}
