package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Holiday;
import com.chronos.timereg.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayServiceImpl(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Override
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    @Override
    public Holiday getHolidayById(Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Holiday not found with id: " + id));
    }

    @Override
    public Holiday createHoliday(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    @Override
    public Holiday updateHoliday(Long id, Holiday holiday) {
        Holiday existing = getHolidayById(id);
        existing.setName(holiday.getName());
        existing.setDate(holiday.getDate());
        existing.setHalfDay(holiday.isHalfDay());
        existing.setSpecialDayType(holiday.getSpecialDayType());
        return holidayRepository.save(existing);
    }

    @Override
    public void deleteHoliday(Long id) {
        holidayRepository.deleteById(id);
    }
}
