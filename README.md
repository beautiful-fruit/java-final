# Development Requirements

jdk 21 and docker

**run docker compose with dev profile**:
```
docker compose --profile dev up
```

see all connection info show up at `/tmp/kubeconfig-devfinal`

- use `/tmp/kubeconfig-devfinal/kubeconfig/k3s.yaml` for k8s connection.
- use etcd's default client port to connect to etcd.
