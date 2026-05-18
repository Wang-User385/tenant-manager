package com.hand.hls.hls.service;
import com.hand.hls.hls.dto.TenantDTO;
import com.hand.hls.hls.entity.TenantEntity;
import com.hand.hls.hls.mspper.TenantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public List<TenantDTO> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TenantDTO getTenantById(Long id) {
        TenantEntity entity = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("租户不存在，ID: " + id));
        return convertToDTO(entity);
    }

    public TenantDTO createTenant(TenantDTO tenantDTO) {
        TenantEntity entity = convertToEntity(tenantDTO);
        entity.setId(null);
        TenantEntity savedEntity = tenantRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
        TenantEntity existingEntity = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("租户不存在，ID: " + id));
        
        BeanUtils.copyProperties(tenantDTO, existingEntity, "id", "createdAt", "updatedAt");
        TenantEntity updatedEntity = tenantRepository.save(existingEntity);
        return convertToDTO(updatedEntity);
    }

    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new RuntimeException("租户不存在，ID: " + id);
        }
        tenantRepository.deleteById(id);
    }

    public List<TenantDTO> searchTenants(String keyword) {
        return tenantRepository.searchTenants(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TenantDTO convertToDTO(TenantEntity entity) {
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private TenantEntity convertToEntity(TenantDTO dto) {
        TenantEntity entity = new TenantEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
