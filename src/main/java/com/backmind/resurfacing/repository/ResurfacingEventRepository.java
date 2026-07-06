package com.backmind.resurfacing.repository;

import com.backmind.resurfacing.entity.ResurfacingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResurfacingEventRepository extends JpaRepository<ResurfacingEvent, UUID> {
}
