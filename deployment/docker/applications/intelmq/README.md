# intelmq-docker
Example configuration for docker-compose
```
intelmq:
  image: majidsalehi/intelmq
  hostname: intelmq
  volumes:
    - ./logs:/opt/intelmq/var/log
    - ./file-output:/opt/intelmq/var/lib/file-output
    - ./config/defaults.conf:/opt/intelmq/etc/defaults.conf
    - ./config/pipeline.conf:/opt/intelmq/etc/pipeline.conf
    - ./config/runtime.conf:/opt/intelmq/etc/runtime.conf
  links:
    - redis
redis:source_pipeline_host - broker IP, FQDN or Unix socket that the bot will use to connect and receive messages.
      destination_pipeline_host - broker IP, FQDN or Unix socket that the bot will use to connect and send messages.
      broker - select which broker intelmq can use. Use the following values:
      redis - Redis allows some persistence but is not so fast as ZeroMQ (in development). But note that persistence has to be manually activated. 
      See http://redis.io/topics/persistence

  image: redis
  hostname: redis
  ```

<a href="mailto:majid.salehi.ghamsari@fokus.fraunhofer.de">Majid Salehi</a>  
