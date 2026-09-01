package com.maintenance.fixFlow.mapper;

import com.maintenance.fixFlow.dto.WorkUpdateRequestDto;
import com.maintenance.fixFlow.dto.WorkUpdateResponseDto;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.entity.WorkUpdate;

public class WorkUpdateMapper {
    public static WorkUpdate toEntity(WorkUpdateRequestDto dto,
                                      MaintenanceRequest maintenanceRequest, User vendor){
        WorkUpdate workUpdate=new WorkUpdate();

        workUpdate.setMessage(dto.getMessage());
        workUpdate.setMaintenanceRequest(maintenanceRequest);
        workUpdate.setVendor(vendor);

        return workUpdate;
    }

    public static WorkUpdateResponseDto toResponseDto(WorkUpdate workUpdate){
        WorkUpdateResponseDto dto=new WorkUpdateResponseDto();

        dto.setId(workUpdate.getId());
        dto.setMessage(workUpdate.getMessage());
        dto.setCreatedAt(workUpdate.getCreatedAt());

        if(workUpdate.getMaintenanceRequest()!=null){
            dto.setMaintenanceRequestId(workUpdate.getMaintenanceRequest().getId());
        }
        if(workUpdate.getVendor()!=null){
            dto.setVendorId(workUpdate.getVendor().getId());
            dto.setVendorName(workUpdate.getVendor().getName());
        }
        return dto;
    }


}
