package com.chronos.timereg.service;

import com.chronos.timereg.model.Location;

import java.util.List;

public interface LocationService {
    Location createLocation(Location location);
    Location updateLocation(Long id, Location location);
    Location getLocationById(Long id);
    List<Location> getAllLocations();
    void deleteLocation(Long id);
}
