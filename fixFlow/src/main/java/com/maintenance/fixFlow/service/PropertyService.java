package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.PropertyRequestDto;
import com.maintenance.fixFlow.dto.PropertyResponseDto;
import com.maintenance.fixFlow.entity.Property;
import com.maintenance.fixFlow.mapper.PropertyMapper;
import com.maintenance.fixFlow.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository){
        this.propertyRepository=propertyRepository;
    }

    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto){
        Property property= PropertyMapper.toEntity(propertyRequestDto);
        return PropertyMapper.toResponseDto(propertyRepository.save(property));
    }

    public PropertyResponseDto getPropertyById(Long id) {
        Optional<Property> property =
                propertyRepository.findById(id);
        if (property.isPresent()) {
            return PropertyMapper.toResponseDto(property.get());
        }
        return null;
    }

    public List<PropertyResponseDto> getAllProperties(){
        List<Property> propertyList= propertyRepository.findAll();
        List<PropertyResponseDto> result=new ArrayList<>();

        for(Property p:propertyList){
            result.add(PropertyMapper.toResponseDto(p));
        }
        return result;
    }

    public PropertyResponseDto updateProperty(PropertyRequestDto dto,Long id){
        Optional<Property> property=propertyRepository.findById(id);

        if(property.isPresent()){
            Property p=property.get();

            p.setName(dto.getName());
            p.setAddress(dto.getAddress());

            Property savedProperty= propertyRepository.save(p);
            return PropertyMapper.toResponseDto(savedProperty);

        }
        return null;
    }

    public String deleteProperty(Long id){
        Optional<Property> property=propertyRepository.findById(id);

        if(property.isPresent()){
            propertyRepository.delete(property.get());
            return "Property Deleted";
        }
        return "Property not found";
    }

}
