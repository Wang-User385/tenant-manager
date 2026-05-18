
package com.hand.hls.hls.controllers;
import com.hand.hls.hls.dto.TenantDTO;
import com.hand.hls.hls.result.ApiResponse;
import com.hand.hls.hls.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ApiResponse<List<TenantDTO>> getAllTenants() {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return ApiResponse.success(tenants);
    }

    @GetMapping("/{id}")
    public ApiResponse<TenantDTO> getTenantById(@PathVariable Long id) {
        try {
            TenantDTO tenant = tenantService.getTenantById(id);
            return ApiResponse.success(tenant);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<TenantDTO> createTenant(@RequestBody TenantDTO tenantDTO) {
        try {
            TenantDTO createdTenant = tenantService.createTenant(tenantDTO);
            return ApiResponse.success("租户创建成功", createdTenant);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建租户失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<TenantDTO> updateTenant(@PathVariable Long id, @RequestBody TenantDTO tenantDTO) {
        try {
            TenantDTO updatedTenant = tenantService.updateTenant(id, tenantDTO);
            return ApiResponse.success("租户更新成功", updatedTenant);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新租户失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTenant(@PathVariable Long id) {
        try {
            tenantService.deleteTenant(id);
            return ApiResponse.success("租户删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除租户失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<TenantDTO>> searchTenants(@RequestParam String keyword) {
        try {
            List<TenantDTO> tenants = tenantService.searchTenants(keyword);
            return ApiResponse.success(tenants);
        } catch (Exception e) {
            return ApiResponse.error(500, "搜索失败: " + e.getMessage());
        }
    }
}
