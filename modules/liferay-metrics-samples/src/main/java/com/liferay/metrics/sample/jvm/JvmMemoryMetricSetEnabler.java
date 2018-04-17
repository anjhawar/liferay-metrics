package com.liferay.metrics.sample.jvm;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

/**
 * class JvmMemoryMetricSetEnabler: Enables the JVM memory tracking stuff as a metric set in the portal registry.
 */
@Component(
        immediate = true
)
public class JvmMemoryMetricSetEnabler {

    @Activate
    protected void activate() {
        // NOTE: We only have to do this for a metric set registry, individuals are get or create actions.
        if (_metricRegistries.getPortalMetricRegistry().getMetrics().containsKey("total.init")) {
            _log.info("Memory statistics already registered.");

            return;
        }

        // not already registered, register now.
        _memoryUsageGauge = new MemoryUsageGaugeSet();

        // first we should see if the metric registries already has the metric set...
        _metricRegistries.getPortalMetricRegistry().registerAll(_memoryUsageGauge);
    }

    @Deactivate
    protected void deactivate() {
        if (Validator.isNotNull(_memoryUsageGauge)) {
            // we have an existing metric set, need to remove the items.
            removeAll(_memoryUsageGauge);

            _memoryUsageGauge = null;
        }
    }

    protected void removeAll(MetricSet metrics) throws IllegalArgumentException {
        for (Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                removeAll((MetricSet) entry.getValue());
            } else {
                _metricRegistries.getPortalMetricRegistry().remove(entry.getKey());
            }
        }
    }

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    private MetricSet _memoryUsageGauge;

    private MetricRegistries _metricRegistries;

    private static final Log _log = LogFactoryUtil.getLog(JvmMemoryMetricSetEnabler.class);
}
