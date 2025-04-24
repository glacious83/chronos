package com.chronos.timereg.service;

import com.chronos.timereg.model.Holiday;

import java.util.List;

public interface HolidayService {
    Holiday getHolidayById(Long id);
    Holiday createHoliday(Holiday holiday);
    Holiday updateHoliday(Long id, Holiday holiday);
    void deleteHoliday(Long id);

    List<Holiday> getHolidaysForYear(int y);
}
