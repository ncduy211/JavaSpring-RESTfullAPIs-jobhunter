package vn.duync.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.duync.jobhunter.domain.Company;
import vn.duync.jobhunter.domain.response.ResultPaginationDTO;

public interface CompanyService {
    void handleCreateCompany(Company company);

    ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable);

    Company getOneCompanyById(Long id);

    Company handleUpdateCompany(Long id, Company company);

    void handleDeleteCompanyById(Long id);
}
