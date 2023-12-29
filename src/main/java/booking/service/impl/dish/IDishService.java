package booking.service.impl.dish;

import booking.dto.request.DishRequest;
import booking.dto.response.DishResponse;
import booking.entity.Dish;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IDishService {
    Page<DishResponse> findAll(String name,int page,int size, double min,double max );

    Page<DishResponse> findAllByStatus(String name,String field,String by,int page,int size, double min,double max );

    DishResponse create(DishRequest dishRequest) throws ExistsException;
    DishResponse update(DishRequest dishRequest,Long id) throws ExistsException;

    String delete(Long id) throws NotFoundException;

    String changeStatus(Long id) throws NotFoundException;

    List<DishResponse> findTopFive();
}
