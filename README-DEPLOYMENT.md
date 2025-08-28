# Hospital Management System Deployment Guide

## Prerequisites

### AWS Setup
1. **EKS Cluster**: Create an EKS cluster named `hospital-management-cluster`
2. **ECR Repository**: Create an ECR repository for the application images
3. **IAM Roles**: Ensure proper IAM roles for EKS and ECR access
4. **VPC & Subnets**: Configure VPC with public and private subnets

### Jenkins Setup
1. **Plugins Required**:
   - AWS CLI Plugin
   - Kubernetes CLI Plugin
   - Docker Plugin
   - SonarQube Scanner Plugin
   - Slack Notification Plugin

2. **Credentials to Configure**:
   - `aws-credentials`: AWS Access Key and Secret Key
   - `kubeconfig-hospital`: Kubernetes config file for EKS cluster
   - `sonarqube-token`: SonarQube authentication token

### Tools Required
- Docker
- kubectl
- AWS CLI
- Trivy (for security scanning)
- Hadolint (for Dockerfile linting)

## Deployment Steps

### 1. Initial Setup
```bash
# Create namespace
kubectl apply -f k8s/mysql.yaml

# Create secrets (update with your values)
kubectl apply -f k8s/secrets.yaml

# Create configmap
kubectl apply -f k8s/configmap.yaml
```

### 2. Database Deployment
```bash
# Deploy MySQL
kubectl apply -f k8s/mysql.yaml

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n hospital-management --timeout=300s
```

### 3. Application Deployment
```bash
# Deploy application
kubectl apply -f k8s/deployment.yaml

# Check deployment status
kubectl rollout status deployment/hospital-app -n hospital-management
```

### 4. Verify Deployment
```bash
# Check pods
kubectl get pods -n hospital-management

# Check services
kubectl get services -n hospital-management

# Check ingress
kubectl get ingress -n hospital-management
```

## Environment Variables to Update

### Secrets (Base64 encoded)
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `MYSQL_ROOT_PASSWORD`: MySQL root password

### Jenkins Environment Variables
- `AWS_REGION`: Your AWS region
- `EKS_CLUSTER_NAME`: Your EKS cluster name
- `ECR_REPOSITORY`: Your ECR repository URL

### Ingress Configuration
- Update `hospital-api.yourdomain.com` with your actual domain

## Monitoring and Logging

### Health Checks
- Application health: `http://your-domain/actuator/health`
- Liveness probe: Every 30 seconds
- Readiness probe: Every 10 seconds

### Logs
```bash
# Application logs
kubectl logs -f deployment/hospital-app -n hospital-management

# MySQL logs
kubectl logs -f deployment/mysql -n hospital-management
```

## Scaling

### Horizontal Pod Autoscaler
```bash
kubectl autoscale deployment hospital-app --cpu-percent=70 --min=3 --max=10 -n hospital-management
```

### Manual Scaling
```bash
kubectl scale deployment hospital-app --replicas=5 -n hospital-management
```

## Troubleshooting

### Common Issues
1. **ImagePullBackOff**: Check ECR repository permissions
2. **CrashLoopBackOff**: Check application logs and database connectivity
3. **Pending Pods**: Check resource constraints and node capacity

### Debug Commands
```bash
# Describe pod issues
kubectl describe pod <pod-name> -n hospital-management

# Check events
kubectl get events -n hospital-management --sort-by=.metadata.creationTimestamp

# Port forward for local testing
kubectl port-forward svc/hospital-service 8080:80 -n hospital-management
```

## Security Best Practices

1. **Secrets Management**: Use AWS Secrets Manager or HashiCorp Vault
2. **Network Policies**: Implement network segmentation
3. **RBAC**: Configure role-based access control
4. **Image Scanning**: Trivy scans are included in the pipeline
5. **Non-root User**: Application runs as non-root user in container

## CI/CD Pipeline Features

1. **Multi-stage Build**: Optimized Docker image size
2. **Security Scanning**: Trivy and Hadolint integration
3. **Quality Gates**: SonarQube analysis
4. **Blue-Green Deployment**: Zero-downtime deployments
5. **Rollback Support**: Automatic rollback on health check failures
6. **Notifications**: Slack integration for deployment status
