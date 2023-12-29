package booking.service.impl.table;

import booking.dto.request.TableRequest;
import booking.dto.response.TableResponse;
import booking.dto.response.TableStatusByTime;
import booking.entity.Table;
import booking.exception.ExistsException;
import booking.exception.NotFoundException;
import booking.repository.TableRepository;
import booking.service.mapper.TableMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TableService implements ITableService{
    private final TableRepository tableRepository;
    private final TableMapper tableMapper;

    @Override
    public Page<TableResponse> findAllByStatus(String name, int page, int size) {
        return tableRepository.findAllByTableNameContainingAndStatus(name,true,PageRequest.of(page,size)).map(tableMapper::toResponse);
    }

    @Override
    public Page<TableResponse> findAll(String name, int page, int size) {
        return tableRepository.findAllByTableNameContaining(name, PageRequest.of(page, size)).map(tableMapper::toResponse);
    }

    @Override
    public TableResponse findById(Long id) throws NotFoundException {
        Table table = tableRepository.findById(id).get();
        if (table == null) {
            throw new NotFoundException("Table " + id + " not found");
        }
        return tableMapper.toResponse(table);
    }

    @Override
    public TableResponse create(TableRequest tableRequest)throws ExistsException {
        if (tableRepository.existsByTableName(tableRequest.getTableName())) {
            throw new ExistsException("Table " + tableRequest.getTableName() + " already exists");
        }
        Table table = tableMapper.toEntity(tableRequest);
        table.setStatus(true);
        return tableMapper.toResponse(tableRepository.save(table));
    }

    @Override
    public TableResponse update(TableRequest tableRequest, Long id)throws ExistsException {
        if (tableRepository.existsByTableName(tableRequest.getTableName())) {
            throw new ExistsException("Table " + tableRequest.getTableName() + " already exists");
        }
        Table table = tableMapper.toEntity(tableRequest);
        table.setId(id);
        table.setStatus(true);
        return tableMapper.toResponse(tableRepository.save(table));
    }

    @Override
    public String changeStatus(Long id) throws NotFoundException {
        Table table = tableRepository.findById(id).get();
        if (table == null) {
            throw new NotFoundException("Table " + id + " not found");
        }
        table.setStatus(!table.isStatus());
        tableRepository.save(table);
        return "Success";
    }

    @Override
    public List<TableStatusByTime> findByTime(Date date, LocalTime start, LocalTime end) {
        return tableRepository.findTableStatusByTime( start, end,date);
    }
}
