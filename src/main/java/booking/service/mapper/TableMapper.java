package booking.service.mapper;

import booking.dto.request.TableRequest;
import booking.dto.response.TableResponse;
import booking.entity.Table;
import org.mapstruct.Mapper;

@Mapper
public interface TableMapper {

    Table toEntity(TableRequest tableRequest);

    TableResponse toResponse(Table table);
}
