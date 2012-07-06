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
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.osgi.Factory;
import org.osgi.framework.BundleContext;

public class DataNodeFactory extends Factory<DataNode> {

    public DataNodeFactory() {
    }

    public DataNodeFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected DataNode doCreate() throws Exception {
        Configuration conf = new Configuration();
        DataNode dataNode = DataNode.createDataNode(null, conf);
        return dataNode;
    }

    @Override
    protected void doDelete(DataNode service) throws Exception {
        service.shutdown();
    }
}
