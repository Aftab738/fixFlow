package com.maintenance.fixFlow.service;


import com.maintenance.fixFlow.dto.MaintenanceRequestRequestDto;
import com.maintenance.fixFlow.dto.MaintenanceRequestResponseDto;
import com.maintenance.fixFlow.entity.*;
import com.maintenance.fixFlow.mapper.MaintenanceRequestMapper;
import com.maintenance.fixFlow.repository.MaintenanceRequestRepository;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;

    public MaintenanceRequestService(MaintenanceRequestRepository maintenanceRequestRepository, UserRepository userRepository, UnitRepository unitRepository) {

        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
    }

    public MaintenanceRequestResponseDto createMaintenanceRequest(MaintenanceRequestRequestDto dto){
        Unit unit=unitRepository.findById(dto.getUnitId()).orElse(null);
        User user=userRepository.findById(dto.getReportedById()).orElse(null);

        MaintenanceRequest maintenanceRequest= MaintenanceRequestMapper.toEntity(dto,user,unit);
        MaintenanceRequest mr= maintenanceRequestRepository.save(maintenanceRequest);
        return MaintenanceRequestMapper.toResponseDto(mr);
    }

    public MaintenanceRequestResponseDto getMaintenanceRequestById(Long id){
        Optional<MaintenanceRequest> maintenanceRequest= maintenanceRequestRepository.findById(id);
        if(maintenanceRequest.isPresent()){
            return MaintenanceRequestMapper.toResponseDto(maintenanceRequest.get());
        }
        return null;
    }

    public List<MaintenanceRequestResponseDto> getAllRequests(){
        List<MaintenanceRequest> list= maintenanceRequestRepository.findAll();
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    public MaintenanceRequestResponseDto updateMaintenanceRequest(MaintenanceRequestRequestDto dto,Long id){
        Optional<MaintenanceRequest> maintenanceReq=
                maintenanceRequestRepository.findById(id);

        if(maintenanceReq.isPresent()){
            MaintenanceRequest mr=maintenanceReq.get();

            User user=userRepository.findById(dto.getReportedById()).orElse(null);
            Unit unit=unitRepository.findById(dto.getUnitId()).orElse(null);
            mr.setUnit(unit);
            mr.setCategory(dto.getCategory());
            mr.setDescription(dto.getDescription());
            mr.setPriority(dto.getPriority());
            mr.setTitle(dto.getTitle());
            mr.setReportedBy(user);
            mr.setStatus(dto.getStatus());

            MaintenanceRequest maintenanceRequest=maintenanceRequestRepository.save(mr);
            return MaintenanceRequestMapper.toResponseDto(maintenanceRequest);
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

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByStatus(
            MaintenanceStatus status) {
        List<MaintenanceRequest> list= maintenanceRequestRepository.findByStatus(status);
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByPriority(
            MaintenancePriority priority) {
        List<MaintenanceRequest> list= maintenanceRequestRepository.findByPriority(priority);
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByCategory(
            MaintenanceCategory category) {
        List<MaintenanceRequest> list= maintenanceRequestRepository.findByCategory(category);
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByUnitId(
            Long unitId) {
        List<MaintenanceRequest> list= maintenanceRequestRepository.findByUnitId(unitId);
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

    public List<MaintenanceRequestResponseDto> getMaintenanceRequestsByReportedById(
            Long userId) {
        List<MaintenanceRequest> list= maintenanceRequestRepository.findByReportedById(userId);
        List<MaintenanceRequestResponseDto> res=new ArrayList<>();

        for(MaintenanceRequest m:list){
            res.add(MaintenanceRequestMapper.toResponseDto(m));
        }
        return res;
    }

}
