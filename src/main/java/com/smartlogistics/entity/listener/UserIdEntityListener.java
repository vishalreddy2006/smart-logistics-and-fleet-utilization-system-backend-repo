package com.smartlogistics.entity.listener;

import jakarta.persistence.PrePersist;
import org.springframework.stereotype.Component;

import com.smartlogistics.entity.Trip;
import com.smartlogistics.entity.Vehicle;
import com.smartlogistics.repository.UserRepository;
import com.smartlogistics.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class UserIdEntityListener {

    private static UserContext userContext;

    @Autowired
    public void setUserContext(UserContext userContext) {
        UserIdEntityListener.userContext = userContext;
    }

    @PrePersist
    public void onPrePersist(Object entity) {
        if (userContext == null) {
            return;
        }

        Long currentUserId = userContext.getCurrentUserIdOrNull();

        if (currentUserId != null) {
            if (entity instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) entity;
                if (vehicle.getUserId() == null) {
                    vehicle.setUserId(currentUserId);
                }
            } else if (entity instanceof Trip) {
                Trip trip = (Trip) entity;
                if (trip.getUserId() == null) {
                    trip.setUserId(currentUserId);
                }
            }
        }
    }
}
