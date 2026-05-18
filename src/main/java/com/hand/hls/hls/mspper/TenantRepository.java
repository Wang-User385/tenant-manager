package com.hand.hls.hls.mspper;
import com.hand.hls.hls.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    
    List<TenantEntity> findByNameContainingIgnoreCase(String name);
    
    List<TenantEntity> findByStatus(String status);
    
    @Query("SELECT t FROM TenantEntity t WHERE t.name LIKE %:keyword% OR t.contact LIKE %:keyword% OR t.phone LIKE %:keyword%")
    List<TenantEntity> searchTenants(@Param("keyword") String keyword);
}
