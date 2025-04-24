package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Holiday;
import com.chronos.timereg.model.enums.SpecialDayType;
import com.chronos.timereg.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chronos.timereg.service.TimeEntryServiceImpl.calculateOrthodoxEaster;

@Service
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final TimeEntryService timeEntryService;

    public HolidayServiceImpl(HolidayRepository holidayRepository, TimeEntryService timeEntryService) {
        this.holidayRepository = holidayRepository;
        this.timeEntryService = timeEntryService;
    }

    @Override
    public List<Holiday> getHolidaysForYear(int year) {
        // 1. Fetch DB-defined holidays for that year
        List<Holiday> dbHolidays = holidayRepository.findAll().stream()
                .filter(h -> h.getDate().getYear() == year)
                .collect(Collectors.toList());

        // 2. Prepare names for fixed and church holidays
        Map<LocalDate, String> names = new HashMap<>();
        names.put(LocalDate.of(year, 1, 1),    "New Year’s Day");
        names.put(LocalDate.of(year, 1, 6),    "Theophania");
        names.put(LocalDate.of(year, 3, 25),   "Independence Day");
        names.put(LocalDate.of(year, 5, 1),    "Labour Day");
        names.put(LocalDate.of(year, 8, 15),   "Dormition of the Virgin Mary");
        names.put(LocalDate.of(year, 10, 28),  "Ochi Day");
        names.put(LocalDate.of(year, 12, 25),  "Christmas Day");
        names.put(LocalDate.of(year, 12, 26),  "Synaxis of the Mother of God");

        LocalDate easter = calculateOrthodoxEaster(year);
        names.put(easter.minusDays(48), "Clean Monday");
        names.put(easter.minusDays(2),  "Good Friday");
        names.put(easter.minusDays(1),  "Holy Saturday");
        names.put(easter,               "Easter Sunday");
        names.put(easter.plusDays(1),   "Easter Monday");
        names.put(easter.plusDays(50),  "Pentecost");

        // 3. Compute church dates and merge any missing
        Set<LocalDate> greekDates = names.keySet();
        for (LocalDate d : greekDates) {
            boolean exists = dbHolidays.stream()
                    .anyMatch(h -> h.getDate().equals(d));
            if (!exists) {
                Holiday h = new Holiday();
                h.setName(names.get(d));
                h.setDate(d);
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
