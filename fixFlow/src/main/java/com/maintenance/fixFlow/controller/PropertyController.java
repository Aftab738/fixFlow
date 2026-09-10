package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.PropertyRequestDto;
import com.maintenance.fixFlow.dto.PropertyResponseDto;
import com.maintenance.fixFlow.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/property")
public class PropertyController {

    private final PropertyService propertyService;
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    public PropertyResponseDto createProperty(@Valid @RequestBody PropertyRequestDto dto){
        return propertyService.createProperty(dto);
    }

    @GetMapping("/{id}")
    public PropertyResponseDto getPropertyById(@PathVariable Long id){
        return propertyService.getPropertyById(id);
    }

    @GetMapping("/getAll")
    public List<PropertyResponseDto> getAll(){
        return propertyService.getAllProperties();
    }

    @PutMapping("/{id}")
    public PropertyResponseDto updateProperty(@Valid @RequestBody PropertyRequestDto dto
                                              ,@PathVariable Long id){
        return propertyService.updateProperty(dto,id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id){
        return propertyService.deleteProperty(id);
    }


}
