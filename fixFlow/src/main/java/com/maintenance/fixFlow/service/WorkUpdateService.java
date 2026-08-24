package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.WorkUpdate;
import com.maintenance.fixFlow.repository.WorkUpdateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkUpdateService {

    private final WorkUpdateRepository workUpdateRepository;

    public WorkUpdateService(WorkUpdateRepository workUpdateRepository) {
        this.workUpdateRepository = workUpdateRepository;
    }

    public WorkUpdate createWorkUpdate(WorkUpdate workUpdate) {
        return workUpdateRepository.save(workUpdate);
    }

    public WorkUpdate getWorkUpdateById(Long id) {
        return workUpdateRepository.findById(id).orElse(null);
    }

    public List<WorkUpdate> getAllWorkUpdates() {
        return workUpdateRepository.findAll();
    }

    public WorkUpdate updateWorkUpdate(WorkUpdate workUpdate, Long id) {

        Optional<WorkUpdate> existingWorkUpdate =
                workUpdateRepository.findById(id);

        if (existingWorkUpdate.isPresent()) {

            WorkUpdate w = existingWorkUpdate.get();

            w.setMessage(workUpdate.getMessage());
            w.setMaintenanceRequest(workUpdate.getMaintenanceRequest());
            w.setVendor(workUpdate.getVendor());

            return workUpdateRepository.save(w);
        }

        return null;
    }

    public String deleteWorkUpdate(Long id) {

        Optional<WorkUpdate> existingWorkUpdate =
                workUpdateRepository.findById(id);

        if (existingWorkUpdate.isPresent()) {
            workUpdateRepository.deleteById(id);
            return "Work update deleted";
        }

        return "Work update not found";
    }

    public List<WorkUpdate> getWorkUpdatesByMaintenanceRequestId(
            Long maintenanceRequestId) {

        return workUpdateRepository
                .findByMaintenanceRequestId(maintenanceRequestId);
    }

    public List<WorkUpdate> getWorkUpdatesByVendorId(Long vendorId) {

        return workUpdateRepository.findByVendorId(vendorId);
    }
}