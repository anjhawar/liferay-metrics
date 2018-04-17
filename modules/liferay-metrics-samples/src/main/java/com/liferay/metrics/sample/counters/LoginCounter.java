package com.liferay.metrics.sample.counters;

import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.util.Portal;
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
                "key=login.events.post"
        },
        service = LifecycleAction.class
)
public class LoginCounter extends Action {

    /**
     * run: Entry point for the LoginPostAction.
     * @param request
     * @param response
     * @throws ActionException
     */
    @Override
    public void run(HttpServletRequest request, HttpServletResponse response) throws ActionException {

        // get the portal registry, the Liferay Logins counter, and increment it.
        _metricRegistries.getPortalMetricRegistry().counter("Liferay Logins").inc();

        // also take care of the company-specific logins...
        long companyId = _portal.getCompanyId(request);

        _metricRegistries.getCompanyMetricRegistry(companyId).counter("Company " + companyId + " Logins").inc();
    }

    @Activate
    protected void activate() {
        // pre-create the counter
        _metricRegistries.getPortalMetricRegistry().counter("Liferay Logins");
    }

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    @Reference(unbind = "-")
    protected void setPortal(final Portal portal) {
        _portal = portal;
    }

    private MetricRegistries _metricRegistries;
    private Portal _portal;
}
