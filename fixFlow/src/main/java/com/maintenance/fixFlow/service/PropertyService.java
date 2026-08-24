package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.entity.Property;
import com.maintenance.fixFlow.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository){
        this.propertyRepository=propertyRepository;
    }

    public Property createProperty(Property property){
        return propertyRepository.save(property);
    }

    public Property getPropertyById(Long id){
        return propertyRepository.findById(id).orElse(null);
    }

    public List<Property> getAllProperties(){
        return propertyRepository.findAll();
    }

    public Property updateProperty(Property prop,Long id){
        Optional<Property> property=propertyRepository.findById(id);

        if(property.isPresent()){
            Property p=property.get();

            p.setName(prop.getName());
            p.setAddress(prop.getAddress());

            return propertyRepository.save(p);

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
