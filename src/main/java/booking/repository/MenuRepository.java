package booking.repository;

import booking.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Long> {
    Page<Menu> findAllByReservationCodeOrReservationId(String code,Long id,Pageable pageable);
    List<Menu> findAllByReservationId(Long id);
}
