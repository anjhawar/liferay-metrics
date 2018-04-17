package com.liferay.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.liferay.metrics.MetricRegistries;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * class MetricRegistriesImpl: Implementation class for the metric registries access.
 *
 * @author dnebinger
 */
@Component(
        immediate = true,
        service = MetricRegistries.class
)
public class MetricRegistriesImpl implements MetricRegistries {
    @Override
    public MetricRegistry getPortalMetricRegistry() {
        return SharedMetricRegistries.getDefault();
    }

    @Override
    public MetricRegistry getGroupMetricRegistry(long groupId) {
        return SharedMetricRegistries.getOrCreate(RegistryNameConstants.GROUP_REGISTRY_PREFIX + groupId);
    }

    @Override
    public MetricRegistry getCompanyMetricRegistry(long companyId) {
        return SharedMetricRegistries.getOrCreate(RegistryNameConstants.COMPANY_REGISTRY_PREFIX + companyId);
    }

    @Override
    public MetricRegistry getPortletMetricRegistry(String plid) {
        return SharedMetricRegistries.getOrCreate(RegistryNameConstants.PORTLET_REGISTRY_PREFIX + plid);
    }

    @Override
    public MetricRegistry getCustomMetricRegistry(String registryName) {
        return SharedMetricRegistries.getOrCreate(registryName);
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
        SharedMetricRegistries.getOrCreate(RegistryNameConstants.PORTAL_REGISTRY);

        // and mark it as the default
        SharedMetricRegistries.setDefault(RegistryNameConstants.PORTAL_REGISTRY);
    }

}
