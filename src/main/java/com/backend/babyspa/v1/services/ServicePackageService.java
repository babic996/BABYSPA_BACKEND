package com.backend.babyspa.v1.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateServicePackageDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateServicePackageDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.ServicePackageRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicePackageService {

    @Autowired
    ServicePackageRepository servicePackageRepository;

    @Autowired
    ArrangementRepository arrangementRepository;

    @Autowired
    SecurityUtil securityUtil;

    public ServicePackage findById(Integer servicePackageId) throws NotFoundException {

        ServicePackage servicePackage = servicePackageRepository.findById(servicePackageId).orElseThrow(
                () -> new NotFoundException("Nije pronadjen paket usluge sa ID: " + servicePackageId + "!"));

        return servicePackage;
    }

    public ServicePackage save(CreateServicePackageDto createServicePackageDto) throws Exception {
        ServicePackage servicePackage = new ServicePackage();

        if (servicePackageRepository.existsByServicePackageNameAndIsDeleted(createServicePackageDto.getServicePackageName(), false)) {
            throw new Exception(
                    "Postoji paket usluge sa imenom: " + createServicePackageDto.getServicePackageName() + "!");
        }

        servicePackage.setServicePackageName(createServicePackageDto.getServicePackageName());
        servicePackage.setPrice(createServicePackageDto.getPrice());
        servicePackage.setServicePackageDurationDays(createServicePackageDto.getServicePackageDurationDays());
        servicePackage.setTermNumber(createServicePackageDto.getTermNumber());
        servicePackage.setNote(createServicePackageDto.getNote());
        servicePackage.setCreatedByUser(securityUtil.getCurrentUser());

        return servicePackageRepository.save(servicePackage);
    }

    public ServicePackage update(UpdateServicePackageDto updateServicePackageDto) throws Exception {
        ServicePackage servicePackage = findById(updateServicePackageDto.getServicePackageId());

        if (servicePackageRepository.existsByServicePackageNameAndServicePackageIdNotAndIsDeleted(
                updateServicePackageDto.getServicePackageName(), updateServicePackageDto.getServicePackageId(), false)) {
            throw new Exception(
                    "Postoji paket usluge sa imenom: " + updateServicePackageDto.getServicePackageName() + "!");
        }

        if (!arrangementRepository.existsByServicePackageAndIsDeleted(servicePackage, false)) {
            servicePackage.setServicePackageName(updateServicePackageDto.getServicePackageName());
            servicePackage.setServicePackageDurationDays(updateServicePackageDto.getServicePackageDurationDays());
            servicePackage.setTermNumber(updateServicePackageDto.getTermNumber());
        }

        servicePackage.setPrice(updateServicePackageDto.getPrice());
        servicePackage.setNote(updateServicePackageDto.getNote());
        servicePackage.setUpdatedByUser(securityUtil.getCurrentUser());

        return servicePackageRepository.save(servicePackage);
    }

    public Double findMaxPriceServicePackage() {

        return servicePackageRepository.findMaxPriceAndIsDeleted(false);
    }

    @Transactional
    public int delete(int servicePackageId) throws NotFoundException {

        ServicePackage servicePackage = findById(servicePackageId);
        if (arrangementRepository.existsByServicePackageAndIsDeleted(servicePackage, false)) {
            throw new IllegalArgumentException("Nije moguće obrisati paket usluge ako postoji aranžman kojem je dodijeljen.");
        }

        servicePackageRepository.delete(servicePackage);

        return servicePackageId;
    }

    public List<ShortDetailsDto> findAllList() {

        return servicePackageRepository.findAllByTenantIdAndIsDeleted(TenantContext.getTenant(), false).stream()
                .map(x -> buildShortDetailsDtoFromServicePackage(x)).collect(Collectors.toList());
    }

    public List<ServicePackage> findAll() {

        return servicePackageRepository.findAllByTenantIdAndIsDeleted(TenantContext.getTenant(), false);
    }

    private ShortDetailsDto buildShortDetailsDtoFromServicePackage(ServicePackage servicePackage) {

        ShortDetailsDto shortDetailsDto = new ShortDetailsDto();
        shortDetailsDto.setId(servicePackage.getServicePackageId());
        shortDetailsDto.setValue(servicePackage.getServicePackageName());

        return shortDetailsDto;
    }

    public Page<ServicePackage> findAll(int page, int size, String searchText, BigDecimal startPrice,
                                        BigDecimal endPrice) {

        Pageable pageable = PageRequest.of(page, size);

        return servicePackageRepository.findAllServicePackageNative(searchText, startPrice, endPrice,
                TenantContext.getTenant(), false, pageable);
    }

    // ovaj se servis koristi samo za izvjestaje jer se izvjestaji generisu preko
    // schedulera
    public List<ServicePackage> findAllByTenantForReport(String tenantId) {

        return servicePackageRepository.findAllByTenantIdAndIsDeleted(tenantId, false);
    }

}
