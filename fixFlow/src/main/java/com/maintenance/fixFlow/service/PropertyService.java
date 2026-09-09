package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.PropertyRequestDto;
import com.maintenance.fixFlow.dto.PropertyResponseDto;
import com.maintenance.fixFlow.entity.Property;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.PropertyMapper;
import com.maintenance.fixFlow.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyResponseDto createProperty(
            PropertyRequestDto propertyRequestDto) {

        Property property =
                PropertyMapper.toEntity(propertyRequestDto);

        Property savedProperty =
                propertyRepository.save(property);

        return PropertyMapper.toResponseDto(savedProperty);
    }

    public PropertyResponseDto getPropertyById(Long id) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        ));

        return PropertyMapper.toResponseDto(property);
    }

    public List<PropertyResponseDto> getAllProperties() {

        List<Property> propertyList =
                propertyRepository.findAll();

        List<PropertyResponseDto> result =
                new ArrayList<>();

        for (Property p : propertyList) {
            result.add(PropertyMapper.toResponseDto(p));
        }

        return result;
    }

    public PropertyResponseDto updateProperty(
            PropertyRequestDto dto,
            Long id) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        ));

        property.setName(dto.getName());
        property.setAddress(dto.getAddress());

        Property savedProperty =
                propertyRepository.save(property);

        return PropertyMapper.toResponseDto(savedProperty);
    }

    public String deleteProperty(Long id) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Property not found with id: " + id
                        ));

        propertyRepository.delete(property);

        return "Property Deleted";
    }
}