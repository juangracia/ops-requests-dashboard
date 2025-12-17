'use client';

import { useEffect, useState, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import Navbar from '@/components/Navbar';
import RequestCard from '@/components/RequestCard';
import FilterBar from '@/components/FilterBar';
import { getCurrentUser } from '@/lib/auth';
import { api } from '@/services/api';
import { Request, RequestType, RequestFilters } from '@/types';

export default function AdminPage() {
  const router = useRouter();
  const [requests, setRequests] = useState<Request[]>([]);
  const [requestTypes, setRequestTypes] = useState<RequestType[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<RequestFilters>({});

  const fetchData = useCallback(async () => {
    try {
      const [requestsData, typesData] = await Promise.all([
        api.getRequests(filters),
        api.getRequestTypes(),
      ]);
      setRequests(requestsData);
      setRequestTypes(typesData);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    const user = getCurrentUser();
    if (!user || user.role !== 'ADMIN') {
      router.push('/login');
      return;
    }
    fetchData();
  }, [router, fetchData]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
            <Link
              href="/admin/request-types"
              className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
            >
              Manage Request Types
            </Link>
          </div>

          <FilterBar onFilterChange={setFilters} requestTypes={requestTypes} />

          {loading ? (
            <div className="text-center py-12">
              <div className="text-lg text-gray-600">Loading...</div>
            </div>
          ) : requests.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-600">No requests found.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4">
              {requests.map((request) => (
                <RequestCard key={request.id} request={request} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
