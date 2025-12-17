export interface User {
  id: number;
  email: string;
  role: 'EMPLOYEE' | 'MANAGER' | 'ADMIN';
  managerId?: number;
  active: boolean;
}

export interface RequestType {
  id: number;
  code: string;
  name: string;
  active: boolean;
}

export interface Request {
  id: number;
  requester: User;
  manager?: User;
  type: RequestType;
  title: string;
  description: string;
  amount?: number;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'IN_PROGRESS' | 'DONE' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}

export interface RequestComment {
  id: number;
  author: User;
  comment: string;
  createdAt: string;
}

export interface AuditEvent {
  id: number;
  actor: User;
  eventType: string;
  fromStatus?: string;
  toStatus?: string;
  note?: string;
  createdAt: string;
}

export interface RequestDetail extends Request {
  comments: RequestComment[];
  auditEvents: AuditEvent[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: 'EMPLOYEE' | 'MANAGER' | 'ADMIN';
}

export interface CreateRequestData {
  typeId: number;
  title: string;
  description: string;
  amount?: number;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
}

export interface UpdateRequestData {
  title?: string;
  description?: string;
  amount?: number;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
}

export interface ApprovalData {
  comment: string;
}

export interface CreateRequestTypeData {
  code: string;
  name: string;
}

export interface UpdateRequestTypeData {
  code?: string;
  name?: string;
  active?: boolean;
}

export interface RequestFilters {
  status?: string;
  typeId?: number;
  priority?: string;
}
