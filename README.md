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
      server: http://localhost:16443
    name: k3s
contexts:
  - context:
      cluster: k3s
      namespace: default
      user: admin
    name: default
current-context: default
kind: Config
preferences: {}
users:
  - name: admin
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
