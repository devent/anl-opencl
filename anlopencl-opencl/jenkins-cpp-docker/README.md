[![Build](https://jenkins.anrisoftware.com/job/robobeerun-jenkins-maven-docker/job/main/badge/icon "Build")](https://jenkins.anrisoftware.com/job/robobeerun-jenkins-maven-docker/job/main) ![Apache2](https://project.anrisoftware.com/attachments/download/474/apache2.0-small.gif "Apache2") (© 2022 Erwin Müller)

Jenkins Maven Build Image
=========================

Description
-----------

Jenkins base build image that contains Maven, Java, Git, SSH, GnuPG and
other tools to build software in Jenkins. It contains scripts to setup
an ssh-agent and an gpg-agent to store passphrases. The contains can be
run as a non-root user.

SSH
---

The image contains the SSH client tools and can run optionally a
ssh-agent that caches the ssh passphrase. The following environment
parameters are needed:

| Parameter                   | Description                              | Optional                                                                 | Example                                   |
|-----------------------------|------------------------------------------|--------------------------------------------------------------------------|-------------------------------------------|
| PROJECT\_SSH\_HOST\_FILE    | The path to a known hosts file.          | As an alternative the parameter PROJECT\_SSH\_HOST can be used.          | /ssh-hosts/hosts                          |
| PROJECT\_SSH\_HOST          | The text of the known hosts file.        | As an alternative the parameter PROJECT\_SSH\_HOST\_FILE can be used.    | gitea.anrisoftware.com ssh-rsa AAAAB3Nza… |
| PROJECT\_SSH\_USER          | The ssh user to be used                  | no                                                                       | jenkins@anrisoftware.com                  |
| PROJECT\_SSH\_PASS          | The ssh passphrase for the private file. | yes                                                                      | somexxxx                                  |
| PROJECT\_SSH\_PRIVATE\_FILE | The path to a ssh private file.          | As an alternative the parameter PROJECT\_SSH\_PRIVATE can be used.       | /ssh-key/id\_rsa                          |
| PROJECT\_SSH\_PRIVATE       | The text of the ssh private file.        | As an alternative the parameter PROJECT\_SSH\_PRIVATE\_FILE can be used. | ——<s>BEGIN OPENSSH PRIVATE KEY——</s> …    |
| PROJECT\_GIT\_NAME          | The git commit author name.              | no                                                                       | jenkins                                   |
| PROJECT\_GIT\_EMAIL         | The git commit author email.             | no                                                                       | jenkins@anrisoftware.com                  |

To setup ssh, ssh-agent and the ssh private file for pushing the
following script must be executed beforehand:

    /setup-ssh.sh

GnuGPG
------

The image contains the GnuGPG tools and can optionally a gpg-agent that
caches the GPG passphrase. The following environment parameters are
needed:

| Parameter       | Description                           | Optional                                                    | Example                                  |
|-----------------|---------------------------------------|-------------------------------------------------------------|------------------------------------------|
| GPG\_PASSPHRASE | The GPG passphrase.                   | yes                                                         | somexxx                                  |
| GPG\_KEY\_FILE  | The path to the private GPG key file. | As an alternative the parameter GPG\_KEY can be used.       | /gpg/gpg-private                         |
| GPG\_KEY        | The text of the private GPG key.      | As an alternative the parameter GPG\_KEY\_FILE can be used. | ——<s>BEGIN PGP PRIVATE KEY BLOCK——</s> … |
| GPG\_KEY\_ID    | The GPG private key ID.               | no                                                          | D9554FE847018A76C32922A8F689EA2A99E32C65 |

To setup ssh, ssh-agent and the ssh private file for pushing the
following script must be executed beforehand:

    /setup-gpg.sh

License
-------

Copyright © 2022 Erwin Müller (erwin@muellerpublic.de)

Licensed under the Apache License, Version 2.0 (the “License”); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

### Files used from jenkinsci/docker-inbound-agent

MIT License

Copyright © 2015-2019 CloudBees, Inc., Nicolas De loof, Carlos Sanchez,
Oleg Nenashev, Alex Earl and other contributors

Permission is hereby granted, free of charge, to any person obtaining a
copy  
of this software and associated documentation files (the “Software”), to
deal  
in the Software without restriction, including without limitation the
rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE  
SOFTWARE.

### Files used from jenkinsci/docker-agent

MIT License

Copyright © 2015-2020 Jenkins project contributors

Permission is hereby granted, free of charge, to any person obtaining a
copy  
of this software and associated documentation files (the “Software”), to
deal  
in the Software without restriction, including without limitation the
rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE  
SOFTWARE.
