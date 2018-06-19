FROM csp-alpine35glibc:1.0

MAINTAINER Thanos Angelatos

RUN apk add --no-cache python3 && python3 -m ensurepip && \
	rm -r /usr/lib/python*/ensurepip && \
	pip3 install --upgrade pip setuptools && \
	rm -r /root/.cache

CMD ["python3", "--version"]	
