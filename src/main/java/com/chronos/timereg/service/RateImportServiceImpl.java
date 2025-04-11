package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Rate;
import com.chronos.timereg.repository.RateRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class RateImportServiceImpl implements RateImportService {

    private final RateRepository rateRepository;

    public RateImportServiceImpl(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Override
    public void importRates(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Get the sheet named "Rates"
            Sheet sheet = workbook.getSheet("rates");
            if (sheet == null) {
                return;
            }

            // Assume the first row (index 0) is the header row with "Profiles" and "Rates"
            int rowCount = sheet.getLastRowNum();
            // Loop through each row starting from row 1
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;  // Skip empty rows

                // Read the "Profiles" column (assumed to be at cell index 0)
                Cell profileCell = row.getCell(0);
                // Read the "Rates" column (assumed to be at cell index 1)
                Cell rateCell = row.getCell(1);

                if (profileCell == null || rateCell == null) continue; // Skip if either is missing

                profileCell.setCellType(CellType.STRING);
                String userTitle = profileCell.getStringCellValue().trim();
                if (userTitle.isEmpty()) continue; // Skip empty profile values

                BigDecimal rateValue = null;
                if (rateCell.getCellType() == CellType.NUMERIC) {
                    rateValue = BigDecimal.valueOf(rateCell.getNumericCellValue());
                } else {
                    rateCell.setCellType(CellType.STRING);
                    String rateStr = rateCell.getStringCellValue().trim();
                    if (!rateStr.isEmpty()) {
                        try {
                            rateValue = new BigDecimal(rateStr);
                        } catch (NumberFormatException e) {
                            throw new BusinessException("Invalid rate format at row " + (i + 1) + ": " + rateStr);
                        }
                    }
                }
                if (rateValue == null) {
                    throw new BusinessException("Null rate value at row " + (i + 1));
                }

                // Look up if a Rate for this userTitle exists.
                Optional<Rate> existingOpt = rateRepository.findByUserTitle(userTitle);
                if (existingOpt.isPresent()) {
                    // Update the existing rate.
                    Rate existingRate = existingOpt.get();
                    existingRate.setRate(rateValue);
                    rateRepository.save(existingRate);
                } else {
                    // Create a new Rate.
                    Rate newRate = new Rate();
                    newRate.setUserTitle(userTitle);
                    newRate.setRate(rateValue);
                    rateRepository.save(newRate);
                }
            }
        } catch (Exception e) {
            throw new BusinessException("Error importing rates: " + e.getMessage());
        }
    }
}
