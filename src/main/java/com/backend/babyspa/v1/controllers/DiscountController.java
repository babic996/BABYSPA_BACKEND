package com.backend.babyspa.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.dtos.CreateDiscountDto;
import com.backend.babyspa.v1.dtos.UpdateDiscountDto;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.services.DiscountService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/discount")
public class DiscountController {

    @Autowired
    DiscountService discountService;

    @GetMapping("/find-by-id")
    public ResponseEntity<ApiResponse<Discount>> findById(@RequestParam int discountId) {

        return ResponseEntity.ok(ApiResponse.success(discountService.findById(discountId)));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Discount>> save(@RequestBody @Valid CreateDiscountDto createDiscountDto) {

        return ResponseEntity.ok(ApiResponse.success(discountService.save(createDiscountDto)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Discount>> update(@RequestBody @Valid UpdateDiscountDto updateDiscountDto) {

        return ResponseEntity.ok(ApiResponse.success(discountService.update(updateDiscountDto)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int discountId) {

        return ResponseEntity.ok(ApiResponse.success(discountService.delete(discountId)));
    }

    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<List<Discount>>> findAll() {

        return ResponseEntity.ok(ApiResponse.success(discountService.findAll()));
    }
}
