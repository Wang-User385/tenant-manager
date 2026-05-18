package com.hand.hls.hls.service;
import com.hand.TenantManagerApplication;
import com.hand.hls.hls.dto.TenantDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TenantManagerApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TenantServiceTest {

    // 1. 仅注入 Service，严禁出现 MockMvc
    @Autowired
    private TenantService tenantService;

    private static Long createdTenantId=1L;

    @Test
    @Order(1)
    void testCreateTenant() {
        TenantDTO tenant = new TenantDTO();
        tenant.setName("单元测试租户");
        tenant.setContact("测试员");
        tenant.setPhone("13800138000");
        tenant.setAddress("测试地址");
        tenant.setStatus("ACTIVE");

        // 2. 直接调用 Service 方法，不经过 HTTP
        TenantDTO created = tenantService.createTenant(tenant);

        // 3. 验证业务数据，而非 HTTP 状态码
        assertNotNull(created.getId(), "创建后 ID 不应为空");
        assertEquals("单元测试租户", created.getName(), "名称应一致");
        createdTenantId = created.getId();
    }

    @Test
    @Order(2)
    void testGetAllTenants() {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        assertNotNull(tenants);
        for (TenantDTO tenant : tenants) {
            System.out.println(tenant.toString());
        }
    }

    @Test
    @Order(3)
    void testGetTenantById() {
        TenantDTO tenant = tenantService.getTenantById(1L);
        assertNotNull(tenant);
        assertEquals(1L, tenant.getId());
        System.out.println(tenant.toString());
    }

    @Test
    @Order(4)
    void testUpdateTenant() {
        TenantDTO updateData = new TenantDTO();
        updateData.setName("更新后的名称");
        updateData.setContact("新联系人");

        TenantDTO updated = tenantService.updateTenant(1L, updateData);
        assertEquals("更新后的名称", updated.getName());
    }

    @Test
    @Order(5)
    void testSearchTenants() {
        List<TenantDTO> results = tenantService.searchTenants("更新后");
        assertFalse(results.isEmpty());
    }

    @Test
    @Order(6)
    void testDeleteTenant() {
        tenantService.deleteTenant(createdTenantId);
        assertThrows(RuntimeException.class, () -> tenantService.getTenantById(createdTenantId));
    }
}
