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

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TaskTracker;
import org.apache.hadoop.util.Daemon;
import org.apache.hadoop.osgi.Factory;
import org.osgi.framework.BundleContext;

public class TaskTrackerFactory extends Factory<TaskTracker> {

    public TaskTrackerFactory() {
    }

    public TaskTrackerFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected TaskTracker doCreate() throws Exception {
        final JobConf jobConf = new JobConf();
        TaskTracker taskTracker = new TaskTracker(jobConf);
        new Daemon(taskTracker).start();
        return taskTracker;
    }

    @Override
    protected void doDelete(TaskTracker service) throws Exception {
        service.shutdown();
    }

}
