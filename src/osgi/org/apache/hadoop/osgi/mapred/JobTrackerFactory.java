/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.osgi.mapred;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobTracker;
import org.apache.hadoop.osgi.Factory;
import org.osgi.framework.BundleContext;

public class JobTrackerFactory extends Factory<JobTracker> {

    private static final Log LOG = LogFactory.getLog(JobTrackerFactory.class);

    public JobTrackerFactory() {
    }

    public JobTrackerFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected JobTracker doCreate() throws Exception {
        final JobConf jobConf = new JobConf();
        final JobTracker jobTracker = JobTracker.startTracker(jobConf);
        new Thread("Job Tracker") {
            @Override
            public void run() {
                try {
                    jobTracker.offerService();
                } catch (Exception e) {
                    LOG.warn("Error starting JobTracker", e);
                }
            }
        }.start();
        return jobTracker;
    }

    @Override
    protected void doDelete(JobTracker service) throws Exception {
        service.stopTracker();
    }

}
