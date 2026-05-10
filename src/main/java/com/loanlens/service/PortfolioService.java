package com.loanlens.service;

import com.loanlens.dto.response.PortfolioSummaryResponse;
import com.loanlens.entity.PortfolioSnapshot;
import com.loanlens.exception.ResourceNotFoundException;
import com.loanlens.repository.PortfolioSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioSnapshotRepository snapshotRepository;

    public PortfolioSummaryResponse getLatestSnapshot() {
        PortfolioSnapshot snapshot = snapshotRepository.findTopByOrderBySnapshotDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No portfolio snapshot available yet"));
        return mapToResponse(snapshot);
    }

    public List<PortfolioSummaryResponse> getLast30Snapshots() {
        return snapshotRepository.findTop30ByOrderBySnapshotDateDesc().stream()
                .map(this::mapToResponse).toList();
    }

    private PortfolioSummaryResponse mapToResponse(PortfolioSnapshot s) {
        return PortfolioSummaryResponse.builder()
                .id(s.getId())
                .snapshotDate(s.getSnapshotDate())
                .totalActiveLoans(s.getTotalActiveLoans())
                .npaLoans(s.getNpaLoans())
                .npaRate(s.getNpaRate())
                .avgHealthScore(s.getAvgHealthScore())
                .totalExposure(s.getTotalExposure())
                .homeLoansExposure(s.getHomeLoansExposure())
                .personalLoansExposure(s.getPersonalLoansExposure())
                .vehicleLoansExposure(s.getVehicleLoansExposure())
                .standardCount(s.getStandardCount())
                .watchCount(s.getWatchCount())
                .stressCount(s.getStressCount())
                .npaCount(s.getNpaCount())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
