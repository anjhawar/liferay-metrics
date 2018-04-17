package com.liferay.metrics.internal.portlet;

import com.liferay.metrics.HealthCheckRegistries;
import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.metrics",
		"com.liferay.portlet.instanceable=false",
        "com.liferay.portlet.header-portlet-css=/css/main.css",
		"javax.portlet.display-name=Liferay Metrics",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=LiferayMetricsPortlet",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class MetricsPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {

		request.setAttribute(HealthCheckRegistries.class.getName(), _healthCheckRegistries);
		request.setAttribute(MetricRegistries.class.getName(), _metricRegistries);

		super.render(request, response);
	}

	@Reference(unbind = "-")
	protected void setMetricRegistries(final MetricRegistries metricRegistries) {
		_metricRegistries = metricRegistries;
	}

	@Reference(unbind = "-")
	protected void setHealthCheckRegistries(final HealthCheckRegistries healthCheckRegistries) {
		_healthCheckRegistries = healthCheckRegistries;
	}

	private HealthCheckRegistries _healthCheckRegistries;

	private MetricRegistries _metricRegistries;

	private static final Log _log = LogFactoryUtil.getLog(MetricsPortlet.class);
}