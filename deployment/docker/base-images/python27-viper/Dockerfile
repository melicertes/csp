FROM ubuntu:18.04
MAINTAINER Christos Panagiotou

# ensure local python is preferred over distribution python
ENV PATH /usr/local/bin:$PATH

# Ensure that Python outputs everything that's printed inside
# the application rather than buffering it.
ENV PYTHONUNBUFFERED 1

# http://bugs.python.org/issue19846
# > At the moment, setting "LANG=C" on a Linux system *fundamentally breaks Python 3*, and that's not OK.
ENV LANG C.UTF-8

ENV GPG_KEY C01E1CAD5EA2C4F0B8E3571504C367C218ADD4FF
ENV PYTHON_VERSION 2.7.13

RUN apt update
RUN apt install -y \
    build-essential \
    python \
    python-dev \
    python-pip \
    python3 \
    python3-dev \
    curl \
    libffi-dev \
    libpng-dev \
    git \
    wget \
    vim

# VIPER STUFF

RUN pip install --upgrade pip

RUN pip --no-cache-dir install matplotlib

USER root

RUN pip install \
    SQLAlchemy \
    PrettyTable \
    python-magic \
    requests \
    pymisp


RUN wget https://github.com/ssdeep-project/ssdeep/releases/download/release-2.14.1/ssdeep-2.14.1.tar.gz && \
    tar -zxvf ssdeep-2.14.1.tar.gz && \
    cd ssdeep-2.14.1 && \
    sh configure && make && \
    make install && \
    pip install pydeep


RUN mkdir /home/viper
WORKDIR /home/viper
RUN git clone https://github.com/viper-framework/viper.git
WORKDIR /home/viper/viper

RUN mkdir -p /opt/csplogs
RUN mkdir -p /home/viper/resources

# Copy viper configuration file
COPY config/viper.conf /home/viper/viper

# Copy new VIPER modules
COPY config/misp.py /home/viper/viper/viper/modules/misp.py
COPY config/cspVT.py /home/viper/viper/viper/modules/cspVT.py
COPY config/cspXOR.py /home/viper/viper/viper/modules/cspXOR.py
COPY config/cspShellcode.py /home/viper/viper/viper/modules/cspShellcode.py

# Copy settings
COPY config/settings.py /home/viper/viper/viper/web/settings.py

# Copy custom CSP auth middleware
COPY config/cspHeaderMiddleware.py /home/viper/viper/viper/web/viperweb/cspHeaderMiddleware.py

# Install pip3
RUN curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py"
RUN python3 get-pip.py
RUN pip install setuptools --upgrade

# Install python3 VIPER dependencies
RUN pip install -r requirements.txt

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]
