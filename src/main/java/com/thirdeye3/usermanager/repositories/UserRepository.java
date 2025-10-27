package com.thirdeye3.usermanager.repositories;

import com.thirdeye3.usermanager.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNameAndPassword(String userName, String password);

    Optional<User> findByUserName(String userName);

    List<User> findByActiveTrue();
    
    Long countByActiveTrue();
    
    @Query(value = "SELECT user_user_id AS userId, roles_id AS roleId FROM users_roles", nativeQuery = true)
    List<Object[]> findAllUserRoleMappings();

}
