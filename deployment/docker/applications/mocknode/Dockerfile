FROM node:alpine

RUN mkdir -p /opt/csp/mocknode
RUN mkdir -p /opt/csp/sslcert
WORKDIR /opt/csp/mocknode

# Install app dependencies
COPY app/package.json /opt/csp/mocknode
RUN npm install

COPY app/* /opt/csp/mocknode/

ADD app .

EXPOSE 3000
CMD ["node", "server.js"]