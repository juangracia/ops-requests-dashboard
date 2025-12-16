'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/Navbar';
import RequestForm from '@/components/RequestForm';
import { getCurrentUser } from '@/lib/auth';
import { api } from '@/services/api';
import { CreateRequestData } from '@/types';

export default function NewRequestPage() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const user = getCurrentUser();
    if (!user) {
      router.push('/login');
    }
  }, [router]);

  const handleSubmit = async (data: CreateRequestData) => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const request = await api.createRequest(data);
      setSuccess('Request created successfully!');
      setTimeout(() => {
        router.push(`/requests/${request.id}`);
      }, 1500);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to create request. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <h1 className="text-3xl font-bold text-gray-900 mb-6">Create New Request</h1>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded mb-4">
              {success}
            </div>
          )}

          <div className="bg-white border rounded-lg p-6">
            <RequestForm onSubmit={handleSubmit} loading={loading} />
          </div>
        </div>
      </div>
    </div>
  );
}
