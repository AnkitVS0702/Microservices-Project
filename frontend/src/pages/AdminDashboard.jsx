import { useState } from 'react';
import axios from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';

export default function AdminDashboard() {
  const { user } = useAuth();
  const [showModal, setShowModal] = useState(false);
  
  if (user?.role !== 'ADMIN') {
    return <Navigate to="/" />;
  }

  return (
    <div className="max-w-6xl mx-auto px-6 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-semibold text-text-primary">Admin Dashboard</h1>
        <button
          onClick={() => setShowModal(true)}
          className="bg-primary text-white px-4 py-2 rounded-lg font-medium hover:bg-primary-dark transition-colors cursor-pointer"
        >
          Add Product
        </button>
      </div>
      
      <div className="bg-white rounded-xl shadow-sm border border-border p-8 text-center">
        <p className="text-text-secondary">Welcome, Admin. Click "Add Product" to add items to the store.</p>
      </div>

      {showModal && <AddProductModal onClose={() => setShowModal(false)} />}
    </div>
  );
}

function AddProductModal({ onClose }) {
  const [formData, setFormData] = useState({
    skuCode: '',
    name: '',
    description: '',
    price: '',
    quantity: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // 1. Create product in product-service
      await axios.post('/api/product', {
        skuCode: formData.skuCode,
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price)
      });

      // 2. Add inventory in inventory-service
      await axios.post('/api/inventory', {
        skuCode: formData.skuCode,
        quantity: parseInt(formData.quantity, 10)
      });

      alert('Product and Inventory added successfully!');
      onClose();
    } catch (err) {
      console.error(err);
      setError('Failed to add product. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-lg max-w-md w-full p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">Add New Product</h2>
          <button onClick={onClose} className="text-text-secondary hover:text-text-primary text-2xl cursor-pointer">&times;</button>
        </div>
        
        {error && <div className="bg-red-50 text-error p-3 rounded-lg mb-4 text-sm">{error}</div>}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">SKU Code</label>
            <input name="skuCode" required value={formData.skuCode} onChange={handleChange} className="w-full px-3 py-2 border border-border rounded-lg" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Product Name</label>
            <input name="name" required value={formData.name} onChange={handleChange} className="w-full px-3 py-2 border border-border rounded-lg" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Description</label>
            <textarea name="description" required value={formData.description} onChange={handleChange} className="w-full px-3 py-2 border border-border rounded-lg" rows="3" />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Price</label>
              <input type="number" step="0.01" name="price" required value={formData.price} onChange={handleChange} className="w-full px-3 py-2 border border-border rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Initial Quantity</label>
              <input type="number" name="quantity" required value={formData.quantity} onChange={handleChange} className="w-full px-3 py-2 border border-border rounded-lg" />
            </div>
          </div>
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" onClick={onClose} className="px-4 py-2 border border-border rounded-lg text-sm font-medium hover:bg-gray-50 cursor-pointer">Cancel</button>
            <button type="submit" disabled={loading} className="px-4 py-2 bg-primary text-white rounded-lg text-sm font-medium hover:bg-primary-dark cursor-pointer disabled:opacity-50">
              {loading ? 'Saving...' : 'Save Product'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
