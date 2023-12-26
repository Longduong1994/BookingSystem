package booking.service.mapper;

import booking.dto.request.MenuRequest;
import booking.dto.request.UpdateMenuRequest;
import booking.dto.response.MenuResponse;
import booking.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MenuMapper {

    @Mapping(target = "quantityOrdered",source = "quantity")
    Menu toEntity(MenuRequest menuRequest);

    @Mapping(target = "dishName" ,source = "menu.dish.dishName")
    @Mapping(target = "image",source = "menu.dish.image")
    @Mapping(target = "price",source = "menu.dish.price")
    MenuResponse toResponse(Menu menu);
}
