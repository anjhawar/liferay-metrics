package com.liferay.metrics;

import com.codahale.metrics.MetricRegistry;

import java.util.List;

/**
 * class MetricRegistries: A component interface class to get access to individual metric registries.
 *
 * @author dnebinger
 */
public interface MetricRegistries {

    /**
     * getPortalMetricRegistry: Returns the global registry for the node.
     * @return MetricRegistry The registry to use.
     */
    MetricRegistry getPortalMetricRegistry();

    /**
     * getGroupMetricRegistry: Returns the registry for the given group (site) id.
     * @param groupId The group (site) id.
     * @return MetricRegistry The registry to use.
     */
    MetricRegistry getGroupMetricRegistry(final long groupId);

    /**
     * getCompanyMetricRegistry: Returns the registry for the given company id.
     * @param companyId The company id.
     * @return MetricRegistry The registry to use.
     */
    MetricRegistry getCompanyMetricRegistry(final long companyId);

    /**
     * getPortletMetricRegistry: Returns the registry for the given portlet plid.
     * @param plid The portlet id string.
     * @return MetricRegistry The registry to use.
     */
    MetricRegistry getPortletMetricRegistry(final String plid);

    /**
     * getCustomMetricRegistry: Returns the registry for the given custom name.
     * @param registryName The name of the custom registry.
     * @return MetricRegistry The registry to use.
     */
    MetricRegistry getCustomMetricRegistry(final String registryName);

    /**
     * getRegistryNames: Returns the list of registry names.
     * @return List The list of names.
     */
    List<String> getRegistryNames();
}
