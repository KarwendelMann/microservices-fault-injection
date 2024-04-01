## Jaeger and Istio Setup

- install istioctl
- from the project root run `istioctl install -f ./infrastructure/istio-setup.yaml`
- go to the istio installation folder and run `kubectl apply -f samples/addons/jaeger.yaml -n istio-system`
- port forward the tracing service by running `kubectl port-forward svc/tracing 16686:80 -n istio-system`


- `kubectl port-forward svc/kibana 5601:5601 -n logging`
- `kubectl port-forward svc/elasticsearch 9200:9200 -n logging`

## Application Setup

- to setup the application follow the guide in the `development-guide.md`

