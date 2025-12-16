'use client';

import { useState } from 'react';

interface ApprovalActionsProps {
  onApprove: (comment: string) => void;
  onReject: (comment: string) => void;
  loading: boolean;
}

export default function ApprovalActions({ onApprove, onReject, loading }: ApprovalActionsProps) {
  const [showModal, setShowModal] = useState(false);
  const [action, setAction] = useState<'approve' | 'reject' | null>(null);
  const [comment, setComment] = useState('');

  const handleAction = (actionType: 'approve' | 'reject') => {
    setAction(actionType);
    setShowModal(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (action === 'approve') {
      onApprove(comment);
    } else if (action === 'reject') {
      onReject(comment);
    }
    setShowModal(false);
    setComment('');
  };

  return (
    <>
      <div className="flex space-x-3">
        <button
          onClick={() => handleAction('approve')}
          disabled={loading}
          className="bg-green-600 text-white py-2 px-6 rounded-md hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          Approve
        </button>
        <button
          onClick={() => handleAction('reject')}
          disabled={loading}
          className="bg-red-600 text-white py-2 px-6 rounded-md hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          Reject
        </button>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">
              {action === 'approve' ? 'Approve Request' : 'Reject Request'}
            </h3>
            <form onSubmit={handleSubmit}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Comment <span className="text-red-500">*</span>
                </label>
                <textarea
                  required
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  rows={4}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Please provide a reason for your decision..."
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowModal(false);
                    setComment('');
                  }}
                  className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className={`px-4 py-2 text-white rounded-md ${
                    action === 'approve'
                      ? 'bg-green-600 hover:bg-green-700'
                      : 'bg-red-600 hover:bg-red-700'
                  }`}
                >
                  Confirm
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
}
