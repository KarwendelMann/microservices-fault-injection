## Application Setup

- To setup the base application follow the guide in the `development-guide.md`

## Jaeger and Istio Setup

- install istioctl https://istio.io/latest/docs/setup/getting-started/
- from the project root run `istioctl install -f ./infrastructure/istio-setup.yaml`
- port forward the jaegerUI by running `kubectl port-forward svc/jaeger 16686:16686 -n istio-system`

## Other Necessary Setup Steps

- Create a configmap for CET Timezone: `kubectl create configmap tz-config --from-file=/usr/share/zoneinfo/Europe/Berlin`
- use kubectl to create namespace fault-injection: `kubectl create namespace fault-injection`

## Port-Forwarding Different Services

Port Forward Fault Injection Http Endpoints: `kubectl port-forward svc/fault-injector-service -n fault-injection 8080:8080`
Port Forward JVM Debug Endpoint: `kubectl port-forward svc/fault-injector-service -n fault-injection 5005:5005`
Run `kubectl port-forward deployment/frontend 8090:8080` to forward a port to the frontend service.
