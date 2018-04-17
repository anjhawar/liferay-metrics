package com.liferay.metrics.internal.servlet;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.liferay.metrics.MetricRegistries;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An HTTP servlets which outputs a {@code text/plain} {@code "pong"} response.
 */
@Component(
        immediate = true,
        property = {
                "osgi.http.whiteboard.servlet.name=com.liferay.metrics.internal.servlet.PingServlet",
                "osgi.http.whiteboard.servlet.pattern=/metrics/ping",
                "servlet.init.httpMethods=GET,HEAD"
        },
        service = {Servlet.class, PingServlet.class}
)
public class PingServlet extends HttpServlet {
    private static final long serialVersionUID = 3772654177231086757L;
    private static final String CONTENT_TYPE = "text/plain";
    private static final String CONTENT = "pong";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String NO_CACHE = "must-revalidate,no-cache,no-store";

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    private MetricRegistries _metricRegistries;
    private Counter _pingCounter;

    @Activate
    protected void activate() {
        MetricRegistry registry = _metricRegistries.getPortalMetricRegistry();

        _pingCounter = registry.counter(PingServlet.class.getName() + ".pings");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Validator.isNotNull(_pingCounter)) {
            // increment the ping counter
            _pingCounter.inc();
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader(CACHE_CONTROL, NO_CACHE);
        resp.setContentType(CONTENT_TYPE);

        try (PrintWriter writer = resp.getWriter()) {
            writer.println(CONTENT);
        }
    }
}
