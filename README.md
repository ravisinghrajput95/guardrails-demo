# Vulnerable Java Demo Application

This project is a **purposefully vulnerable Java web application** designed for learning and practicing:
- Secure coding
- DevSecOps workflows
- CI/CD security scanning (Snyk / Trivy / GitHub Actions)
- Kubernetes guardrails (Kyverno / KubeArmor)
- Runtime threat detection and analysis

⚠ **Important:**  
This application is **intentionally insecure.**  
Do **not** expose it to the public internet or use in production environments.

---

## Features (and Vulnerabilities)

| Feature | Description | Intentional Vulnerability |
|--------|-------------|--------------------------|
| `/user?name=<value>` | Lookup user info in SQLite | **SQL Injection** via raw string query |
| `/exec?cmd=<value>` | Run OS commands | **Command Injection** risk |
| `/upload` | Upload files | No validation → **Malicious file upload** risk |
| `/read?file=<value>` | Read files | **Path traversal** potential |
| Database | SQLite local file | Weak auth, no encryption |

This app is used to demonstrate **shifting from "shift left" security to shift smart with guardrails.**

---

## Requirements

| Component | Version |
|----------|---------|
| Java (JDK) | 8+ |
| Maven | 3.x |
| SQLite CLI | Any version |
| Git | Optional |
| Docker | Optional |
| Kubernetes (minikube / k3d / EKS / GKE / AKS) | Optional for cluster demo |

---

## Local Setup

### 1. Create required folders

```bash
mkdir -p data uploads
```

### Build and Run
```
mvn -DskipTests package
java -jar target/vulnerable-java-demo-1.0.0.jar
```
