package com.loanlens.service;

import com.loanlens.dto.response.RiskAlertResponse;
import com.loanlens.entity.RiskAlert;
import com.loanlens.enums.AlertStatus;
import com.loanlens.exception.ResourceNotFoundException;
import com.loanlens.repository.RiskAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAlertService {

    private final RiskAlertRepository riskAlertRepository;

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAllAlerts() {
        return riskAlertRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAlertsByOfficer(Long officerId) {
        return riskAlertRepository.findByAssignedOfficerId(officerId).stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getOpenAlertsByOfficer(Long officerId) {
        return riskAlertRepository.findOpenAlertsByOfficer(officerId).stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAlertsByLoan(Long loanId) {
        return riskAlertRepository.findLatestAlertsByLoan(loanId).stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RiskAlertResponse> getAlertsByStatus(AlertStatus status) {
        return riskAlertRepository.findByStatus(status).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public RiskAlertResponse updateAlertStatus(Long alertId, AlertStatus status, String notes) {
        RiskAlert alert = riskAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));
        alert.setStatus(status);
        if (notes != null && !notes.isBlank()) {
            alert.setNotes(notes);
        }
        riskAlertRepository.save(alert);
        return mapToResponse(alert);
    }

    private RiskAlertResponse mapToResponse(RiskAlert ra) {
        return RiskAlertResponse.builder()
                .id(ra.getId())
                .loanId(ra.getLoan().getId())
                .borrowerName(ra.getLoan().getBorrower().getFullName())
                .previousTier(ra.getPreviousTier())
                .newTier(ra.getNewTier())
                .alertDate(ra.getAlertDate())
                .status(ra.getStatus())
                .assignedOfficerUsername(ra.getAssignedOfficer() != null ? ra.getAssignedOfficer().getUsername() : null)
                .notes(ra.getNotes())
                .build();
    }
}
