package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.ArrangementAud;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.models.UserActionType;
import com.backend.babyspa.v1.repositories.ArrangementAudRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArrangementAudService {

    @Autowired
    ArrangementAudRepository arrangementAudRepository;

    @Autowired
    ArrangementSnapshotService arrangementSnapshotService;

    @Transactional
    public void save(Arrangement arrangement, User actionByUser, UserActionType userActionType) {
        ArrangementAud arrangementAud = new ArrangementAud();

        arrangementAud.setArrangementSnapshot(arrangementSnapshotService.save(arrangement));
        arrangementAud.setUserActionType(userActionType);
        arrangementAud.setArrangementId(arrangement.getArrangementId());
        arrangementAud.setActionByUser(actionByUser);

        arrangementAudRepository.save(arrangementAud);
    }
}
