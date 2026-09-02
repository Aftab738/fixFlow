package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.RatingRequestDto;
import com.maintenance.fixFlow.dto.RatingResponseDto;
import com.maintenance.fixFlow.entity.Rating;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.mapper.RatingMapper;
import com.maintenance.fixFlow.repository.RatingRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public RatingService(
            RatingRepository ratingRepository,
            UserRepository userRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {

        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public RatingResponseDto createRating(RatingRequestDto dto) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository
                        .findById(dto.getMaintenanceRequestId())
                        .orElse(null);

        User vendor =
                userRepository
                        .findById(dto.getVendorId())
                        .orElse(null);

        User tenant =
                userRepository
                        .findById(dto.getTenantId())
                        .orElse(null);

        Rating rating =
                RatingMapper.toEntity(
                        dto,
                        maintenanceRequest,
                        vendor,
                        tenant);

        Rating savedRating = ratingRepository.save(rating);

        return RatingMapper.toResponseDto(savedRating);
    }

    public RatingResponseDto getRatingById(Long id) {

        Optional<Rating> rating =
                ratingRepository.findById(id);

        if (rating.isPresent()) {
            return RatingMapper.toResponseDto(rating.get());
        }

        return null;
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

        Optional<Rating> existingRating =
                ratingRepository.findById(id);

        if (existingRating.isPresent()) {

            Rating rating = existingRating.get();

            MaintenanceRequest maintenanceRequest =
                    maintenanceRequestRepository
                            .findById(dto.getMaintenanceRequestId())
                            .orElse(null);

            User vendor =
                    userRepository
                            .findById(dto.getVendorId())
                            .orElse(null);

            User tenant =
                    userRepository
                            .findById(dto.getTenantId())
                            .orElse(null);

            rating.setScore(dto.getScore());
            rating.setComment(dto.getComment());
            rating.setMaintenanceRequest(maintenanceRequest);
            rating.setVendor(vendor);
            rating.setTenant(tenant);

            Rating savedRating =
                    ratingRepository.save(rating);

            return RatingMapper.toResponseDto(savedRating);
        }

        return null;
    }

    public String deleteRating(Long id) {

        Optional<Rating> rating =
                ratingRepository.findById(id);

        if (rating.isPresent()) {
            ratingRepository.delete(rating.get());
            return "Rating Deleted";
        }

        return "Rating not found";
    }

    public Optional<RatingResponseDto> getRatingByMaintenanceRequestId(
            Long maintenanceRequestId) {

        Optional<Rating> rating =
                ratingRepository.findByMaintenanceRequestId(
                        maintenanceRequestId);

        if (rating.isPresent()) {
            return Optional.of(
                    RatingMapper.toResponseDto(rating.get()));
        }

        return Optional.empty();
    }

    public List<RatingResponseDto> getRatingsByVendorId(Long vendorId) {

        List<Rating> list =
                ratingRepository.findByVendorId(vendorId);

        List<RatingResponseDto> res = new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }

    public List<RatingResponseDto> getRatingsByTenantId(Long tenantId) {

        List<Rating> list =
                ratingRepository.findByTenantId(tenantId);

        List<RatingResponseDto> res = new ArrayList<>();

        for (Rating rating : list) {
            res.add(RatingMapper.toResponseDto(rating));
        }

        return res;
    }
}