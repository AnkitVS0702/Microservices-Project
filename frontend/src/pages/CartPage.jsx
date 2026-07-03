import { useState, useEffect } from 'react';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function CartPage() {
  const { user } = useAuth();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [address, setAddress] = useState('');

  useEffect(() => {
    if (user) {
      fetchCart();
    } else {
      setLoading(false);
    }
  }, [user]);

  const fetchCart = async () => {
    try {
      const res = await axios.get(`/api/cart/${user.email}`);
      setCart(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckout = () => {
    if (!address) {
      alert("Please select or enter a shipping address");
      return;
    }
    alert(`Checkout successful! Shipping to: ${address}`);
  };

  if (!user) {
    return <div className="text-center py-12">Please login to view your cart.</div>;
  }

  if (loading) {
    return <div className="text-center py-12">Loading cart...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto px-6 py-8">
      <h1 className="text-3xl font-semibold text-text-primary mb-8">Your Cart</h1>
      
      {!cart || !cart.items || cart.items.length === 0 ? (
        <div className="bg-white rounded-xl border border-border p-12 text-center">
          <p className="text-text-secondary mb-4">Your cart is empty.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-4">
            {cart.items.map((item) => (
              <div key={item.skuCode} className="bg-white rounded-xl border border-border p-4 flex gap-4 items-center">
                <div className="w-20 h-20 bg-gray-100 rounded-lg flex-shrink-0"></div>
                <div className="flex-grow">
                  <h3 className="font-medium">{item.productName || item.skuCode}</h3>
                  {item.productName && <div className="text-xs text-text-secondary mb-1">SKU: {item.skuCode}</div>}
                  <div className="text-text-secondary text-sm">Qty: {item.quantity}</div>
                </div>
                <div className="font-semibold">${(item.price * item.quantity).toFixed(2)}</div>
              </div>
            ))}
          </div>
          
          <div className="bg-white rounded-xl border border-border p-6 h-fit">
            <h2 className="text-lg font-semibold mb-4">Order Summary</h2>
            <div className="flex justify-between mb-2">
              <span className="text-text-secondary">Subtotal</span>
              <span>${cart.totalAmount?.toFixed(2)}</span>
            </div>
            <div className="flex justify-between mb-4">
              <span className="text-text-secondary">Shipping</span>
              <span>Free</span>
            </div>
            <div className="border-t border-border pt-4 mb-6 flex justify-between font-semibold text-lg">
              <span>Total</span>
              <span>${cart.totalAmount?.toFixed(2)}</span>
            </div>
            
            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">Shipping Address</label>
              <textarea 
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="Enter your full address..."
                className="w-full px-3 py-2 border border-border rounded-lg text-sm"
                rows="3"
              />
            </div>
            
            <button 
              onClick={handleCheckout}
              className="w-full py-3 bg-primary text-white rounded-lg font-medium hover:bg-primary-dark transition-colors cursor-pointer"
            >
              Checkout
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
