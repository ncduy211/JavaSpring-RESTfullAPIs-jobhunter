package vn.duync.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duync.jobhunter.domain.Company;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duync.jobhunter.service.impl.CompanyServiceImpl;
import vn.duync.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class CompanyController {
    private CompanyServiceImpl companyServiceImpl;

    public CompanyController(CompanyServiceImpl companyServiceImpl) {
        this.companyServiceImpl = companyServiceImpl;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> create(@Valid @RequestBody Company newCompany) {
        this.companyServiceImpl.handleCreateCompany(newCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getOneCompanyById(@PathVariable("id") Long id) {
        Company company = this.companyServiceImpl.getOneCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @RequestParam("current") Optional<String> currentPageOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String stringCurrentPage = currentPageOptional.isPresent() ? currentPageOptional.get() : "";
        int currentPage = Integer.parseInt(stringCurrentPage);

        String stringPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        int pageSize = Integer.parseInt(stringPageSize);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        ResultPaginationDTO listCompanies = this.companyServiceImpl.getAllCompanies(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listCompanies);
    }

    @PutMapping("/companies/update/{id}")
    public ResponseEntity<Company> updateCompany(@Valid @PathVariable("id") Long id,
            @Valid @RequestBody Company updatedCompany) {
        Company company = this.companyServiceImpl.handleUpdateCompany(id, updatedCompany);
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }

    @DeleteMapping("/companies/delete/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable("id") Long id) throws IdInvalidException {
        this.companyServiceImpl.handleDeleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Company deleted successfully");
    }

}
