package com.projectmanagement.repository;

import com.projectmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     */
    List<User> findByRole(String role);

    /**
     * Find active users by role
     */
    List<User> findByRoleAndIsActiveTrue(String role);

    /**
     * Find all active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(String role, Pageable pageable);

    /**
     * Find active users with pagination
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Search users by name or email with pagination
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);

    /**
     * Search users by name or email and role with pagination
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByRoleAndSearchTerm(@Param("role") String role, @Param("search") String search, Pageable pageable);

    /**
     * Find active users by search term with pagination
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findActiveBySearchTerm(@Param("search") String search, Pageable pageable);

    /**
     * Find active users by role and search term with pagination
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findActiveByRoleAndSearchTerm(@Param("role") String role, @Param("search") String search, Pageable pageable);

    /**
     * Count users by role
     */
    long countByRole(String role);

    /**
     * Count active users by role
     */
    long countByRoleAndIsActiveTrue(String role);

    /**
     * Count total active users
     */
    long countByIsActiveTrue();
} 