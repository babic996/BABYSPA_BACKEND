package com.backend.babyspa.v1.services;

import java.util.List;

import com.backend.babyspa.v1.exceptions.BuisnessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateDiscountDto;
import com.backend.babyspa.v1.dtos.UpdateDiscountDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.repositories.DiscountRepository;

import jakarta.transaction.Transactional;

@Service
public class DiscountService {

    @Autowired
    DiscountRepository discountRepository;

    public Discount findById(Integer discountId) {

        return discountRepository.findById(discountId)
                .orElseThrow(() -> new NotFoundException("Nije pronađen popust sa ID: " + discountId + "!"));
    }

    public Discount save(CreateDiscountDto createDiscountDto) {
        if (discountRepository.existsByValueAndIsPrecentage(createDiscountDto.getValue(),
                createDiscountDto.getIsPrecentage())) {
            throw new BuisnessException("Postoji popust sa unijetim parametrima!");
        }
        Discount discount = new Discount();

        if (createDiscountDto.getIsPrecentage()) {
            discount.setDiscountName(createDiscountDto.getValue().toString() + "%");
        } else {
            discount.setDiscountName(createDiscountDto.getValue().toString() + "KM");
        }

        discount.setPrecentage(createDiscountDto.getIsPrecentage());
        discount.setValue(createDiscountDto.getValue());

        return discountRepository.save(discount);
    }

    public Discount update(UpdateDiscountDto updateDiscountDto) {
        if (discountRepository.existsByValueAndIsPrecentageAndDiscountIdNot(updateDiscountDto.getValue(),
                updateDiscountDto.getIsPrecentage(), updateDiscountDto.getDisountId())) {
            throw new BuisnessException("Postoji popust sa unijetim parametrima!");
        }
        Discount discount = findById(updateDiscountDto.getDisountId());

        if (updateDiscountDto.getIsPrecentage()) {
            discount.setDiscountName(updateDiscountDto.getValue().toString() + "%");
        } else {
            discount.setDiscountName(updateDiscountDto.getValue().toString() + "KM");
        }

        discount.setPrecentage(updateDiscountDto.getIsPrecentage());
        discount.setValue(updateDiscountDto.getValue());

        return discountRepository.save(discount);
    }

    @Transactional
    public int delete(int discountId) {
        Discount discount = findById(discountId);

        discountRepository.delete(discount);
        return discountId;
    }

    public List<Discount> findAll() {

        return discountRepository.findByTenantId(TenantContext.getTenant());
    }
}
