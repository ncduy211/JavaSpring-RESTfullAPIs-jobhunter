package vn.duync.jobhunter.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duync.jobhunter.domain.Company;
import vn.duync.jobhunter.domain.response.ResultPaginationDTO;
import vn.duync.jobhunter.repository.CompanyRepository;
import vn.duync.jobhunter.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void handleCreateCompany(Company company) {
        this.companyRepository.save(company);
    }

    public Company getOneCompanyById(Long id) {
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            return optionalCompany.get();
        }
        return null;
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPage = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageCompany.getNumber() + 1);
        meta.setPageSize(pageCompany.getSize());
        meta.setTotal(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());

        resultPage.setMeta(meta);
        resultPage.setResult(pageCompany.getContent());
        return resultPage;
    }

    public Company handleUpdateCompany(Long id, Company company) {
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        if (optionalCompany.isPresent()) {
            Company currentCompany = optionalCompany.get();
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());

            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompanyById(Long id) {
        this.companyRepository.deleteById(id);
    }

}
