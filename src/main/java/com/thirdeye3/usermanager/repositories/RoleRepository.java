package com.thirdeye3.usermanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.thirdeye3.usermanager.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
