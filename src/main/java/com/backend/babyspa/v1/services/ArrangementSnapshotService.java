package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.ArrangementSnapshot;
import com.backend.babyspa.v1.repositories.ArrangementSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ArrangementSnapshotService {

  @Autowired private ArrangementSnapshotRepository arrangementSnapshotRepository;

  @Transactional
  public ArrangementSnapshot save(Arrangement arrangement) {
    ArrangementSnapshot arrangementSnapshot = new ArrangementSnapshot();

    arrangementSnapshot.setNote(arrangement.getNote());
    arrangementSnapshot.setPrice(arrangement.getPrice());
    arrangementSnapshot.setRemainingTerm(arrangement.getRemainingTerm());
    arrangementSnapshot.setExtendDurationDays(arrangement.getExtendDurationDays());
    arrangementSnapshot.setBabyInfo(arrangement.getBaby().toString());
    arrangementSnapshot.setServicePackageInfo(arrangement.getServicePackage().toString());
    arrangementSnapshot.setDiscountInfo(
        Objects.nonNull(arrangement.getDiscount()) ? arrangement.getDiscount().toString() : "");
    arrangementSnapshot.setPaymentTypeInfo(
        Objects.nonNull(arrangement.getPaymentType())
            ? arrangement.getPaymentType().toString()
            : "");
    arrangementSnapshot.setGiftCardInfo(
        Objects.nonNull(arrangement.getGiftCard()) ? arrangement.getGiftCard().toString() : "");
    arrangementSnapshot.setStatusInfo(arrangement.getStatus().toString());

    return arrangementSnapshotRepository.save(arrangementSnapshot);
  }
}
