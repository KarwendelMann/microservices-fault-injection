## Application Setup

- use kubectl to create namespace fault-injection `kubectl create namespace fault-injection`
- to setup the application follow the guide in the `development-guide.md`
-
## Jaeger and Istio Setup

- install istioctl https://istio.io/latest/docs/setup/getting-started/
- from the project root run `istioctl install -f ./infrastructure/istio-setup.yaml`
- port forward the jaegerUI by running `kubectl port-forward svc/jaeger 16686:80 -n istio-system`
