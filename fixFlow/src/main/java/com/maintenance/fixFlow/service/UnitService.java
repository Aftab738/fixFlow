package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public Unit createUnit(Unit unit){
        return unitRepository.save(unit);
    }

    public Unit getUnitById(Long id){
        return unitRepository.findById(id).orElse(null);
    }

    public List<Unit> getAllUnits(){
        return unitRepository.findAll();
    }

    public Unit updateUnit(Unit unit,Long id){
        Optional<Unit> unit1=unitRepository.findById(id);

        if(unit1.isPresent()){
            Unit u=unit1.get();

            u.setFloor(unit.getFloor());
            u.setUnitNumber(unit.getUnitNumber());
            u.setProperty(unit.getProperty());

            return unitRepository.save(u);
        }

        return null;
    }

    public String deleteUnit(Long id){
        Optional<Unit> unit=unitRepository.findById(id);

        if(unit.isPresent()){
            unitRepository.delete(unit.get());
            return "Unit removed";
        }
        return "Unit not found";
    }

}
