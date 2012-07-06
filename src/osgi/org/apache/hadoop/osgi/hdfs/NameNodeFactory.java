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

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.osgi.Factory;
import org.osgi.framework.BundleContext;

public class NameNodeFactory extends Factory<NameNode> {

    public NameNodeFactory() {
    }

    public NameNodeFactory(BundleContext bundleContext) {
        super(bundleContext);
    }

    @Override
    protected NameNode doCreate() throws Exception {
        Configuration conf = new Configuration();
        boolean exists = false;
        for (File file : FSNamesystem.getNamespaceDirs(conf)) {
            exists |= file.exists();
        }
        if (!exists) {
            NameNode.format(conf);
        }
        NameNode nameNode = NameNode.createNameNode(null, conf);
        return nameNode;
    }

    @Override
    protected void doDelete(NameNode service) throws Exception {
        service.stop();
    }

}
