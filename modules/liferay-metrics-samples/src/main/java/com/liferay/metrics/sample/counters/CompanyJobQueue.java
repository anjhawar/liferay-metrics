package com.liferay.metrics.sample.counters;

import com.codahale.metrics.Counter;
import com.liferay.metrics.MetricRegistries;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class CompanyJobQueue: Example of what one might build for Liferay, inspired by http://metrics.dropwizard.io/4.0.0/getting-started.html#counters
 *
 * @author dnebinger
 */
@Component(
        immediate = true
)
public class CompanyJobQueue {

    public class Job {

    }

    public interface Queue {
        void offer(Job job);
        Job take();
    }

    private Queue queue;

    public void addJob(long companyId, Job job) {
        // fetch the counter
        Counter pendingJobs = _metricRegistries.getCompanyMetricRegistry(companyId).counter("pending-jobs");

        // increment
        pendingJobs.inc();

        // do the other stuff
        queue.offer(job);
    }

    public Job takeJob(long companyId) {
        // fetch the counter
        Counter pendingJobs = _metricRegistries.getCompanyMetricRegistry(companyId).counter("pending-jobs");

        // decrement
        pendingJobs.dec();

        // do the other stuff
        return queue.take();
    }

    @Reference(unbind = "-")
    protected void setMetricRegistries(final MetricRegistries metricRegistries) {
        _metricRegistries = metricRegistries;
    }

    private MetricRegistries _metricRegistries;
}
