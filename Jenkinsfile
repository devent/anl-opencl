/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Bundle POM
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
 * ANL-OpenCL :: Bundle POM is a derivative work based on Josua Tippetts' C++ library:
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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: Bundle POM bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Builds and deploys the project.
 *
 * @author Erwin Mueller, erwin.mueller@deventm.org
 * @since 0.0.3
 * @version 1.4.0
 */

def groupId
def artifactId
def version
def isSnapshot

pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: "3"))
        disableConcurrentBuilds()
        timeout(time: 60, unit: "MINUTES")
    }

    agent {
        label "maven"
    }

    stages {

        /**
        * The stage will checkout the current branch.
        */
        stage("Checkout Build") {
            steps {
                container("maven") {
                    checkout scm
                }
            }
        } // stage

        /**
        * Setups the pipeline.
        */
        stage("Setup") {
            steps {
                container("maven") {
                    script {
                        groupId = sh script: 'mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout', returnStdout: true
                        artifactId = sh script: 'mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout', returnStdout: true
                        version = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
                        isSnapshot = (version =~ /(?i).*-snapshot$/).matches()
                        echo "${groupId}/${artifactId}:${version} snapshot: ${isSnapshot}"
                    }
                }
            }
        } // stage

        /**
        * The stage will compile, test and deploy on all branches.
        */
        stage("Compile, Test and Deploy") {
            steps {
                container("maven") {
                    script {
                        if (isSnapshot) {
                            sh "/setup-gpg.sh; mvn -s /m2/settings.xml -B clean install site:site deploy site:deploy"
                        } else {
                            sh "/setup-gpg.sh; mvn -s /m2/settings.xml -B clean install site:site site:deploy"
                        }
                    }
                }
            }
        } // stage

        /**
        * The stage will deploy the artifacts and the generated site to the public repository from the main branch.
        */
        stage("Publish to Private") {
            when {
                allOf {
                    expression { !isSnapshot }
                    branch "main"
                }
            }
            steps {
                container("maven") {
                    sh "/setup-gpg.sh; mvn -s /m2/settings.xml -B deploy"
                }
            }
        } // stage

        /**
        * Package the installation.
        */
        stage("Package Installation") {
            steps {
                container("maven") {
                    script {
                        sh "/setup-gpg.sh; cd anlopencl-jme3-izpack; mvn -s /m2/settings.xml -B clean install -Pcompile-izpack"
                        sh "/setup-gpg.sh; cd anlopencl-jme3-izpack-fat; mvn -s /m2/settings.xml -B clean install -Pcompile-izpack"
                        def artifactsOriginal = []
                        artifactsOriginal << "anlopencl-jme3-izpack/target/anlopencl-jme3-izpack-${version}-izpack.jar"
                        artifactsOriginal << "anlopencl-jme3-izpack-fat/target/anlopencl-jme3-izpack-fat-${version}-allinone.jar"
                        artifactsOriginal << "anlopencl-jme3-izpack-fat/target/anlopencl-jme3-izpack-fat-${version}-install.jar"
                        def artifacts = []
                        artifacts << "anlopencl-jme3-izpack/target/anlopencl-jme3-izpack-${version}-${env.BRANCH_NAME}-izpack.jar"
                        artifacts << "anlopencl-jme3-izpack-fat/target/anlopencl-jme3-izpack-fat-${version}-${env.BRANCH_NAME}-allinone.jar"
                        artifacts << "anlopencl-jme3-izpack-fat/target/anlopencl-jme3-izpack-fat-${version}-${env.BRANCH_NAME}-install.jar"
                        artifacts.eachWithIndex { a, index ->
                            sh "mv ${artifactsOriginal[index]} ${a}"
                            archiveArtifacts artifacts: a, followSymlinks: false
                        }
                    }
                }
            }
        } // stage

        /**
        * The stage will deploy the artifacts and the generated site to the public repository from the main branch.
        */
        stage("Publish to Public") {
            when {
                allOf {
                    expression { !isSnapshot }
                    branch "main"
                }
            }
            steps {
                container("maven") {
                    sh "/setup-gpg.sh; mvn -s /m2/settings.xml -Posssonatype -B deploy"
                }
            }
        } // stage

    } // stages

    post {
        success {
            container("maven") {
                script {
                    manager.createSummary("document.png").appendText("<a href=\"${env.JAVADOC_URL}/${groupId}/${artifactId}/${version}/index.html\">View Maven Site</a>", false)
                }
            }
        }
    } // post
        
}
