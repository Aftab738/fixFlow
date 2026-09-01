package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.RatingRequestDto;
import com.maintenance.fixFlow.dto.RatingResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.Rating;
import com.maintenance.fixFlow.entity.User;

public class RatingMapper {

    public static Rating toEntity(RatingRequestDto dto,
                                  MaintenanceRequest maintenanceRequest, User vendor, User tenant){
        Rating rating=new Rating();

        rating.setComment(dto.getComment());
        rating.setScore(dto.getScore());
        rating.setTenant(tenant);
        rating.setMaintenanceRequest(maintenanceRequest);
        rating.setVendor(vendor);

        return rating;
    }

    public static RatingResponseDto toResponseDto(Rating rating){
        RatingResponseDto dto=new RatingResponseDto();

        dto.setId(rating.getId());
        dto.setComment(rating.getComment());
        dto.setScore(rating.getScore());
        dto.setCreatedAt(rating.getCreatedAt());

        if(rating.getTenant()!=null){
            dto.setTenantId(rating.getTenant().getId());
            dto.setTenantName(rating.getTenant().getName());
        }

        if(rating.getVendor()!=null){
            dto.setVendorId(rating.getVendor().getId());
            dto.setVendorName(rating.getVendor().getName());
        }

        if(rating.getMaintenanceRequest()!=null){
            dto.setMaintenanceRequestId(rating.getMaintenanceRequest().getId());
        }

        return dto;
    }
}
