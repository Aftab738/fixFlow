package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.UnitRequestDto;
import com.maintenance.fixFlow.dto.UnitResponseDto;
import com.maintenance.fixFlow.entity.Property;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.mapper.UnitMapper;
import com.maintenance.fixFlow.repository.PropertyRepository;
import com.maintenance.fixFlow.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;

    public UnitService(UnitRepository unitRepository,
                       PropertyRepository propertyRepository) {
        this.unitRepository = unitRepository;
        this.propertyRepository = propertyRepository;
    }

    public UnitResponseDto createUnit(UnitRequestDto dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElse(null);

        Unit unit = UnitMapper.toEntity(dto, property);
        Unit savedUnit = unitRepository.save(unit);

        return UnitMapper.toResponseDto(savedUnit);
    }

    public UnitResponseDto getUnitById(Long id) {
        Optional<Unit> unit = unitRepository.findById(id);
        if (unit.isPresent()) {
            return UnitMapper.toResponseDto(unit.get());
        }

        return null;
    }

    public List<UnitResponseDto> getAllUnits() {
        List<Unit> units = unitRepository.findAll();
        List<UnitResponseDto> result = new ArrayList<>();

        for (Unit unit : units) {
            result.add(UnitMapper.toResponseDto(unit));
        }

        return result;
    }

    public UnitResponseDto updateUnit(UnitRequestDto dto, Long id) {
        Optional<Unit> existingUnit = unitRepository.findById(id);
        if (existingUnit.isPresent()) {
            Unit unit = existingUnit.get();

            Property property = propertyRepository.findById(dto.getPropertyId())
                    .orElse(null);
            unit.setFloor(dto.getFloor());
            unit.setUnitNumber(dto.getUnitNumber());
            unit.setProperty(property);

            Unit savedUnit = unitRepository.save(unit);

            return UnitMapper.toResponseDto(savedUnit);
        }

        return null;
    }

    public String deleteUnit(Long id) {
        Optional<Unit> unit = unitRepository.findById(id);
        if (unit.isPresent()) {
            unitRepository.delete(unit.get());
            return "Unit removed";
        }

        return "Unit not found";
    }
}