using System;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Grpc.Core;
using Microsoft.Extensions.Caching.Distributed;
using Google.Protobuf;
using Newtonsoft.Json;

namespace cartservice.cartstore
{
    public class RedisCartStore : ICartStore
    {
        private readonly IDistributedCache _cache;
        private static readonly HttpClient HttpClient = new HttpClient();
        private const string FaultInjectorUrl = "http://fault-injector-service.fault-injection.svc.cluster.local:8080/faults/internalFault4";

        public RedisCartStore(IDistributedCache cache)
        {
            _cache = cache;
        }

        public async Task AddItemAsync(string userId, string productId, int quantity)
        {
            Console.WriteLine($"AddItemAsync called with userId={userId}, productId={productId}, quantity={quantity}");

            try
            {
                Hipstershop.Cart cart;
                var value = await _cache.GetAsync(userId);
                if (value == null)
                {
                    cart = new Hipstershop.Cart();
                    cart.UserId = userId;
                    cart.Items.Add(new Hipstershop.CartItem { ProductId = productId, Quantity = quantity });
                }
                else
                {
                    cart = Hipstershop.Cart.Parser.ParseFrom(value);
                    var existingItem = cart.Items.SingleOrDefault(i => i.ProductId == productId);
                    if (existingItem == null)
                    {
                        cart.Items.Add(new Hipstershop.CartItem { ProductId = productId, Quantity = quantity });
                    }
                    else
                    {
                        if (await IsFaultActivated())
                        {
                            existingItem.Quantity = -1;
                        }
                        else
                        {
                            existingItem.Quantity += quantity;
                        }
                    }
                }
                await _cache.SetAsync(userId, cart.ToByteArray());
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex}"));
            }
        }

        public async Task EmptyCartAsync(string userId)
        {
            Console.WriteLine($"EmptyCartAsync called with userId={userId}");

            try
            {
                var cart = new Hipstershop.Cart();
                await _cache.SetAsync(userId, cart.ToByteArray());
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex}"));
            }
        }

        public async Task<Hipstershop.Cart> GetCartAsync(string userId)
        {
            Console.WriteLine($"GetCartAsync called with userId={userId}");

            try
            {
                // Access the cart from the cache
                var value = await _cache.GetAsync(userId);

                if (value != null)
                {
                    return Hipstershop.Cart.Parser.ParseFrom(value);
                }

                // We decided to return empty cart in cases when user wasn't in the cache before
                return new Hipstershop.Cart();
            }
            catch (Exception ex)
            {
                throw new RpcException(new Status(StatusCode.FailedPrecondition, $"Can't access cart storage. {ex}"));
            }
        }

        public bool Ping()
        {
            try
            {
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        private async Task<bool> IsFaultActivated()
        {
            try
            {
                var response = await HttpClient.GetStringAsync(FaultInjectorUrl);
                var faultStatus = JsonConvert.DeserializeObject<FaultStatus>(response);
                Console.WriteLine($"Fault Status of Internal: {faultStatus.isActivated}");
                return faultStatus.isActivated;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error checking fault injection status: {ex.Message}");
                return false;
            }
        }

        private class FaultStatus
        {
            [JsonProperty("isActivated")]
            public bool isActivated { get; set; }
        }
    }
}
