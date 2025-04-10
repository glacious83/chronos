package com.chronos.timereg.repository;

import com.chronos.timereg.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCityNameAndCountryCode(String cityName, String countryCode);

    Optional<Location> findByCityName(String locationStr);

    @Query("SELECT l from Location l WHERE " +
            "l.cityName IS NULL OR l.cityName = '' OR " +
            "l.country IS NULL OR l.country = '' OR l.country = 'Unknown' OR " +
            "l.countryCode IS NULL OR l.countryCode = ''")
    List<Location> findLocationsWithMissingFields();
}
