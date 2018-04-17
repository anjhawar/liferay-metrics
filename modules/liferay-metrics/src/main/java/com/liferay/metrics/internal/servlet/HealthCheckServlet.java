package com.liferay.metrics.internal.servlet;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckFilter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.liferay.metrics.HealthCheckRegistries;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.SortedMap;

/**
 * class HealthCheckServlet: A health check servlet adapted from the DropWizard version to work under the OSGi
 * HTTP whiteboard.
 *
 * @author dnebinger
 */
@Component(
    immediate = true,
    property = {
        "osgi.http.whiteboard.servlet.name=com.liferay.metrics.internal.servlet.HealthCheckServlet",
        "osgi.http.whiteboard.servlet.pattern=/metrics/health-checks",
        "servlet.init.httpMethods=GET,HEAD"
    },
        service = {Servlet.class, HealthCheckServlet.class}
)
public class HealthCheckServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.mapper = new ObjectMapper().registerModule(new HealthCheckModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // try to fetch the registry from the parameters
        String registryType = ParamUtil.getString(req, "type");
        String registryKey = ParamUtil.getString(req, "key");

        HealthCheckRegistry registry = null;

        if (Validator.isNotNull(registryType)) {
            if ("portal".equalsIgnoreCase(registryType)) {
                registry = _healthCheckRegistries.getPortalHealthCheckRegistry();
            } else if ("group".equalsIgnoreCase(registryType)) {
                long groupId = GetterUtil.getLong(registryKey);

                if (groupId > 0) {
                    registry = _healthCheckRegistries.getGroupHealthCheckRegistry(groupId);
                }
            } else if ("company".equalsIgnoreCase(registryType)) {
                long companyId = GetterUtil.getLong(registryKey);

                if (companyId > 0) {
                    registry = _healthCheckRegistries.getCompanyHealthCheckRegistry(companyId);
                }
            } else if ("custom".equalsIgnoreCase(registryType)) {
                if (Validator.isNotNull(registryKey)) {
                    registry = _healthCheckRegistries.getCustomHealthCheckRegistry(registryKey);
                }
            }
        }

        // if we don't have a registry, use the portal registry.
        if (registry == null) {
            registry = _healthCheckRegistries.getPortalHealthCheckRegistry();
        }

        final SortedMap<String, HealthCheck.Result> results = runHealthChecks(registry);

        resp.setContentType(CONTENT_TYPE);
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        if (results.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        } else {
            if (isAllHealthy(results)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        final boolean prettyPrint = ParamUtil.getBoolean(req,"pretty");
        ObjectWriter writer = getWriter(prettyPrint);

        try (OutputStream output = resp.getOutputStream()) {
            writer.writeValue(output, results);
        }
    }

    private ObjectWriter getWriter(final boolean prettyPrint) {

        if (prettyPrint) {
            return mapper.writerWithDefaultPrettyPrinter();
        }

        return mapper.writer();
    }

    private SortedMap<String, HealthCheck.Result> runHealthChecks(final HealthCheckRegistry registry) {
        return registry.runHealthChecks(HealthCheckFilter.ALL);
    }

    private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
        for (HealthCheck.Result result : results.values()) {
            if (!result.isHealthy()) {
                return false;
            }
        }

        return true;
    }

    @Reference(unbind = "-")
    protected void setHealthCheckRegistries(final HealthCheckRegistries healthCheckRegistries) {
        _healthCheckRegistries = healthCheckRegistries;
    }

    private HealthCheckRegistries _healthCheckRegistries;

    private static final String CONTENT_TYPE = "application/json";

    private transient ObjectMapper mapper;

    private static final Log _log = LogFactoryUtil.getLog(HealthCheckServlet.class);
}
