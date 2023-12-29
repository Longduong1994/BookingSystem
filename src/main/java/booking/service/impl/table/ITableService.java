package booking.service.impl.table;

import booking.dto.request.TableRequest;
import booking.dto.response.TableResponse;
import booking.dto.response.TableStatusByTime;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ITableService {

    Page<TableResponse> findAllByStatus(String name,int page,int size);
    Page<TableResponse> findAll(String name,int page,int size);
    TableResponse findById(Long id) throws NotFoundException;
    TableResponse create(TableRequest tableRequest) throws ExistsException;
    TableResponse update(TableRequest tableRequest,Long id) throws ExistsException;
    String changeStatus(Long id) throws NotFoundException;
    List<TableStatusByTime> findByTime(Date date, LocalTime start, LocalTime end) ;

}
