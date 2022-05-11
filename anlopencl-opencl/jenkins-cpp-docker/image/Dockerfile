FROM robobeerun/jenkins-base:latest
LABEL maintainer "Erwin Müller <erwin@muellerpublic.de>"

USER root

ENV DEBUG="false"
ENV DEBIAN_FRONTEND=noninteractive

RUN set -ex; \
    apt-get update; \
    apt-get upgrade -y; \
    apt-get install -y \
    build-essential \
    clang \
    cmake \
    gdb \
    wget \
    libevent-pthreads-2.1-7
    libopencv-core4.2
    libopencv-core-dev
    libopencv-highgui4.2
    libopencv-highgui-dev
    libopencv-imgcodecs4.2
    libopencv-imgcodecs-dev
    opencl-dev
    ; \
    rm -rf /var/lib/apt/lists/*

USER jenkins

ENV PATH=${PATH}

ENTRYPOINT ["/docker-entrypoint.sh"]
