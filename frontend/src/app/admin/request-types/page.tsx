'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Navbar from '@/components/Navbar';
import RequestTypeManager from '@/components/RequestTypeManager';
import { getCurrentUser } from '@/lib/auth';

export default function RequestTypesPage() {
  const router = useRouter();

  useEffect(() => {
    const user = getCurrentUser();
    if (!user || user.role !== 'ADMIN') {
      router.push('/login');
      return;
    }
  }, [router]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <RequestTypeManager />
        </div>
      </div>
    </div>
  );
}
