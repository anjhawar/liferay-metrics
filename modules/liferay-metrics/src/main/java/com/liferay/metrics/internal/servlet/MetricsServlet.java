package com.liferay.metrics.internal.servlet;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A servlet which returns the metrics in a given registry as an {@code application/json} response.
 */
@Component(
        immediate = true,
        property = {
                "osgi.http.whiteboard.servlet.name=com.liferay.metrics.internal.servlet.MetricsServlet",
                "osgi.http.whiteboard.servlet.pattern=/metrics/metrics",
                "servlet.init.httpMethods=GET,HEAD",
                "showSamples:Boolean=true",
                "durationUnit=SECONDS",
                "rateUnit=SECONDS",
                "allowedOrigin=",
                "jsonpCallback="
        },
        service = {Servlet.class, MetricsServlet.class}
)
public class MetricsServlet extends HttpServlet {
    public static final String RATE_UNIT = "rateUnit";
    public static final String DURATION_UNIT = "durationUnit";
    public static final String SHOW_SAMPLES = "showSamples";
    public static final String ALLOWED_ORIGIN = "allowedOrigin";
    public static final String CALLBACK_PARAM = "jsonpCallback";

    private static final long serialVersionUID = 1049773947734939602L;
    private static final String CONTENT_TYPE = "application/json";

    private String allowedOrigin;
    private String jsonpParamName;
    private transient ObjectMapper mapper;

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    private MetricRegistries _metricRegistries;

    @Activate
    @Modified
    protected void activate(final Map<String, Object> properties) {

        final TimeUnit rateUnit = parseTimeUnit((String) properties.get(RATE_UNIT), TimeUnit.SECONDS);
        final TimeUnit durationUnit = parseTimeUnit((String) properties.get(DURATION_UNIT), TimeUnit.SECONDS);
        final boolean showSamples = (Boolean) properties.get(SHOW_SAMPLES);

        this.mapper = new ObjectMapper().registerModule(new MetricsModule(rateUnit, durationUnit, showSamples, MetricFilter.ALL));

        this.allowedOrigin = (String) properties.get(ALLOWED_ORIGIN);
        this.jsonpParamName = (String) properties.get(CALLBACK_PARAM);
    }

    public static final String PARAM_SCOPE = "scope";
    public static final String PARAM_KEY = "key";

    public static final String SCOPE_PORTAL = "portal";
    public static final String SCOPE_GROUP = "group";
    public static final String SCOPE_COMPANY = "company";
    public static final String SCOPE_CUSTOM = "custom";
    public static final String SCOPE_PORTLET = "portlet";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String registryScope = ParamUtil.getString(req, PARAM_SCOPE);
        String registryKey = ParamUtil.getString(req, PARAM_KEY);

        MetricRegistry registry = null;

        if (Validator.isNotNull(registryScope)) {
            if (SCOPE_PORTAL.equalsIgnoreCase(registryScope)) {
                registry = _metricRegistries.getPortalMetricRegistry();
            } else if (SCOPE_GROUP.equalsIgnoreCase(registryScope)) {
                long groupId = GetterUtil.getLong(registryKey);

                if (groupId > 0) {
                    registry = _metricRegistries.getGroupMetricRegistry(groupId);
                }
            } else if (SCOPE_COMPANY.equalsIgnoreCase(registryScope)) {
                long companyId = GetterUtil.getLong(registryKey);

                if (companyId > 0) {
                    registry = _metricRegistries.getCompanyMetricRegistry(companyId);
                }
            } else if (SCOPE_CUSTOM.equalsIgnoreCase(registryScope)) {
                if (Validator.isNotNull(registryKey)) {
                    registry = _metricRegistries.getCustomMetricRegistry(registryKey);
                }
            } else if (SCOPE_PORTLET.equalsIgnoreCase(registryScope)) {
                if (Validator.isNotNull(registryKey)) {
                    registry = _metricRegistries.getPortletMetricRegistry(registryKey);
                }
            }
        }

        // if we don't have a registry, use the portal registry.
        if (registry == null) {
            registry = _metricRegistries.getPortalMetricRegistry();
        }

        resp.setContentType(CONTENT_TYPE);

        if (Validator.isNotNull(allowedOrigin)) {
            resp.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        }

        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setStatus(HttpServletResponse.SC_OK);

        final boolean prettyPrint = ParamUtil.getBoolean(req,"pretty");
        ObjectWriter writer = getWriter(prettyPrint);

        try (OutputStream output = resp.getOutputStream()) {
            if (Validator.isNotNull(jsonpParamName) && Validator.isNotNull(ParamUtil.getString(req,jsonpParamName))) {
                writer.writeValue(output, new JSONPObject(ParamUtil.getString(req,jsonpParamName), registry));
            } else {
                writer.writeValue(output, registry);
            }
        }
    }

    private ObjectWriter getWriter(final boolean prettyPrint) {

        if (prettyPrint) {
            return mapper.writerWithDefaultPrettyPrinter();
        }

        return mapper.writer();
    }

    private TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
        try {
            return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
