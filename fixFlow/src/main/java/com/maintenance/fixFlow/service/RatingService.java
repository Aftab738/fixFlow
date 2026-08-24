package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Rating;
import com.maintenance.fixFlow.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id).orElse(null);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating updateRating(Rating rating, Long id) {

        Optional<Rating> existingRating =
                ratingRepository.findById(id);

        if (existingRating.isPresent()) {

            Rating r = existingRating.get();

            r.setScore(rating.getScore());
            r.setComment(rating.getComment());
            r.setMaintenanceRequest(rating.getMaintenanceRequest());
            r.setVendor(rating.getVendor());
            r.setTenant(rating.getTenant());

            return ratingRepository.save(r);
        }

        return null;
    }

    public String deleteRating(Long id) {

        Optional<Rating> existingRating =
                ratingRepository.findById(id);

        if (existingRating.isPresent()) {
            ratingRepository.deleteById(id);
            return "Rating Deleted";
        }

        return "Rating not found";
    }

    public Rating getRatingByMaintenanceRequestId(Long maintenanceRequestId) {
        return ratingRepository
                .findByMaintenanceRequestId(maintenanceRequestId)
                .orElse(null);
    }

    public List<Rating> getRatingsByVendorId(Long vendorId) {
        return ratingRepository.findByVendorId(vendorId);
    }

    public List<Rating> getRatingsByTenantId(Long tenantId) {
        return ratingRepository.findByTenantId(tenantId);
    }
}