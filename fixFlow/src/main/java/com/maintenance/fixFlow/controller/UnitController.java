package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.UnitRequestDto;
import com.maintenance.fixFlow.dto.UnitResponseDto;
import com.maintenance.fixFlow.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    public UnitResponseDto createUnit(@Valid @RequestBody UnitRequestDto dto){
        return unitService.createUnit(dto);
    }

    @GetMapping("/{id}")
    public UnitResponseDto getUnitById(@PathVariable Long id){
        return unitService.getUnitById(id);
    }

    @GetMapping("/getAll")
    public List<UnitResponseDto> getAllUnit(){
        return unitService.getAllUnits();
    }

    @PutMapping("/{id}")
    public UnitResponseDto updateUnit(@Valid @RequestBody UnitRequestDto dto,
                                      @PathVariable Long id){
        return unitService.updateUnit(dto,id);
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id){
        return unitService.deleteUnit(id);
    }


}
