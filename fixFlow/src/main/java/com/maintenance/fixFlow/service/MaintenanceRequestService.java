package com.maintenance.fixFlow.service;


import com.maintenance.fixFlow.entity.MaintenanceCategory;
import com.maintenance.fixFlow.entity.MaintenancePriority;
import com.maintenance.fixFlow.entity.MaintenanceRequest;
import com.maintenance.fixFlow.entity.MaintenanceStatus;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestService(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public MaintenanceRequest createMaintenanceRequest(MaintenanceRequest mReq){
        return maintenanceRequestRepository.save(mReq);
    }

    public MaintenanceRequest getMaintenanceRequestById(Long id){
        return maintenanceRequestRepository.findById(id).orElse(null);
    }

    public List<MaintenanceRequest> getAllRequests(){
        return maintenanceRequestRepository.findAll();
    }

    public MaintenanceRequest updateMaintenanceRequest(MaintenanceRequest mReq,Long id){
        Optional<MaintenanceRequest> maintenanceReq=
                maintenanceRequestRepository.findById(id);

        if(maintenanceReq.isPresent()){
            MaintenanceRequest mr=maintenanceReq.get();

            mr.setUnit(mReq.getUnit());
            mr.setCategory(mReq.getCategory());
            mr.setDescription(mReq.getDescription());
            mr.setPriority(mReq.getPriority());
            mr.setTitle(mReq.getTitle());
            mr.setReportedBy(mReq.getReportedBy());
            mr.setStatus(mReq.getStatus());

            return maintenanceRequestRepository.save(mr);
        }
        return null;
    }

    public String deleteMaintenanceRequest(Long id){
        Optional<MaintenanceRequest> maintenanceReq=
                maintenanceRequestRepository.findById(id);

        if(maintenanceReq.isPresent()){
            maintenanceRequestRepository.delete(maintenanceReq.get());
            return "Deleted";
        }
        return "Not found";
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByStatus(
            MaintenanceStatus status) {
        return maintenanceRequestRepository.findByStatus(status);
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByPriority(
            MaintenancePriority priority) {
        return maintenanceRequestRepository.findByPriority(priority);
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByCategory(
            MaintenanceCategory category) {
        return maintenanceRequestRepository.findByCategory(category);
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByUnitId(
            Long unitId) {
        return maintenanceRequestRepository.findByUnitId(unitId);
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByReportedById(
            Long userId) {
        return maintenanceRequestRepository.findByReportedById(userId);
    }

}
