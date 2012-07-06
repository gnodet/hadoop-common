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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.osgi.service.url.AbstractURLStreamHandlerService;

public class HdfsUrlHandler extends AbstractURLStreamHandlerService {

    private static final String SYNTAX = "hdfs:[path]";

    public HdfsUrlHandler() {
    }

    /**
     * Open the connection for the given URL.
     *
     * @param url the url from which to open a connection.
     * @return a connection on the specified URL.
     * @throws java.io.IOException if an error occurs or if the URL is malformed.
     */
    @Override
    public URLConnection openConnection(URL url) throws IOException {
        return new Connection(url);
    }

    public class Connection extends URLConnection {

        private InputStream is;

        public Connection(URL url) throws MalformedURLException {
            super(url);
        }

        @Override
        public void connect() throws IOException {
            FileSystem fs = FileSystem.get(URI.create(url.toString()), new Configuration());
            is = fs.open(new Path(url.getPath()));
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (is == null) {
                connect();
            }
            return is;
        }
    }
}
