package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.RatingRequestDto;
import com.maintenance.fixFlow.dto.RatingResponseDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.RatingMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.RatingRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final AssignmentRepository assignmentRepository;

    public RatingService(
            RatingRepository ratingRepository,
            UserRepository userRepository,
            MaintenanceRequestRepository maintenanceRequestRepository,
            AssignmentRepository assignmentRepository) {

        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public RatingResponseDto createRating(RatingRequestDto dto) {

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor = userRepository.findById(dto.getVendorId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        User tenant = userRepository.findById(dto.getTenantId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getTenantId()
                                ));

        if (maintenanceRequest.getStatus() != MaintenanceStatus.COMPLETED) {
            throw new BusinessException(
                    "Only completed maintenance requests can be rated"
            );
        }

        if (!maintenanceRequest.getReportedBy().getId()
                .equals(tenant.getId())) {

            throw new BusinessException(
                    "Only the tenant who reported the request can rate it"
            );
        }

        List<Assignment> assignments =
                assignmentRepository.findByMaintenanceRequestId(
                        maintenanceRequest.getId());

        boolean assigned = false;

        for (Assignment a : assignments) {

            if (a.getVendor().getId().equals(vendor.getId())) {
                assigned = true;
                break;
            }
        }

        if (!assigned) {
            throw new BusinessException(
                    "Vendor was not assigned to this maintenance request"
            );
        }

        Optional<Rating> existing = ratingRepository.findByMaintenanceRequestId(
                        maintenanceRequest.getId());

        if (existing.isPresent()) {
            throw new BusinessException(
                    "Maintenance request has already been rated"
            );
        }

        Rating rating =RatingMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor,
                        tenant);

        Rating savedRating = ratingRepository.save(rating);

        return RatingMapper.toResponseDto(savedRating);
    }

    public RatingResponseDto getRatingById(Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rating not found with id: " + id
                                ));

        return RatingMapper.toResponseDto(rating);
    }

    public List<RatingResponseDto> getAllRatings() {

        List<Rating> list = ratingRepository.findAll();

        List<RatingResponseDto> res = new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }

    public RatingResponseDto updateRating(
            RatingRequestDto dto,
            Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rating not found with id: " + id
                                ));

        MaintenanceRequest maintenanceRequest = maintenanceRequestRepository.findById(dto.getMaintenanceRequestId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found with id: "
                                                + dto.getMaintenanceRequestId()
                                ));

        User vendor = userRepository.findById(dto.getVendorId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        User tenant = userRepository.findById(dto.getTenantId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getTenantId()
                                ));

        rating.setScore(dto.getScore());
        rating.setComment(dto.getComment());
        rating.setMaintenanceRequest(maintenanceRequest);
        rating.setVendor(vendor);
        rating.setTenant(tenant);

        Rating savedRating =
                ratingRepository.save(rating);

        return RatingMapper.toResponseDto(savedRating);
    }

    public String deleteRating(Long id) {

        Rating rating = ratingRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rating not found with id: " + id
                                ));

        ratingRepository.delete(rating);

        return "Rating Deleted";
    }

    public Optional<RatingResponseDto> getRatingByMaintenanceRequestId(
            Long maintenanceRequestId) {

        Optional<Rating> rating = ratingRepository.findByMaintenanceRequestId(
                        maintenanceRequestId);

        if (rating.isPresent()) {
            return Optional.of(
                    RatingMapper.toResponseDto(rating.get()));
        }

        return Optional.empty();
    }

    public List<RatingResponseDto> getRatingsByVendorId(
            Long vendorId) {

        List<Rating> list = ratingRepository.findByVendorId(vendorId);

        List<RatingResponseDto> res =
                new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }

    public List<RatingResponseDto> getRatingsByTenantId(
            Long tenantId) {

        List<Rating> list = ratingRepository.findByTenantId(tenantId);

        List<RatingResponseDto> res =
                new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }
}