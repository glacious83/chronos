package com.chronos.timereg.repository;

import com.chronos.timereg.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCityNameAndCountryCode(String cityName, String countryCode);

    Optional<Location> findByCityName(String locationStr);
}
