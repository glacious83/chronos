package com.chronos.timereg.service;

import com.chronos.timereg.model.Company;

import java.util.List;

public interface CompanyService {
    Company createCompany(Company company);
    Company updateCompany(Long id, Company company);
    Company getCompanyById(Long id);
    List<Company> getAllCompanies();
    void deleteCompany(Long id);
}
