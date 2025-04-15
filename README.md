# Java Final Project(unnamed)

## Development Requirements

- jdk 21
- docker
- gardle

## Setup development environment

**run docker compose with dev profile**:
```bash
#!/bin/bash
docker compose --profile dev up
```

see all connection info show up at `/tmp/kubeconfig-devfinal`

- use `/tmp/kubeconfig-devfinal/kubeconfig/k3s.yaml` for k8s connection.
- use etcd's default client port to connect to etcd.


## Run the project

```bash
#!/bin/bash
gradlew bootRun
```