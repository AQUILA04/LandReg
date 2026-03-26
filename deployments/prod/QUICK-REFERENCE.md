# LandReg - Quick Reference Card

## 🚀 Déploiement Rapide

```bash
# 1. Installer les outils
cd on-prem/scripts && sudo ./bootstrap-server.sh

# 2. Initialiser K8s
kubeadm init --pod-network-cidr=10.244.0.0/16
kubectl label node <NODE> node-type=<database|middleware|api|afis|monitoring>

# 3. Déployer
cd on-prem && ./scripts/deploy.sh
```

---

## 📊 Statut Quotidien

```bash
# Vue d'ensemble
kubectl get all -A

# Pods
kubectl get pods -A
kubectl top pods -A

# Nodes
kubectl get nodes
kubectl top nodes

# Events récents
kubectl get events -A --sort-by='.lastTimestamp' | tail -20
```

---

## 🔍 Debugging

```bash
# Logs
kubectl logs <POD> -n <NS>
kubectl logs -f <POD> -n <NS>

# Description
kubectl describe pod <POD> -n <NS>

# Shell
kubectl exec -it <POD> -n <NS> -- /bin/bash

# Test réseau
kubectl run test --image=busybox -it --rm -- wget -qO- http://<SVC>:<PORT>
```

---

## 🔄 Rollback

```bash
# Restart deployment
kubectl rollout restart deployment/<NAME> -n <NS>

# Rollback
kubectl rollout undo deployment/<NAME> -n <NS>

# Historique
kubectl rollout history deployment/<NAME> -n <NS>
```

---

## 📈 Scaling

```bash
# Scale
kubectl scale deployment <NAME> --replicas=3 -n <NS>

# HPA (auto-scaling)
kubectl autoscale deployment <NAME> --min=2 --max=10 --cpu-percent=70 -n <NS>
```

---

## 💾 Backup/Restore

```bash
# PostgreSQL
kubectl exec postgres-0 -n database -- pg_dump -U postgres landreg > backup.sql
cat backup.sql | kubectl exec -i postgres-0 -n database -- psql -U postgres landreg

# MongoDB
kubectl exec mongodb-0 -n database -- mongodump --archive=/tmp/bak.archive
kubectl cp database/mongodb-0:/tmp/bak.archive ./bak.archive
```

---

## 🔐 Passwords

```bash
# PostgreSQL
kubectl get secret postgres-postgresql -n database -o jsonpath='{.data.postgresql-password}' | base64 -d

# MongoDB
kubectl get secret mongodb -n database -o jsonpath='{.data.mongodb-password}' | base64 -d

# Grafana
kubectl get secret grafana -n monitoring -o jsonpath='{.data.admin-password}' | base64 -d
```

---

## 🌐 Accès Services

```bash
kubectl port-forward svc/<SVC> <LOCAL_PORT>:<SVC_PORT> -n <NS>

# Exemples:
kubectl port-forward -n backend svc/landreg-api-service 8080:8080
kubectl port-forward -n monitoring svc/prometheus-server 9090:80
kubectl port-forward -n monitoring svc/grafana 3000:80
kubectl port-forward -n monitoring svc/kibana 5601:5601
```

---

## 🧹 Cleanup

```bash
# Supprimer un namespace
kubectl delete namespace <NS>

# Supprimer helm release
helm uninstall <RELEASE> -n <NS>

# Drain un node
kubectl drain <NODE> --ignore-daemonsets --delete-emptydir-data
kubectl cordon <NODE>
kubectl uncordon <NODE>
```

---

## 📝 Namespaces

| Namespace | Composants |
|-----------|------------|
| database | PostgreSQL, MongoDB |
| middleware | Kafka, Redis |
| backend | LandReg API |
| afis | AFIS Master, AFIS Workers |
| monitoring | Prometheus, Grafana, ELK |

---

## 🔗 URLs Locales

| Service | URL |
|---------|-----|
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| Kibana | http://localhost:5601 |
| API | http://localhost:8080 |
| AFIS Master | http://localhost:8081 |

---

## ⚡ Commandes Utiles

```bash
# Watch pods en temps réel
watch kubectl get pods -A

# Logs tous containers d'un pod
kubectl logs <POD> --all-containers=true -f

# Ressources d'un pod
kubectl get pod <POD> -n <NS> -o jsonpath='{.spec.containers[*].resources}'

# Top 5 pods CPU
kubectl top pods -A | sort -k2 -rn | head -6

# PVCs non bound
kubectl get pvc -A | grep -v Bound

# Pods error/pending
kubectl get pods -A | grep -E 'Error|Pending|ImagePull'

# Restart si CrashLoop
kubectl delete pod <POD> -n <NS>
```
