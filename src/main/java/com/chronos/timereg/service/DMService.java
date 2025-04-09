package com.chronos.timereg.service;

import com.chronos.timereg.model.DM;

import java.util.List;

public interface DMService {
    DM createDM(DM dm);
    DM updateDM(Long id, DM dm);
    DM getDMById(Long id);
    List<DM> getAllDMs();
    void deleteDM(Long id);
}
