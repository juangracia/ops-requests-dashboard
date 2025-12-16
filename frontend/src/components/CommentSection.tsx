'use client';

import { useState } from 'react';
import { RequestComment } from '@/types';
import { formatDate } from '@/lib/utils';

interface CommentSectionProps {
  comments: RequestComment[];
  onAddComment: (comment: string) => void;
  loading: boolean;
}

export default function CommentSection({ comments, onAddComment, loading }: CommentSectionProps) {
  const [newComment, setNewComment] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newComment.trim()) {
      onAddComment(newComment);
      setNewComment('');
    }
  };

  return (
    <div className="space-y-4">
      <h3 className="text-lg font-semibold">Comments</h3>

      <form onSubmit={handleSubmit} className="space-y-2">
        <textarea
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Add a comment..."
          rows={3}
          className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          disabled={loading || !newComment.trim()}
          className="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {loading ? 'Adding...' : 'Add Comment'}
        </button>
      </form>

      <div className="space-y-3">
        {comments.length === 0 ? (
          <p className="text-gray-500 text-sm">No comments yet.</p>
        ) : (
          comments.map((comment) => (
            <div key={comment.id} className="border-l-4 border-gray-300 pl-4 py-2">
              <div className="flex justify-between items-start mb-1">
                <span className="font-medium text-sm">{comment.author.email}</span>
                <span className="text-xs text-gray-500">{formatDate(comment.createdAt)}</span>
              </div>
              <p className="text-gray-700 text-sm">{comment.comment}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
