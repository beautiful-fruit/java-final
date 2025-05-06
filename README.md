## requirements

1. jdk 21+ (17 should work?)
2. docker
3. gardle

## run the project

### setup etcd and k8s

```bash
docker compose --profile dev up
```

### connect to k8s

k8s config is generated at `/tmp/kubeconfig-devfinal/kubeconfig`

kubeconfig:

```yaml
apiVersion: v1
clusters:
  - cluster:
      certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUREakNDQWZhZ0F3SUJBZ0lUVmZETjhUQXA2ZE9oL3FUUmFMWXFJNUp3empBTkJna3Foa2lHOXcwQkFRc0YKQURBWE1SVXdFd1lEVlFRRERBd3hNQzR4TlRJdU1UZ3pMakV3SGhjTk1qUXhNVEExTURJeE1qRXdXaGNOTXpReApNVEF6TURJeE1qRXdXakFYTVJVd0V3WURWUVFEREF3eE1DNHhOVEl1TVRnekxqRXdnZ0VpTUEwR0NTcUdTSWIzCkRRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRQ3k0Qlg5TWFKcExZNHk5VDI3dkl6YUlxZXZpQ21zdThtRTVVc2EKcFlJNGRuS1FMdzhSWjBUTlZ5WmJpQ2VQb0luSXVPc1FwQ0xIeFhrNFY1a1R5NXl3UldoODFZb1RhdkxSWllFTwpVdW1jN09tMEhmWDB6QjQ5aU5zbkFwZzkwdUZzb1ZBbllZWVhYZFJoLzI0UTFISy9zSzF0WGJaemVrNkxhNDA2CkVmK0ExY2UvR3hnNGl2a1ZlYS9QNEtlU0c2MEY4bFNWcUIzYUluT3kwb1RqbFV3S3FEVzM4TmlmS1A4OGZaVWsKNVpTUkZoTkZmWXlINEJxcERkOU5peEFaZnFFZjBLVm0zZElyUThtZVZNU0M1bTd6WFVvckM0dnJPZnVUdkh4RQozbXVoOWlXTUtHM0FQVGladDNWdlg4RWhrREJ5Y0hJejArSytWaG5jM0cweVBKTkhBZ01CQUFHalV6QlJNQjBHCkExVWREZ1FXQkJTYmh0elVvZ0FxeStNdnVVTk02VEpHb0RPdkFUQWZCZ05WSFNNRUdEQVdnQlNiaHR6VW9nQXEKeStNdnVVTk02VEpHb0RPdkFUQVBCZ05WSFJNQkFmOEVCVEFEQVFIL01BMEdDU3FHU0liM0RRRUJDd1VBQTRJQgpBUUJrU0JpWHdFNVo0a2x6UGdyL0VvYUtsS1E4OFZhZ1Z5TDVzRGd3MFVPamFYNllYa2pzcnhmckZNdzFPeG1UCnEvQVJqa2Zob3VqcGRRcDMxRHhBa0tiWWZvQVNYV1ZDenkvQjdaa2hIVThtY3pzeDRaRWEyaW1BTU9MTGZkS00KRW5zWlk5d2RCNi9rL2xwNHZTV2ZBazh4ZUUwUytHSGRhWEU0cklFZWtnbmcxbkY0QVlTcDFFM2EyS0E2aHFvTApxWlRmdjRyMFk3VEVub2NUVmN2ZEcrbzg4SlR6N0F5dzVmN2ZvSjFSaWRCK0ZqSjl6WEMrQkM0OHlicXpwQ0JpCndZUjBQRDVraE5pRHc5Y2QrUWlKMkNqT1A1VkNEY3lhYTIwb1VBbXFoeWQ3Y1l1RHg0UVFYWEFlMktlN3ZDYmwKbE9jRWRmUS9iYUVYT0d6VmRsOE14S3dtCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
      server: https://10.2.0.66:16443
    name: microk8s
contexts:
  - context:
      cluster: microk8s
      namespace: default
      user: admin
    name: default
current-context: default
kind: Config
preferences: {}
users:
  - name: minikube
    user:
      client-certificate: /tmp/kubeconfig-devfinal/kubeconfig/client.crt
      client-key: /tmp/kubeconfig-devfinal/kubeconfig/client.key
```

### run the unit test

```bash
source test-env.sh
gradlew test
```

### connect to etcd

```bash
export ETCDCTL_API=3
export ETCDCTL_ENDPOINTS=http://127.0.0.1:2379
```

delete all keys in etcd:

```bash
etcdctl del "" --prefix
```

## run the project

## Run the project

```bash
gradlew bootRun
```
