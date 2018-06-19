FROM csp-alpine35glibc:1.0
MAINTAINER Orestis Akrivopoulos

# ensure local python is preferred over distribution python
ENV PATH /usr/local/bin:$PATH

# Ensure that Python outputs everything that's printed inside
# the application rather than buffering it.
ENV PYTHONUNBUFFERED 1

# http://bugs.python.org/issue19846
# > At the moment, setting "LANG=C" on a Linux system *fundamentally breaks Python 3*, and that's not OK.
ENV LANG C.UTF-8

# install ca-certificates so that HTTPS works consistently
# the other runtime dependencies for Python are installed later
RUN apk add --no-cache ca-certificates

ENV GPG_KEY C01E1CAD5EA2C4F0B8E3571504C367C218ADD4FF
ENV PYTHON_VERSION 2.7.13

RUN set -ex \
	&& apk add --no-cache \
		gnupg \
		openssl \
		tar \
		xz \
		python-dev \
		postgresql-dev
# if this is called "PIP_VERSION", pip explodes with "ValueError: invalid truth value '<VERSION>'"
ENV PYTHON_PIP_VERSION 9.0.1
COPY config/* /tmp/
RUN apk add --no-cache py-psycopg2 postgresql-client gcc postgresql-dev linux-headers
RUN set -ex; \
	\
	apk add --no-cache --virtual .fetch-deps openssl; \
	\
	wget -O get-pip.py "http://central.preprod.melicertes.eu/repo-loads/python27/get-pip.py"; \
	\
	apk del .fetch-deps; \
	\
	python get-pip.py \
		--disable-pip-version-check \
		--no-cache-dir \
		"pip==$PYTHON_PIP_VERSION" \
	; \
	pip --version; \
	\
	find /usr/local -depth \
		\( \
			\( -type d -a -name test -o -name tests \) \
			-o \
			\( -type f -a -name '*.pyc' -o -name '*.pyo' \) \
		\) -exec rm -rf '{}' +; \
	rm -f get-pip.py

RUN cd /tmp \
    && pip install -r requirements.txt \
    && rm -f requirements.txt

RUN apk add --no-cache curl

CMD ["python2"]