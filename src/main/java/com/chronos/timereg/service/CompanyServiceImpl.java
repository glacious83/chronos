package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Company;
import com.chronos.timereg.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company createCompany(Company company) {
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new BusinessException("Company with name " + company.getName() + " already exists.");
        }
        return companyRepository.save(company);
    }

    @Override
    public Company updateCompany(Long id, Company company) {
        Company existing = getCompanyById(id);
        existing.setName(company.getName());
        existing.setExternal(company.isExternal());
        return companyRepository.save(existing);
    }

    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Company not found with id: " + id));
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public void deleteCompany(Long id) {
        Company existing = getCompanyById(id);
        companyRepository.delete(existing);
    }
}
