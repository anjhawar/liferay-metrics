package com.liferay.metrics.sample.counters;

import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * class LoginCounter: A login post processor that increments the logins metric counter.
 */
@Component(
        immediate = true,
        property = {
                "key=logout.events.post"
        },
        service = LifecycleAction.class
)
public class LogoutCounter extends Action {

    /**
     * run: Entry point for the LoginPostAction.
     * @param request
     * @param response
     * @throws ActionException
     */
    @Override
    public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {

        // get the portal registry, the Liferay Logins counter, and increment it.
        _metricRegistries.getPortalMetricRegistry().counter("Liferay Logouts").inc();
    }

    @Activate
    protected void activate() {
        // pre-create the counter
        _metricRegistries.getPortalMetricRegistry().counter("Liferay Logouts");
    }

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    private MetricRegistries _metricRegistries;
}
