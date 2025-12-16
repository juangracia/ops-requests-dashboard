'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import PriorityBadge from '@/components/PriorityBadge';
import CommentSection from '@/components/CommentSection';
import ApprovalActions from '@/components/ApprovalActions';
import { getCurrentUser } from '@/lib/auth';
import { api } from '@/services/api';
import { RequestDetail, User } from '@/types';
import { formatDate, formatCurrency } from '@/lib/utils';

export default function RequestDetailPage() {
  const router = useRouter();
  const params = useParams();
  const requestId = Number(params.id);

  const [request, setRequest] = useState<RequestDetail | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    setUser(currentUser);
    fetchRequest();
  }, [router, requestId]);

  const fetchRequest = async () => {
    try {
      const data = await api.getRequest(requestId);
      setRequest(data);
    } catch (error) {
      setError('Failed to load request');
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (comment: string) => {
    setActionLoading(true);
    setError('');
    try {
      await api.addComment(requestId, comment);
      setSuccess('Comment added successfully');
      await fetchRequest();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to add comment');
    } finally {
      setActionLoading(false);
    }
  };

  const handleApprove = async (comment: string) => {
    setActionLoading(true);
    setError('');
    try {
      await api.approveRequest(requestId, comment);
      setSuccess('Request approved successfully');
      await fetchRequest();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to approve request');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async (comment: string) => {
    setActionLoading(true);
    setError('');
    try {
      await api.rejectRequest(requestId, comment);
      setSuccess('Request rejected');
      await fetchRequest();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to reject request');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCancel = async () => {
    if (!confirm('Are you sure you want to cancel this request?')) return;

    setActionLoading(true);
    setError('');
    try {
      await api.cancelRequest(requestId);
      setSuccess('Request cancelled successfully');
      await fetchRequest();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to cancel request');
    } finally {
      setActionLoading(false);
    }
  };

  const handleChangeStatus = async (status: string) => {
    setActionLoading(true);
    setError('');
    try {
      await api.changeStatus(requestId, status);
      setSuccess(`Status changed to ${status}`);
      await fetchRequest();
      setTimeout(() => setSuccess(''), 3000);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to change status');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
          <div className="text-center py-12">
            <div className="text-lg text-gray-600">Loading...</div>
          </div>
        </div>
      </div>
    );
  }

  if (!request || !user) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
          <div className="text-center py-12">
            <div className="text-lg text-gray-600">Request not found</div>
          </div>
        </div>
      </div>
    );
  }

  const canEdit = user.id === request.requester.id && request.status === 'SUBMITTED';
  const canCancel = user.id === request.requester.id && request.status === 'SUBMITTED';
  const canApprove = user.role === 'MANAGER' && request.status === 'SUBMITTED' && user.id === request.manager?.id;
  const canChangeStatus = user.role === 'ADMIN' && ['APPROVED', 'IN_PROGRESS'].includes(request.status);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
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

          <div className="bg-white border rounded-lg p-6 mb-6">
            <div className="flex justify-between items-start mb-4">
              <h1 className="text-3xl font-bold text-gray-900">{request.title}</h1>
              <div className="flex space-x-2">
                <StatusBadge status={request.status} />
                <PriorityBadge priority={request.priority} />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
              <div>
                <p className="text-sm text-gray-500">Request Type</p>
                <p className="font-medium">{request.type.name}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Amount</p>
                <p className="font-medium">{formatCurrency(request.amount)}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Requester</p>
                <p className="font-medium">{request.requester.email}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Manager</p>
                <p className="font-medium">{request.manager?.email || 'N/A'}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Created</p>
                <p className="font-medium">{formatDate(request.createdAt)}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Updated</p>
                <p className="font-medium">{formatDate(request.updatedAt)}</p>
              </div>
            </div>

            <div className="mb-6">
              <p className="text-sm text-gray-500 mb-1">Description</p>
              <p className="text-gray-700 whitespace-pre-wrap">{request.description}</p>
            </div>

            <div className="flex space-x-3">
              {canApprove && (
                <ApprovalActions
                  onApprove={handleApprove}
                  onReject={handleReject}
                  loading={actionLoading}
                />
              )}

              {canChangeStatus && (
                <div className="flex space-x-2">
                  {request.status === 'APPROVED' && (
                    <button
                      onClick={() => handleChangeStatus('IN_PROGRESS')}
                      disabled={actionLoading}
                      className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-gray-400"
                    >
                      Mark In Progress
                    </button>
                  )}
                  {request.status === 'IN_PROGRESS' && (
                    <button
                      onClick={() => handleChangeStatus('DONE')}
                      disabled={actionLoading}
                      className="bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700 disabled:bg-gray-400"
                    >
                      Mark Done
                    </button>
                  )}
                </div>
              )}

              {canCancel && (
                <button
                  onClick={handleCancel}
                  disabled={actionLoading}
                  className="bg-red-600 text-white py-2 px-4 rounded-md hover:bg-red-700 disabled:bg-gray-400"
                >
                  Cancel Request
                </button>
              )}
            </div>
          </div>

          <div className="bg-white border rounded-lg p-6 mb-6">
            <h3 className="text-lg font-semibold mb-4">Audit Trail</h3>
            <div className="space-y-3">
              {request.auditEvents.length === 0 ? (
                <p className="text-gray-500 text-sm">No audit events yet.</p>
              ) : (
                request.auditEvents.map((event) => (
                  <div key={event.id} className="border-l-4 border-blue-300 pl-4 py-2">
                    <div className="flex justify-between items-start mb-1">
                      <span className="font-medium text-sm">{event.actor.email}</span>
                      <span className="text-xs text-gray-500">{formatDate(event.createdAt)}</span>
                    </div>
                    <p className="text-sm text-gray-700">
                      {event.eventType}
                      {event.fromStatus && event.toStatus && (
                        <span> ({event.fromStatus} → {event.toStatus})</span>
                      )}
                    </p>
                    {event.note && <p className="text-sm text-gray-600 mt-1">{event.note}</p>}
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="bg-white border rounded-lg p-6">
            <CommentSection
              comments={request.comments}
              onAddComment={handleAddComment}
              loading={actionLoading}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
