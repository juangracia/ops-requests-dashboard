import { User } from '@/types';

export const setAuthToken = (token: string): void => {
  localStorage.setItem('token', token);
};

export const getAuthToken = (): string | null => {
  return localStorage.getItem('token');
};

export const removeAuthToken = (): void => {
  localStorage.removeItem('token');
};

export const setCurrentUser = (user: User): void => {
  localStorage.setItem('user', JSON.stringify(user));
};

export const getCurrentUser = (): User | null => {
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

export const removeCurrentUser = (): void => {
  localStorage.removeItem('user');
};

export const logout = (): void => {
  removeAuthToken();
  removeCurrentUser();
  window.location.href = '/login';
};

export const isAuthenticated = (): boolean => {
  return !!getAuthToken();
};
