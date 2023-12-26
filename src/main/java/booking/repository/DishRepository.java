package booking.repository;

import booking.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DishRepository extends JpaRepository<Dish,Long> {

    @Query("SELECT d FROM Dish d WHERE d.dishName LIKE %:name% AND d.price BETWEEN :min AND :max")
    Page<Dish> findAllByProductNameContainingAndPriceBetween(@Param("name") String name,
                                                                @Param("min") Double min,
                                                                @Param("max") Double max,
                                                                Pageable pageable);
    @Query("SELECT d FROM Dish d WHERE d.dishName LIKE %:name% AND d.price BETWEEN :min AND :max AND d.status = true")
    Page<Dish> findAllByProductNameContainingAndPriceBetweenAndStatus(String name, double min, double max, Pageable pageable);

    Boolean existsByDishName(String name);
}
