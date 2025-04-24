package com.chronos.timereg.service;

import com.chronos.timereg.dto.TimeEntryRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.*;
import com.chronos.timereg.model.enums.ApprovalStatus;
import com.chronos.timereg.model.enums.SpecialDayType;
import com.chronos.timereg.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class TimeEntryServiceImpl implements TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final LeaveEntryRepository leaveEntryRepository;
    private final HolidayRepository holidayRepository;
    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;

    public TimeEntryServiceImpl(TimeEntryRepository timeEntryRepository,
                                UserRepository userRepository,
                                LeaveEntryRepository leaveEntryRepository,
                                HolidayRepository holidayRepository,
                                ContractRepository contractRepository, ProjectRepository projectRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
        this.leaveEntryRepository = leaveEntryRepository;
        this.holidayRepository = holidayRepository;
        this.contractRepository = contractRepository;
        this.projectRepository = projectRepository;
    }

    // --------------------------------------------------
    // Helper Methods for Special Day Logic
    // --------------------------------------------------

    /**
     * Determines the SpecialDayType for the given date.
     * First, it checks the admin-defined holiday table. If a holiday record exists,
     * then it returns HALF_DAY if the holiday is marked as a half-day,
     * or uses the specialDayType stored in the holiday record.
     * If no admin-defined holiday exists, falls back on default computed logic.
     */
    SpecialDayType determineSpecialDayType(LocalDate date) {
        Optional<Holiday> holidayOpt = holidayRepository.findByDate(date);
        if (holidayOpt.isPresent()) {
            Holiday holiday = holidayOpt.get();
            if (holiday.isHalfDay()) {
                return SpecialDayType.HALF_DAY;
            }
            return holiday.getSpecialDayType() != null ? holiday.getSpecialDayType() : SpecialDayType.GREEK_HOLIDAY;
        }
        // Fall back to computed default:
        Set<LocalDate> holidays = getGreekHolidays(date.getYear());
        if (holidays.contains(date)) {
            return SpecialDayType.GREEK_HOLIDAY;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return SpecialDayType.SATURDAY;
        }
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return SpecialDayType.SUNDAY;
        }
        return SpecialDayType.NORMAL;
    }

    /**
     * Builds a set of Greek holiday dates (fixed + computed for Easter) for a given year.
     * This includes Holy Spirit (Pentecost) and other common church feasts.
     */
    @Override
    public Set<LocalDate> getGreekHolidays(int year) {
        Set<LocalDate> holidays = new HashSet<>();

        // Fixed-date holidays
        holidays.add(LocalDate.of(year, 1, 1));    // New Year’s Day
        holidays.add(LocalDate.of(year, 1, 6));    // Theophania
        holidays.add(LocalDate.of(year, 3, 25));   // March 25 (National Independence)
        holidays.add(LocalDate.of(year, 5, 1));    // Labour Day
        holidays.add(LocalDate.of(year, 8, 15));   // Dormition of the Virgin Mary
        holidays.add(LocalDate.of(year, 10, 28));  // Ochi Day
        holidays.add(LocalDate.of(year, 12, 25));  // Christmas Day
        holidays.add(LocalDate.of(year, 12, 26));  // Synaxis of the Mother of God

        // Example: simplified calculation for Orthodox Easter + Holy Spirit
        LocalDate orthodoxEaster = calculateOrthodoxEaster(year);
        // Clean Monday: 48 days before Easter
        holidays.add(orthodoxEaster.minusDays(48));
        // Good Friday: 2 days before Easter
        holidays.add(orthodoxEaster.minusDays(2));
        // Holy Saturday: 1 day before Easter
        holidays.add(orthodoxEaster.minusDays(1));
        // Easter Sunday
        holidays.add(orthodoxEaster);
        // Easter Monday: the day after Easter
        holidays.add(orthodoxEaster.plusDays(1));
        // Holy Spirit (Pentecost): 50 days after Easter
        holidays.add(orthodoxEaster.plusDays(50));

        return holidays;
    }

    @Override
    public List<Holiday> getAllHolidays() {
        List<Holiday> dbHolidays = holidayRepository.findAll();

        int year = LocalDate.now().getYear();
        // compute church dates
        LocalDate orthodoxEaster = calculateOrthodoxEaster(year);
        Map<LocalDate,String> names = new HashMap<>();
        // fixed
        names.put(LocalDate.of(year, 1, 1),   "New Year’s Day");
        names.put(LocalDate.of(year, 1, 6),   "Theophania");
        names.put(LocalDate.of(year, 3, 25),  "Independence Day");
        names.put(LocalDate.of(year, 5, 1),   "Labour Day");
        names.put(LocalDate.of(year, 8, 15),  "Dormition of the Virgin Mary");
        names.put(LocalDate.of(year, 10, 28), "Ochi Day");
        names.put(LocalDate.of(year, 12, 25), "Christmas Day");
        names.put(LocalDate.of(year, 12, 26), "Synaxis of the Mother of God");
        // church
        names.put(orthodoxEaster.minusDays(48), "Clean Monday");
        names.put(orthodoxEaster.minusDays(2),  "Good Friday");
        names.put(orthodoxEaster.minusDays(1),  "Holy Saturday");
        names.put(orthodoxEaster,               "Easter Sunday");
        names.put(orthodoxEaster.plusDays(1),   "Easter Monday");
        names.put(orthodoxEaster.plusDays(50),  "Pentecost");

        Set<LocalDate> greekDates = new HashSet<>(names.keySet());
        // add any missing computed ones
        for (LocalDate date : greekDates) {
            boolean exists = dbHolidays.stream()
                    .anyMatch(h -> h.getDate().equals(date));
            if (!exists) {
                Holiday h = new Holiday();
                h.setName(names.get(date));
                h.setDate(date);
                h.setHalfDay(false);
                h.setSpecialDayType(SpecialDayType.GREEK_HOLIDAY);
                dbHolidays.add(h);
            }
        }

        return dbHolidays;
    }


    /**
     * Approximate algorithm to find Orthodox Easter. This is only a placeholder.
     * For production, consider using a better-tested approach or library.
     */
    public static LocalDate calculateOrthodoxEaster(int year) {
        // The following algorithm is based on the algorithm published in the
        // Orthodox-Holy-Days-Java-Calculator repository.
        // (In production, use the complete, verified source from that project.)

        // Compute the Julian Easter date:
        int a = year % 4;
        int b = year % 7;
        int c = year % 19;
        int d = (19 * c + 15) % 30;
        int e = (2 * a + 4 * b - d + 34) % 7;
        int month = (d + e + 114) / 31; // 3 = March, 4 = April.
        int day = ((d + e + 114) % 31) + 1;
        LocalDate julianEaster = LocalDate.of(year, month, day);

        // Determine the current offset between the Julian and Gregorian calendars.
        // (For the 20th and 21st centuries, this offset is usually 13 days.)
        int offset = 13;

        // Convert the Julian date to the Gregorian date.

        // For testing purposes, if the Nosfistis algorithm gives the expected results for 2026,
        // then gregorianEaster should be April 5, 2026.
        return julianEaster.plusDays(offset);
    }

    // --------------------------------------------------
    // CRUD Methods
    // --------------------------------------------------

    @Override
    public TimeEntry createTimeEntry(TimeEntryRequest dto) {
        Project project = null;
        if (dto.getWorkedHours() > 8.0) {
            throw new BusinessException("Individual time entry worked hours cannot exceed 8 hours per day.");
        }
        // Lookup the user.
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + dto.getUserId()));

        Contract contract = contractRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Contract not found."));

        // Check for conflicting leave entries.
        if (!leaveEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate()).isEmpty()) {
            throw new BusinessException("Cannot register working hours for a day on which the user is on leave.");
        }

        // Sum existing worked hours for the day.
        List<TimeEntry> existingEntries = timeEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate());
        double totalWorked = existingEntries.stream().mapToDouble(TimeEntry::getWorkedHours).sum();
        if (totalWorked + dto.getWorkedHours() > 8.0) {
            throw new BusinessException("Total worked hours for the day cannot exceed 8 hours. Already recorded: " + totalWorked + " hours.");
        }

        if (dto.getProjectId()!=null) {
            project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new BusinessException("Project not found with id: " + dto.getProjectId()));
        }

        validateTimeEntry(dto, contract);

        // Build the new TimeEntry entity.
        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setDate(dto.getDate());
        entry.setWorkedHours(dto.getWorkedHours());
        entry.setOvertimeHours(dto.getOvertimeHours());
        entry.setWorkLocation(dto.getWorkLocation());
        entry.setSpecialDayType(determineSpecialDayType(dto.getDate()));
        entry.setProject(project);
        entry.setCompensationHours(0.0); // System-managed field.
        entry.setApprovalStatus(ApprovalStatus.PENDING);

        // **Important:** Always set isLeave to false since this field is not provided by the user.
        entry.setIsLeave(false);

        return timeEntryRepository.save(entry);
    }

    @Override
    public TimeEntry updateTimeEntry(Long id, TimeEntryRequest dto) {
        if (!leaveEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate()).isEmpty()) {
            throw new BusinessException("Cannot update: working hours cannot be registered if the user is on leave for that day.");
        }

        TimeEntry existing = getTimeEntryById(id);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + dto.getUserId()));

        Contract contract = contractRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Contract not found."));

        List<TimeEntry> otherEntries = timeEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate());
        otherEntries.removeIf(entry -> entry.getId().equals(id));
        validateTimeEntry(dto, contract);

        existing.setUser(user);
        existing.setDate(dto.getDate());
        existing.setWorkedHours(dto.getWorkedHours());
        existing.setOvertimeHours(dto.getOvertimeHours());
        existing.setWorkLocation(dto.getWorkLocation());
        existing.setSpecialDayType(determineSpecialDayType(dto.getDate()));

        // Ensure isLeave remains false on update as well.
        existing.setIsLeave(false);

        return timeEntryRepository.save(existing);
    }

    @Override
    public TimeEntry getTimeEntryById(Long id) {
        return timeEntryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("TimeEntry not found with id: " + id));
    }

    @Override
    public List<TimeEntry> getAllTimeEntries() {
        return timeEntryRepository.findAll();
    }

    @Override
    public void deleteTimeEntry(Long id) {
        TimeEntry existing = getTimeEntryById(id);
        timeEntryRepository.delete(existing);
    }

    @Override
    public double getTotalCompensationHoursForUser(Long userId) {
        List<TimeEntry> entries = timeEntryRepository.findByUser_Id(userId);
        return entries.stream()
                .mapToDouble(TimeEntry::getCompensationHours)
                .sum();
    }

    @Override
    public List<TimeEntry> getTimeEntryByUserIdAndDates(Long userId, LocalDate startDate, LocalDate endDate) {
        return timeEntryRepository.findByUser_IdAndDateBetween(userId, startDate, endDate);
    }

    private void validateTimeEntry(TimeEntryRequest dto, Contract contract) {
        if (contract == null) {
            return;
        }
        if (contract.getWorkingHoursStart()==null || contract.getWorkingHoursEnd()==null) {
            return;
        }
        long dailyContractHours = Duration.between(contract.getWorkingHoursStart(), contract.getWorkingHoursEnd()).toMinutes() / 60;

        if (dto.getWorkedHours() < dailyContractHours)
            throw new BusinessException("Total hours (" + dto.getWorkedHours() + ") less than contracted hours (" + dailyContractHours + ").");

        if (dto.getWorkedHours() > dailyContractHours)
            throw new BusinessException("Overtime approval required for exceeding contracted hours.");

        if (!leaveEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate()).isEmpty())
            throw new BusinessException("Cannot register working hours for a day on which the user is on leave.");

        List<TimeEntry> existingEntries = timeEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate());
        double totalWorked = existingEntries.stream().mapToDouble(TimeEntry::getWorkedHours).sum();
        if (totalWorked + dto.getWorkedHours() > dailyContractHours)
            throw new BusinessException("Total worked hours for the day exceed contracted hours.");
    }
}
