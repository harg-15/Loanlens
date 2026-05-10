package com.loanlens.service;

import com.loanlens.dto.request.BorrowerRequest;
import com.loanlens.dto.response.BorrowerResponse;
import com.loanlens.entity.Borrower;
import com.loanlens.exception.BadRequestException;
import com.loanlens.exception.ResourceNotFoundException;
import com.loanlens.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Transactional
    public BorrowerResponse createBorrower(BorrowerRequest request) {
        if (borrowerRepository.existsByPanNumber(request.getPanNumber())) {
            throw new BadRequestException("PAN number already registered: " + request.getPanNumber());
        }
        if (borrowerRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        Borrower borrower = Borrower.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .panNumber(request.getPanNumber())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .employmentType(request.getEmploymentType())
                .annualIncome(request.getAnnualIncome())
                .build();

        borrowerRepository.save(borrower);
        return mapToResponse(borrower);
    }

    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowerById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found: " + id));
        return mapToResponse(borrower);
    }

    @Transactional(readOnly = true)
    public List<BorrowerResponse> getAllBorrowers() {
        return borrowerRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public BorrowerResponse updateBorrower(Long id, BorrowerRequest request) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found: " + id));

        borrower.setFullName(request.getFullName());
        borrower.setEmail(request.getEmail());
        borrower.setPhone(request.getPhone());
        borrower.setDateOfBirth(request.getDateOfBirth());
        borrower.setAddress(request.getAddress());
        borrower.setEmploymentType(request.getEmploymentType());
        borrower.setAnnualIncome(request.getAnnualIncome());

        borrowerRepository.save(borrower);
        return mapToResponse(borrower);
    }

    @Transactional
    public void deleteBorrower(Long id) {
        if (!borrowerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Borrower not found: " + id);
        }
        borrowerRepository.deleteById(id);
    }

    private BorrowerResponse mapToResponse(Borrower b) {
        return BorrowerResponse.builder()
                .id(b.getId())
                .fullName(b.getFullName())
                .email(b.getEmail())
                .phone(b.getPhone())
                .panNumber(b.getPanNumber())
                .dateOfBirth(b.getDateOfBirth())
                .address(b.getAddress())
                .employmentType(b.getEmploymentType())
                .annualIncome(b.getAnnualIncome())
                .createdAt(b.getCreatedAt())
                .build();
    }
}