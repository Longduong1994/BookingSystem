package booking.config;

import booking.service.mapper.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CategoryMapper categoryMapper() {
        return Mappers.getMapper(CategoryMapper.class);
    }
    @Bean
    public DishMapper dishMapper() {
        return Mappers.getMapper(DishMapper.class);
    }
    @Bean
    TableMapper tableMapper() {
        return Mappers.getMapper(TableMapper.class);
    }
    @Bean
    ReservationMapper reservationMapper() {
        return Mappers.getMapper(ReservationMapper.class);
    }
    @Bean
    MenuMapper menuMapper() {return Mappers.getMapper(MenuMapper.class);}
    @Bean
    UserMapper userMapper() {return Mappers.getMapper(UserMapper.class);}
}
