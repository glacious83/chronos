package com.chronos.timereg.service;

import com.chronos.timereg.model.Rate;
import java.util.List;

public interface RateService {
    Rate createRate(Rate rate);
    Rate updateRate(Long id, Rate rate);
    Rate getRateById(Long id);
    List<Rate> getAllRates();
    List<String> getTitles();
    void deleteRate(Long id);
}
