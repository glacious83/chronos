package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.DM;
import com.chronos.timereg.repository.DMRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DMServiceImpl implements DMService {

    private final DMRepository dmRepository;

    public DMServiceImpl(DMRepository dmRepository) {
        this.dmRepository = dmRepository;
    }

    @Override
    public DM createDM(DM dm) {
        // Check if a DM with the same code exists.
        if (dmRepository.findByCode(dm.getCode()).isPresent()) {
            throw new BusinessException("DM with code " + dm.getCode() + " already exists.");
        }
        // Validate budget (non-negative)
        if (dm.getBudget() == null || dm.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Budget must be non-negative.");
        }
        // Validate duration: startDate must be before or equal to endDate.
        if (dm.getStartDate() == null || dm.getEndDate() == null) {
            throw new BusinessException("Start date and end date must be provided.");
        }
        if (dm.getEndDate().isBefore(dm.getStartDate())) {
            throw new BusinessException("End date cannot be before start date.");
        }
        return dmRepository.save(dm);
    }

    @Override
    public DM updateDM(Long id, DM dm) {
        DM existing = getDMById(id);
        existing.setCode(dm.getCode());
        existing.setDescription(dm.getDescription());
        if (dm.getBudget() == null || dm.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Budget must be non-negative.");
        }
        existing.setBudget(dm.getBudget());
        if (dm.getStartDate() == null || dm.getEndDate() == null) {
            throw new BusinessException("Start date and end date must be provided.");
        }
        if (dm.getEndDate().isBefore(dm.getStartDate())) {
            throw new BusinessException("End date cannot be before start date.");
        }
        existing.setStartDate(dm.getStartDate());
        existing.setEndDate(dm.getEndDate());
        return dmRepository.save(existing);
    }

    @Override
    public DM getDMById(Long id) {
        return dmRepository.findById(id)
                .orElseThrow(() -> new BusinessException("DM not found with id: " + id));
    }

    @Override
    public List<DM> getAllDMs() {
        return dmRepository.findAll();
    }

    @Override
    public void deleteDM(Long id) {
        DM existing = getDMById(id);
        dmRepository.delete(existing);
    }
}
