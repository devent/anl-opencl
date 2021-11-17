/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
package com.anrisoftware.anlopencl.jme.opencl

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.jme3.app.SimpleApplication
import com.jme3.asset.AssetManager
import com.jme3.system.AppSettings

class SourceResourcesLoaderApp extends SimpleApplication {

    public static void main(String[] args){
        def i = Guice.createInjector()
        def app = i.getInstance(SourceResourcesLoaderApp)
        app.start(i)
    }

    SourceResourcesProvider sourceResourcesLoader

    Injector injector

    SourceResourcesLoaderApp() {
        def settings = new AppSettings(true);
        settings.setOpenCLSupport(true);
        settings.setVSync(true);
        setShowSettings(false);
        setSettings(settings);
    }

    public void start(Injector injector) {
        this.injector = injector.createChildInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                    }
                    @Provides
                    AssetManager getAssetManager() {
                        SourceResourcesLoaderApp.this.assetManager
                    }
                })
        super.start();
    }

    @Override
    public void simpleInitApp() {
        sourceResourcesLoader = injector.getInstance(SourceResourcesProvider)
        sourceResourcesLoader.load()
    }
}
