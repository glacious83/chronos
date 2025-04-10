package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Location;
import com.chronos.timereg.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location createLocation(Location location) {
        if (locationRepository.findByCityNameAndCountryCode(location.getCityName(), location.getCountryCode()).isPresent()) {
            throw new BusinessException("Location " + location.getCityName() + " (" + location.getCountryCode() + ") already exists.");
        }
        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(Long id, Location location) {
        Location existing = getLocationById(id);
        existing.setCityName(location.getCityName());
        existing.setCountry(location.getCountry());
        existing.setCountryCode(location.getCountryCode());
        return locationRepository.save(existing);
    }

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Location not found with id: " + id));
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public void deleteLocation(Long id) {
        Location existing = getLocationById(id);
        locationRepository.delete(existing);
    }
}
