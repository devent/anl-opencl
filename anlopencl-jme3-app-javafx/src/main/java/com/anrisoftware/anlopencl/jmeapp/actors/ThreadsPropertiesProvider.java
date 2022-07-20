/*
 * Copyright ${project.custom.year} Erwin Müller <erwin.mueller@anrisoftware.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anrisoftware.anlopencl.jmeapp.actors;

import java.net.URL;

import javax.inject.Singleton;

import com.anrisoftware.globalpom.threads.properties.external.PropertiesThreads;
import com.anrisoftware.propertiesutils.AbstractContextPropertiesProvider;

/**
 * Returns properties for threads pool.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 2.3
 */
@SuppressWarnings("serial")
@Singleton
public class ThreadsPropertiesProvider extends AbstractContextPropertiesProvider {

    private static final URL RES = ThreadsPropertiesProvider.class.getResource("/exec_threads.properties");

    ThreadsPropertiesProvider() {
        super(PropertiesThreads.class, RES);
    }

}
