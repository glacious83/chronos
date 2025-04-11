package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Rate;
import com.chronos.timereg.repository.RateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;

    public RateServiceImpl(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Override
    public Rate createRate(Rate rate) {
        if (rateRepository.findByUserTitle(rate.getUserTitle()).isPresent()) {
            throw new BusinessException("Rate for user title \"" + rate.getUserTitle() + "\" already exists.");
        }
        if (rate.getRate() == null || rate.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Rate must be non-negative.");
        }
        return rateRepository.save(rate);
    }

    @Override
    public Rate updateRate(Long id, Rate rate) {
        Rate existing = getRateById(id);
        existing.setUserTitle(rate.getUserTitle());
        if (rate.getRate() == null || rate.getRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Rate must be non-negative.");
        }
        existing.setRate(rate.getRate());
        return rateRepository.save(existing);
    }

    @Override
    public Rate getRateById(Long id) {
        return rateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rate not found with id: " + id));
    }

    @Override
    public List<Rate> getAllRates() {
        return rateRepository.findAll();
    }

    @Override
    public List<String> getTitles() {
        return rateRepository.findTitles();
    }

    @Override
    public void deleteRate(Long id) {
        Rate existing = getRateById(id);
        rateRepository.delete(existing);
    }
}
