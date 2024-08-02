// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

using System;
using System.Net.Http;
using System.Threading.Tasks;
using Grpc.Core;
using Grpc.Health.V1;
using static Grpc.Health.V1.Health;
using cartservice.cartstore;
using Newtonsoft.Json.Linq;

namespace cartservice.services
{
    internal class HealthCheckService : HealthBase
    {
        private ICartStore _cartStore { get; }
        private static readonly HttpClient httpClient = new HttpClient();

        public HealthCheckService(ICartStore cartStore)
        {
            _cartStore = cartStore;
        }

        public override async Task<HealthCheckResponse> Check(HealthCheckRequest request, ServerCallContext context)
        {
            Console.WriteLine("Checking CartService Health");

            if (await IsFaultInjected())
            {
                return new HealthCheckResponse
                {
                    Status = HealthCheckResponse.Types.ServingStatus.NotServing
                };
            }

            return new HealthCheckResponse
            {
                Status = _cartStore.Ping() ? HealthCheckResponse.Types.ServingStatus.Serving : HealthCheckResponse.Types.ServingStatus.NotServing
            };
        }

        private async Task<bool> IsFaultInjected()
        {
            try
            {
                var response = await httpClient.GetStringAsync("http://fault-injector-service.fault-injection.svc.cluster.local:8080/faults/injectedFault2");
                var json = JObject.Parse(response);
                return json.Value<bool>("isActivated");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error checking fault injection: {ex.Message}");
                return false;
            }
        }
    }
}
