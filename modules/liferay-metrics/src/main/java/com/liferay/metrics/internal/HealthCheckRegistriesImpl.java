package com.liferay.metrics.internal;

import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.liferay.metrics.HealthCheckRegistries;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * class HealthCheckRegistriesImpl: Implementation of the health check registries component.
 *
 * @author dnebinger
 */
@Component(
        immediate = true,
        service = HealthCheckRegistries.class
)
public class HealthCheckRegistriesImpl implements HealthCheckRegistries {
    @Override
    public HealthCheckRegistry getPortalHealthCheckRegistry() {
        return SharedHealthCheckRegistries.getOrCreate(RegistryNameConstants.PORTAL_REGISTRY);
    }

    @Override
    public HealthCheckRegistry getGroupHealthCheckRegistry(long groupId) {
        return SharedHealthCheckRegistries.getOrCreate(RegistryNameConstants.GROUP_REGISTRY_PREFIX + groupId);
    }

    @Override
    public HealthCheckRegistry getCompanyHealthCheckRegistry(long companyId) {
        return SharedHealthCheckRegistries.getOrCreate(RegistryNameConstants.COMPANY_REGISTRY_PREFIX + companyId);
    }

    @Override
    public HealthCheckRegistry getPortletHealthCheckRegistry(String plid) {
        return SharedHealthCheckRegistries.getOrCreate(RegistryNameConstants.PORTLET_REGISTRY_PREFIX + plid);
    }

    @Override
    public HealthCheckRegistry getCustomHealthCheckRegistry(String registryName) {
        return SharedHealthCheckRegistries.getOrCreate(registryName);
    }

    @Override
    public List<String> getRegistryNames() {
        List<String> names = new ArrayList<>();

        Set<String> current = SharedMetricRegistries.names();

        if ((current != null) && (! current.isEmpty())) {
            names.addAll(current);
        }

        return names;
    }

    @Activate
    public void activate() {

        // create the global registry
        SharedHealthCheckRegistries.getOrCreate(RegistryNameConstants.PORTAL_REGISTRY);

        // and mark it as the default
        SharedHealthCheckRegistries.setDefault(RegistryNameConstants.PORTAL_REGISTRY);
    }
}
