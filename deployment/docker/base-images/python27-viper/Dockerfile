FROM ubuntu:16.04
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

#RUN set -ex \
#	&& apk add --no-cache \
#	    gcc \
#	    g++ \
#	    make \
#		gnupg \
#		openssl \
#		tar \
#		xz \
#		python-dev \
#		py-pip \
#		postgresql-dev

RUN apt update
RUN apt install -y \
    build-essential \
    python \
    python-dev \
    python-pip \
#    linux-headers-$(uname -r) \
    curl \
    libffi-dev \
#    freetype-dev \
#    freetype \
    libpng-dev \
    git \
    wget

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
RUN mkdir logs

COPY config/viper.conf /home/viper/viper
COPY config/misp.py /home/viper/viper/viper/modules/misp.py
COPY config/cspHeaderMiddleware.py /home/viper/viper/viper/web/viperweb/cspHeaderMiddleware.py
#RUN pip install numpy
#RUN apk add --no-cache numpy
#RUN echo git+https://github.com/viper-framework/pefile.git#egg=pefile > constraints.txt
#RUN cp constraints.txt exclude.txt
#RUN cat constraints.txt
#RUN sed -i '/git+https:\/\/github.com\/viper-framework\/pefile.git#egg=pefile/d' requirements-modules.txt
RUN pip install setuptools --upgrade
RUN pip install -r requirements.txt

#CMD ["python", "../viper/viper-cli"]
#CMD ["python", "../viper/viper-web.py","-H", "localhost", "-p", "9090"]


#CMD ["python2"]
