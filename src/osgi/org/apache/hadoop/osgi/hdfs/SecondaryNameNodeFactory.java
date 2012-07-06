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
package org.apache.hadoop.osgi.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode;
import org.apache.hadoop.util.Daemon;
import org.apache.hadoop.osgi.Factory;
import org.osgi.framework.BundleContext;

public class SecondaryNameNodeFactory extends Factory<SecondaryNameNode> {

    public SecondaryNameNodeFactory() {
    }

    public SecondaryNameNodeFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected SecondaryNameNode doCreate() throws Exception {
        Configuration conf = new Configuration();
        SecondaryNameNode secondaryNameNode = new SecondaryNameNode(conf);
        new Daemon(secondaryNameNode).start();
        return secondaryNameNode;
    }

    @Override
    protected void doDelete(SecondaryNameNode service) throws Exception {
        service.shutdown();
    }

}
