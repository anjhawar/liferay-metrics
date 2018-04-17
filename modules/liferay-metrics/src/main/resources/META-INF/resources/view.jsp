<%@ include file="/init.jsp" %>

<%

	HealthCheckRegistries healthCheckRegistries = (HealthCheckRegistries) request.getAttribute(HealthCheckRegistries.class.getName());
	MetricRegistries metricRegistries = (MetricRegistries) request.getAttribute(MetricRegistries.class.getName());

	List<String> healthCheckRegistryNames = healthCheckRegistries.getRegistryNames();
	List<String> metricRegistryNames = metricRegistries.getRegistryNames();

	boolean hasHealthChecks = ListUtil.isNotEmpty(healthCheckRegistryNames);
	boolean hasMetrics = ListUtil.isNotEmpty(metricRegistryNames);

	if (hasHealthChecks) {
		Collections.sort(healthCheckRegistryNames);
	}
	if (hasMetrics) {
	    Collections.sort(metricRegistryNames);
	}
%>

<div class="health">

	<div class="health-checks">
		<c:if test="<%= ! hasHealthChecks %>">
			<p><liferay-ui:message key="no-health-check-registries" /></p>
		</c:if>
		<c:if test="<%= hasHealthChecks %>">
			<h1><liferay-ui:message key="healthcheck-registries" /></h1>
			<p><liferay-ui:message key="healthcheck-registries-info" /></p>
			<%
				HealthCheckRegistry registry;
				SortedMap<String, HealthCheck.Result> results;
				HealthCheck.Result result;

				for (String registryName : healthCheckRegistryNames ) {
			%>
			<h2><liferay-ui:message key="healthcheck-registry-name" arguments="<%= registryName %>" /></h2>
			<%
				    registry = healthCheckRegistries.getCustomHealthCheckRegistry(registryName);

				    results = registry.runHealthChecks();

				    if (MapUtil.isNotEmpty(results)) {
			%>
		<h3><liferay-ui:message key="counters" /></h3>
		<table class="health-checks-table">
			<tbody>
			<tr><th><liferay-ui:message key="column.health.check" /></th><th><liferay-ui:message key="column.healthy" /></th><th><liferay-ui:message key="column.message" /></th></tr>
					<%
						for (String name : results.keySet()) {
							result = results.get(name);
							%>
			<tr><td><%= name %></td><td><%= result.isHealthy() %></td><td><%= result.getMessage() %></td></tr>
			<%
						}
					%>
			</tbody>
		</table>
			<%
					}
				}
			%>
		</c:if>
	</div>

	<div class="metrics">
		<c:if test="<%= ! hasMetrics %>">
			<p><liferay-ui:message key="no-metric-registries" /></p>
		</c:if>
		<c:if test="<%= hasMetrics %>">
			<h1><liferay-ui:message key="metric-registries" /></h1>
			<p><liferay-ui:message key="metric-registries-info" /></p>
			<%
				MetricRegistry metricRegistry;
				SortedMap<String,Counter> counters;
				SortedMap<String,Meter> meters;
				SortedMap<String,Gauge> gauges;
				SortedMap<String,Histogram> histograms;
				SortedMap<String,Timer> timers;

				for (String registryName : metricRegistryNames) {
				    %>
			<h2><liferay-ui:message key="metric-registry-name" arguments="<%= registryName %>" /></h2>
					<%
				    metricRegistry = metricRegistries.getCustomMetricRegistry(registryName);

				    counters = metricRegistry.getCounters();
				    meters = metricRegistry.getMeters();
				    gauges = metricRegistry.getGauges();
				    histograms = metricRegistry.getHistograms();
				    timers = metricRegistry.getTimers();

						if (MapUtil.isNotEmpty(counters)) {
							Counter counter;

					%>
			<h3><liferay-ui:message key="counters" /></h3>
			<table class="health-metrics-table">
				<tbody>
				<tr><th><liferay-ui:message key="column.counter" /></th><th><liferay-ui:message key="column.value" /></th></tr>
				<%

					for (String name : counters.keySet()) {
						counter = counters.get(name);

				%>
				<tr><td><%= name %></td><td><%= counter.getCount() %></td></tr>
				<%
					}
				%>
				</tbody>
			</table>
			<%
					}
				if (MapUtil.isNotEmpty(gauges)) {
					Gauge gauge;

			%>
			<h3><liferay-ui:message key="gauges" /></h3>
			<table class="health-metrics-table">
				<tbody>
				<tr><th><liferay-ui:message key="column.gauge" /></th><th><liferay-ui:message key="column.value" /></th></tr>
				<%

					for (String name : gauges.keySet()) {
						gauge = gauges.get(name);

				%>
				<tr><td><%= name %></td><td><%= gauge.getValue() %></td></tr>
				<%
					}
				%>
				</tbody>
			</table>
			<%
					}
				if (MapUtil.isNotEmpty(histograms)) {
					Histogram histogram;

			%>
			<h3><liferay-ui:message key="histograms" /></h3>
			<table class="health-metrics-table">
				<tbody>
				<tr><th><liferay-ui:message key="column.histogram" /></th><th><liferay-ui:message key="column.value" /></th></tr>
				<%

					for (String name : histograms.keySet()) {
						histogram = histograms.get(name);

				%>
				<tr><td><%= name %></td><td><%= histogram.getCount() %></td></tr>
				<%
					}
				%>
				</tbody>
			</table>
			<%
					}
				if (MapUtil.isNotEmpty(meters)) {
					Meter meter;

			%>
			<h3><liferay-ui:message key="meters" /></h3>
			<table class="health-metrics-table">
				<tbody>
				<tr><th><liferay-ui:message key="column.meter" /></th><th><liferay-ui:message key="column.value" /></th></tr>
				<%

					for (String name : meters.keySet()) {
						meter = meters.get(name);

				%>
				<tr><td><%= name %></td><td><%= meter.getCount() %></td></tr>
				<%
					}
				%>
				</tbody>
			</table>
			<%
					}
				if (MapUtil.isNotEmpty(timers)) {
					Timer timer;

			%>
			<h3><liferay-ui:message key="timers" /></h3>
			<table class="health-metrics-table">
				<tbody>
				<tr><th><liferay-ui:message key="column.timer" /></th><th><liferay-ui:message key="column.value" /></th></tr>
				<%

					for (String name : timers.keySet()) {
						timer = timers.get(name);

				%>
				<tr><td><%= name %></td><td><%= timer.getCount() %></td></tr>
				<%
					}
				%>
				</tbody>
			</table>
			<%
					}
				}
			%>
		</c:if>
	</div>
</div>

