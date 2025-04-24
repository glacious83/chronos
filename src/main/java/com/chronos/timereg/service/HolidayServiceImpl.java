package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Holiday;
import com.chronos.timereg.model.enums.SpecialDayType;
import com.chronos.timereg.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final TimeEntryService timeEntryService;

    public HolidayServiceImpl(HolidayRepository holidayRepository, TimeEntryService timeEntryService) {
        this.holidayRepository = holidayRepository;
        this.timeEntryService = timeEntryService;
    }

    @Override
    public List<Holiday> getAllHolidays() {
        List<Holiday> dbHolidays = holidayRepository.findAll();
        int year = LocalDate.now().getYear();
        Set<LocalDate> greekDates = timeEntryService.getGreekHolidays(year);

        for (LocalDate date : greekDates) {
            boolean exists = dbHolidays.stream()
                    .anyMatch(h -> h.getDate().equals(date));
            if (!exists) {
                Holiday h = new Holiday();
                h.setName("Greek Holiday");
                h.setDate(date);
                h.setHalfDay(false);
                h.setSpecialDayType(SpecialDayType.GREEK_HOLIDAY);
                dbHolidays.add(h);
            }
        }

        return dbHolidays;
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
