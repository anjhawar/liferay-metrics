package com.liferay.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;

import java.util.List;

public interface HealthCheckRegistries {
    /**
     * getPortalHealthCheckRegistry: Returns the global registry for the node.
     * @return HealthCheckRegistry The registry to use.
     */
    HealthCheckRegistry getPortalHealthCheckRegistry();

    /**
     * getGroupHealthCheckRegistry: Returns the registry for the given group (site) id.
     * @param groupId The group (site) id.
     * @return HealthCheckRegistry The registry to use.
     */
    HealthCheckRegistry getGroupHealthCheckRegistry(final long groupId);

    /**
     * getCompanyHealthCheckRegistry: Returns the registry for the given company id.
     * @param companyId The company id.
     * @return HealthCheckRegistry The registry to use.
     */
    HealthCheckRegistry getCompanyHealthCheckRegistry(final long companyId);

    /**
     * getPortletHealthCheckRegistry: Returns the registry for the given portlet plid.
     * @param plid The portlet id string.
     * @return HealthCheckRegistry The registry to use.
     */
    HealthCheckRegistry getPortletHealthCheckRegistry(final String plid);

    /**
     * getCustomHealthCheckRegistry: Returns the registry for the given custom name.
     * @param registryName The name of the custom registry.
     * @return HealthCheckRegistry The registry to use.
     */
    HealthCheckRegistry getCustomHealthCheckRegistry(final String registryName);

    /**
     * getRegistryNames: Returns the list of registry names.
     * @return List The list of names.
     */
    List<String> getRegistryNames();
}
