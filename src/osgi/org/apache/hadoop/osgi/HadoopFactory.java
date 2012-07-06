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
package org.apache.hadoop.osgi;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.CleanupQueue;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.osgi.hdfs.DataNodeFactory;
import org.apache.hadoop.osgi.hdfs.HdfsUrlHandler;
import org.apache.hadoop.osgi.hdfs.NameNodeFactory;
import org.apache.hadoop.osgi.hdfs.SecondaryNameNodeFactory;
import org.apache.hadoop.osgi.mapred.JobTrackerFactory;
import org.apache.hadoop.osgi.mapred.TaskTrackerFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.url.URLStreamHandlerService;

public class HadoopFactory implements ManagedService {

    public static final String CONFIG_PID = "org.apache.hadoop";

    private BundleContext bundleContext;

    private DataNodeFactory dataNodeFactory;
    private NameNodeFactory nameNodeFactory;
    private SecondaryNameNodeFactory secondaryNameNodeFactory;
    private JobTrackerFactory jobTrackerFactory;
    private TaskTrackerFactory taskTrackerFactory;

    private ServiceRegistration urlHandlerRegistration;

    public HadoopFactory(BundleContext context) {
        bundleContext = context;
        dataNodeFactory = new DataNodeFactory(bundleContext);
        nameNodeFactory = new NameNodeFactory(bundleContext);
        secondaryNameNodeFactory = new SecondaryNameNodeFactory(bundleContext);
        jobTrackerFactory = new JobTrackerFactory(bundleContext);
        taskTrackerFactory = new TaskTrackerFactory(bundleContext);
    }

    public void updated(Dictionary dictionary) throws ConfigurationException {
        Properties properties = new Properties();
        if (dictionary != null) {
            // Set system properties and build configuration
            for (Enumeration e = dictionary.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                String keyStr = key.toString();
                String valStr = dictionary.get(key).toString();
                if (keyStr.startsWith("hadoop.")) {
                    System.setProperty(keyStr, valStr);
                }
                properties.setProperty(keyStr, valStr);
            }
            Configuration.setConfigurations(Configuration.class.getClassLoader(), properties);
            // Set singletons
            UserGroupInformation.setConfiguration(new Configuration());
            // Update url handler
            properties.put("url.handler.protocol", "hdfs");
            if (urlHandlerRegistration == null) {
                urlHandlerRegistration = bundleContext.registerService(URLStreamHandlerService.class.getName(), new HdfsUrlHandler(), properties);
            } else {
                urlHandlerRegistration.setProperties(properties);
            }
            properties.remove("url.handler.protocol");
        } else {
            if (urlHandlerRegistration != null) {
                urlHandlerRegistration.unregister();
            }
        }

        updateFactory(properties, "nameNode", nameNodeFactory);
        updateFactory(properties, "dataNode", dataNodeFactory);
        updateFactory(properties, "secondaryNameNode", secondaryNameNodeFactory);
        updateFactory(properties, "jobTracker", jobTrackerFactory);
        updateFactory(properties, "taskTracker", taskTrackerFactory);
    }

    private void updateFactory(final Dictionary properties, final String prop, final Factory<?> factory) throws ConfigurationException {
        factory.delete();
        if (getBool(properties, prop)) {
            factory.create(properties);
        }
    }

    public void destroy() throws Exception {
        FileSystem.closeAll();
        updated(null);
        CleanupQueue.getInstance().stop();
        DefaultMetricsSystem.INSTANCE.shutdown();
    }

    private boolean getBool(Dictionary properties, String key) {
        Object val = properties != null ? properties.get(key) : null;
        if (val != null) {
            return Boolean.parseBoolean(val.toString());
        } else {
            return false;
        }
    }

}
