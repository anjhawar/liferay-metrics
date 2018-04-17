# liferay-metrics

This is a project to bring DropWizard's Metrics library to Liferay in a way that is:

1. OSGi-friendly
2. Supports Liferay standard groupings (company, site and portlet as well as portal-wide).

To use, just build and deploy the liferay-metrics module to your Liferay instance. Then @Reference the
com.liferay.metrics.MetricRegistries and/or com.liferay.metrics.HealthCheckRegistries instances.

The registries provide the scope for individual DropWizard Metric registries scoped for:

* Portal, global registry.
* Company, keyed by the company id.
* Group (Site), keyed by the group id.
* Portlet, keyed by the plid.
* Custom, keyed by a developer-defined string.

Using the different scopes, you can track otherwise similar metrics.  So if you wanted to track
logins by company, you can use the Company scope to access a company-specific counter for logins to
increment.

## Features

The liferay-metrics module provides the following features:

* The registries to access scoped Metrics and Health Checks.
* Exports the DropWizard Metrics packages and classes so they can be used in your custom modules.
* A simple portlet which displays the current metric values and health check results.
* A refactor of the DropWizard Metrics servlets to work under the Liferay OSGi Whiteboard pattern.

## Servlets

The following servlets are available:

1. /o/metrics/gprof - Generates a gprof-compatible profile file from the current JVM.
2. /o/metrics/health-checks - Runs the health checks and returns a JSON object with the results.
3. /o/metrics/ping - A simple ping servlet that responds with "pong", useful for external response testing.
4. /o/metrics/thread-dump - Generates a thread dump of the current instance.
5. /o/metrics/admin - A simple menu fronting the above servlets.

Note that while these are all useful servlets, there is no security or permissions protecting these servlets.
You will want to take steps to control access to the servlets.