package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;
import java.util.Date;
import java.util.List;

@Repository
public interface MainRepository extends JpaRepository<Ship, Long> {

    @Query(value = "SELECT s FROM Ship s WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND s.planet LIKE CONCAT('%', :planet, '%') " +
//            "AND s.shipType = :shipType " +
            "AND s.prodDate >= :after AND s.prodDate <= :before " +
            "AND s.isUsed = :isUsed " +
            "AND s.speed >= :minSpeed AND s.speed <= :maxSpeed " +
            "AND s.crewSize >= :minCrewSize AND s.crewSize <= :maxCrewSize " +
            "AND s.rating >= :minRating AND s.rating <= :maxRating " +
            "ORDER BY  :order", nativeQuery = true)
    List<Ship> getByFilter(@Param("name") String name, @Param("planet") String planet,
                           @Param("isUsed") boolean isUsed, //@Param("shipType") ShipType shipType,
                           @Param("after") Date after, @Param("before") Date before,
                           @Param("minSpeed") double minSpeed, @Param("maxSpeed") double maxSpeed,
                           @Param("minCrewSize") int minCrewSize, @Param("maxCrewSize") int maxCrewSize,
                           @Param("minRating") double minRating, @Param("maxRating") double maxRating,
                           @Param("order") ShipOrder shipOrder);

}
