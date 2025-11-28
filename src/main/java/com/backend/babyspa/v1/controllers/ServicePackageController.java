package com.backend.babyspa.v1.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.dtos.CreateServicePackageDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateServicePackageDto;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.services.ServicePackageService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-package")
public class ServicePackageController {

    @Autowired
    ServicePackageService servicePackageService;

    @GetMapping("/find-by-id")
    public ResponseEntity<ApiResponse<ServicePackage>> findById(@RequestParam Integer servicePackageId) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.findById(servicePackageId)));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<ServicePackage>> save(
            @RequestBody @Valid CreateServicePackageDto createServicePackageDto) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.save(createServicePackageDto)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ServicePackage>> update(
            @RequestBody @Valid UpdateServicePackageDto updateServicePackageDto) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.update(updateServicePackageDto)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int servicePackageId) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.delete(servicePackageId)));
    }

    @GetMapping("/find-all-list")
    public ResponseEntity<ApiResponse<List<ShortDetailsDto>>> findAllList() {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.findAllList()));
    }

    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<Page<ServicePackage>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchText,
                                                                     @RequestParam(required = false) BigDecimal startPrice,
                                                                     @RequestParam(required = false) BigDecimal endPrice) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.findAll(page, size, searchText, startPrice, endPrice)));
    }

    @GetMapping("/find-max-price")
    public ResponseEntity<ApiResponse<Double>> findMaxPrice() {

        return ResponseEntity.ok(ApiResponse.success(servicePackageService.findMaxPriceServicePackage()));
    }
}
