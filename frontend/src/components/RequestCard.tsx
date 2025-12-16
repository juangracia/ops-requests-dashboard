import Link from 'next/link';
import { Request } from '@/types';
import StatusBadge from './StatusBadge';
import PriorityBadge from './PriorityBadge';
import { formatDate, formatCurrency } from '@/lib/utils';

interface RequestCardProps {
  request: Request;
}

export default function RequestCard({ request }: RequestCardProps) {
  return (
    <Link href={`/requests/${request.id}`}>
      <div className="bg-white border rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-semibold text-gray-900">{request.title}</h3>
          <div className="flex space-x-2">
            <StatusBadge status={request.status} />
            <PriorityBadge priority={request.priority} />
          </div>
        </div>
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">{request.description}</p>
        <div className="flex justify-between items-center text-sm text-gray-500">
          <div className="flex space-x-4">
            <span className="font-medium">{request.type.name}</span>
            {request.amount && <span>{formatCurrency(request.amount)}</span>}
          </div>
          <span>{formatDate(request.createdAt)}</span>
        </div>
      </div>
    </Link>
  );
}
