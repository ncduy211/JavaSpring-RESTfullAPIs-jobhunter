package vn.duync.jobhunter.service;

import org.springframework.data.domain.Pageable;

import vn.duync.jobhunter.domain.Company;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;

public interface CompanyService {
    void handleCreateCompany(Company company);

    ResultPaginationDTO getAllCompanies(Pageable pageable);

    Company getOneCompanyById(Long id);

    Company handleUpdateCompany(Long id, Company company);

    void handleDeleteCompanyById(Long id);
}
