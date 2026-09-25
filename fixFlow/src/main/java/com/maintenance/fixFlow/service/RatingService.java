package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.RatingRequestDto;
import com.maintenance.fixFlow.dto.RatingResponseDto;
import com.maintenance.fixFlow.dto.RatingUpdateDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.exception.BusinessException;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.RatingMapper;
import com.maintenance.fixFlow.repository.AssignmentRepository;
import com.maintenance.fixFlow.repository.RatingRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean tenantRole = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenantRole = true;
            }
        }

        User vendor = userRepository.findById(dto.getVendorId()).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + dto.getVendorId()
                                ));

        User tenant;

        if (tenantRole) {
            tenant = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!maintenanceRequest.getReportedBy()
                    .getId().equals(tenant.getId())) {

                throw new AccessDeniedException("You can rate only your own maintenance request");
            }

        }
        else if (manager) {
            tenant = userRepository.findById(dto.getTenantId()).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with id: "
                                            + dto.getTenantId()
                            ));

        }
        else {
            throw new AccessDeniedException("You are not allowed to create a Rating");
        }

        if (maintenanceRequest.getStatus() != MaintenanceStatus.COMPLETED) {
            throw new BusinessException("Only completed maintenance requests can be rated");
        }

        if (!maintenanceRequest.getReportedBy().getId().equals(tenant.getId())) {
            throw new BusinessException("Only the tenant who reported the request can rate it");
        }

        List<Assignment> assignments = assignmentRepository.findByMaintenanceRequestId(maintenanceRequest.getId());

        boolean assigned = false;

        for (Assignment a : assignments) {
            if (a.getVendor().getId().equals(vendor.getId())) {
                assigned = true;
                break;
            }
        }

        if (!assigned) {
            throw new BusinessException("Vendor was not assigned to this maintenance request");
        }

        Optional<Rating> existing = ratingRepository.findByMaintenanceRequestId(
                        maintenanceRequest.getId()
                );

        if (existing.isPresent()) {
            throw new BusinessException(
                    "Maintenance request has already been rated"
            );
        }

        Rating rating = RatingMapper.toEntity(
                dto,
                maintenanceRequest,
                vendor,
                tenant
        );

        Rating savedRating = ratingRepository.save(rating);

        return RatingMapper.toResponseDto(savedRating);
    }

    public RatingResponseDto getRatingById(Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException("Rating not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (manager) {
            return RatingMapper.toResponseDto(rating);
        }

        if (tenant) {
            if (!rating.getTenant().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to view this Rating."
                );
            }
        }
        else if (vendor) {
            if (!rating.getVendor().getEmail().equals(email)) {
                throw new AccessDeniedException("You are not allowed to view this Rating.");
            }
        }
        else {
            throw new AccessDeniedException("You are not allowed to view this Rating.");
        }

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
            RatingUpdateDto dto,
            Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rating not found with id: " + id
                                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        if (!manager) {
            if (!rating.getTenant().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to update this Rating."
                );
            }
        }

        rating.setScore(dto.getScore());
        rating.setComment(dto.getComment());

        Rating savedRating = ratingRepository.save(rating);

        return RatingMapper.toResponseDto(savedRating);
    }

    public String deleteRating(Long id) {

        Rating rating = ratingRepository.findById(id).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rating not found with id: " + id
                                ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
        }

        if (!manager) {
            if (!rating.getTenant().getEmail().equals(email)) {
                throw new AccessDeniedException(
                        "You are not allowed to delete this Rating."
                );
            }
        }

        ratingRepository.delete(rating);

        return "Rating Deleted";
    }

    public Optional<RatingResponseDto> getRatingByMaintenanceRequestId(
            Long maintenanceRequestId) {

        Optional<Rating> rating = ratingRepository.findByMaintenanceRequestId(
                        maintenanceRequestId
                );

        if (rating.isEmpty()) {
            return Optional.empty();
        }

        Rating r = rating.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendor = false;
        boolean tenant = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendor = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenant = true;
            }
        }

        if (manager) {
            return Optional.of(RatingMapper.toResponseDto(r));
        }

        if (tenant) {
            if (!r.getTenant().getEmail().equals(email)) {
                throw new AccessDeniedException("You are not allowed to view this Rating.");
            }
        }
        else if (vendor) {
            if (!r.getVendor().getEmail().equals(email)) {
                throw new AccessDeniedException("You are not allowed to view this Rating.");
            }
        }
        else {
            throw new AccessDeniedException("You are not allowed to view this Rating.");
        }

        return Optional.of(RatingMapper.toResponseDto(r));
    }

    public List<RatingResponseDto> getRatingsByVendorId(
            Long vendorId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean vendorRole = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_VENDOR")) {
                vendorRole = true;
            }
        }

        if (vendorRole) {
            User vendor = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!vendor.getId().equals(vendorId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Ratings."
                );
            }
        }
        else if (!manager) {
            throw new AccessDeniedException("You are not allowed to view these Ratings.");
        }

        List<Rating> list = ratingRepository.findByVendorId(vendorId);

        List<RatingResponseDto> res = new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }

    public List<RatingResponseDto> getRatingsByTenantId(
            Long tenantId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;
        boolean tenantRole = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
            }
            else if (a.getAuthority().equals("ROLE_TENANT")) {
                tenantRole = true;
            }
        }

        if (tenantRole) {
            User tenant = userRepository.findByEmail(email).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + email
                            ));

            if (!tenant.getId().equals(tenantId)) {
                throw new AccessDeniedException(
                        "You are not allowed to view these Ratings."
                );
            }
        }
        else if (!manager) {
            throw new AccessDeniedException("You are not allowed to view these Ratings.");
        }

        List<Rating> list = ratingRepository.findByTenantId(tenantId);

        List<RatingResponseDto> res = new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }
        return res;
    }
}