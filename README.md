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
      certificate-authority-data: aaa
      server: https://127.0.0.1:6443
    name: default
contexts:
  - context:
      cluster: default
      user: default
    name: default
current-context: default
kind: Config
preferences: {}
users:
  - name: default
    user:
      client-certificate-data: bbb
      client-key-data: ccc
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

## run in production

### configuration by environment variable

- ETCD_ENDPOINT_0, ETCD_ENDPOINT_1: uris for etcd cluster, start with `http://` or `grpc://`
- KUBECONFIG: path of kubernetes config(yaml file)

kubernetes config should look like:
```yaml
apiVersion: v1
clusters:
  - cluster:
      certificate-authority-data: aaa
      server: https://127.0.0.1:6443
    name: default
contexts:
  - context:
      cluster: default
      user: default
    name: default
current-context: default
kind: Config
preferences: {}
users:
  - name: default
    user:
      client-certificate-data: bbb
      client-key-data: ccc
```

If backend is deploy inside kubernetes, use following role privilege setting(and grant the role to the pod):
```yml
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: my-cloud-provisioning

rules:
  - apiGroups:
      - "*"
    resources:
      - "*"
    verbs:
      - "*"
```
