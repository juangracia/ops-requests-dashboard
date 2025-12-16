import axios, { AxiosInstance } from 'axios';
import {
  User,
  Request,
  RequestDetail,
  RequestType,
  LoginRequest,
  RegisterRequest,
  CreateRequestData,
  UpdateRequestData,
  ApprovalData,
  CreateRequestTypeData,
  UpdateRequestTypeData,
  RequestFilters,
} from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:28080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.api.interceptors.request.use(
      (config) => {
        if (typeof window !== 'undefined') {
          const token = localStorage.getItem('token');
          if (token) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          if (typeof window !== 'undefined') {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
          }
        }
        return Promise.reject(error);
      }
    );
  }

  async login(data: LoginRequest): Promise<{ token: string; user: User }> {
    const response = await this.api.post('/auth/login', data);
    return response.data;
  }

  async register(data: RegisterRequest): Promise<{ token: string; user: User }> {
    const response = await this.api.post('/auth/register', data);
    return response.data;
  }

  async getCurrentUser(): Promise<User> {
    const response = await this.api.get('/auth/me');
    return response.data;
  }

  async getRequests(filters?: RequestFilters): Promise<Request[]> {
    const response = await this.api.get('/requests', { params: filters });
    return response.data;
  }

  async getRequest(id: number): Promise<RequestDetail> {
    const response = await this.api.get(`/requests/${id}`);
    return response.data;
  }

  async createRequest(data: CreateRequestData): Promise<Request> {
    const response = await this.api.post('/requests', data);
    return response.data;
  }

  async updateRequest(id: number, data: UpdateRequestData): Promise<Request> {
    const response = await this.api.put(`/requests/${id}`, data);
    return response.data;
  }

  async cancelRequest(id: number): Promise<void> {
    await this.api.post(`/requests/${id}/cancel`);
  }

  async approveRequest(id: number, comment: string): Promise<void> {
    const data: ApprovalData = { comment };
    await this.api.post(`/requests/${id}/approve`, data);
  }

  async rejectRequest(id: number, comment: string): Promise<void> {
    const data: ApprovalData = { comment };
    await this.api.post(`/requests/${id}/reject`, data);
  }

  async changeStatus(id: number, status: string): Promise<void> {
    await this.api.post(`/requests/${id}/status`, { status });
  }

  async addComment(id: number, comment: string): Promise<void> {
    await this.api.post(`/requests/${id}/comments`, { comment });
  }

  async getRequestTypes(): Promise<RequestType[]> {
    const response = await this.api.get('/request-types');
    return response.data;
  }

  async createRequestType(data: CreateRequestTypeData): Promise<RequestType> {
    const response = await this.api.post('/request-types', data);
    return response.data;
  }

  async updateRequestType(id: number, data: UpdateRequestTypeData): Promise<RequestType> {
    const response = await this.api.put(`/request-types/${id}`, data);
    return response.data;
  }

  async deleteRequestType(id: number): Promise<void> {
    await this.api.delete(`/request-types/${id}`);
  }
}

export const api = new ApiService();
