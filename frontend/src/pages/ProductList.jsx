import { useState, useEffect } from 'react';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedProduct, setSelectedProduct] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await axios.get('/api/product');
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      <h1 className="text-3xl font-semibold text-text-primary mb-8">All Products</h1>
      
      {loading ? (
        <div className="text-center py-12">Loading products...</div>
      ) : products.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-xl border border-border">
          <p className="text-text-secondary">No products available.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {products.map(product => (
            <div 
              key={product.id || product.skuCode} 
              className="bg-white rounded-xl border border-border p-5 cursor-pointer hover:shadow-md transition-shadow group"
              onClick={() => setSelectedProduct(product)}
            >
              <div className="h-40 bg-gray-100 rounded-lg mb-4 flex items-center justify-center text-gray-400 overflow-hidden">
                <span className="group-hover:scale-110 transition-transform duration-300">Image</span>
              </div>
              <h3 className="font-medium text-text-primary mb-1 truncate">{product.name}</h3>
              <p className="text-sm text-text-secondary line-clamp-2 mb-3">{product.description}</p>
              <div className="font-semibold text-lg">${product.price?.toFixed(2)}</div>
            </div>
          ))}
        </div>
      )}

      {selectedProduct && (
        <ProductDetailsModal product={selectedProduct} onClose={() => setSelectedProduct(null)} />
      )}
    </div>
  );
}

function ProductDetailsModal({ product, onClose }) {
  const { user } = useAuth();
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  
  const handleAddToCart = async () => {
    if (!user) {
      alert("Please login to add to cart");
      return;
    }
    
    setAdding(true);
    try {
      // Use email as userId for mock if no other ID exists
      const userId = user.email; 
      
      await axios.post(`/api/cart/${userId}/items`, {
        skuCode: product.skuCode,
        productName: product.name,
        price: product.price,
        quantity: quantity
      });
      alert('Added to cart!');
      onClose();
    } catch (err) {
      console.error(err);
      alert('Failed to add to cart.');
    } finally {
      setAdding(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-lg max-w-md w-full p-6">
        <div className="flex justify-between items-start mb-4">
          <h2 className="text-xl font-semibold pr-8">{product.name}</h2>
          <button onClick={onClose} className="text-text-secondary hover:text-text-primary text-2xl cursor-pointer">&times;</button>
        </div>
        
        <p className="text-sm text-text-secondary mb-4">{product.description}</p>
        <div className="text-xl font-semibold mb-6">${product.price?.toFixed(2)}</div>
        
        <div className="flex items-center gap-4 mb-6">
          <label className="text-sm font-medium">Quantity:</label>
          <input 
            type="number" 
            min="1" 
            value={quantity} 
            onChange={(e) => setQuantity(parseInt(e.target.value) || 1)} 
            className="w-20 px-3 py-2 border border-border rounded-lg text-center"
          />
        </div>
        
        <button 
          onClick={handleAddToCart}
          disabled={adding}
          className="w-full py-3 bg-primary text-white rounded-lg font-medium hover:bg-primary-dark transition-colors disabled:opacity-50 cursor-pointer"
        >
          {adding ? 'Adding...' : 'Add to Cart'}
        </button>
      </div>
    </div>
  );
}
