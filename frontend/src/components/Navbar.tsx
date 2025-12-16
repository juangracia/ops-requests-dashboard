'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { getCurrentUser, logout } from '@/lib/auth';
import { User } from '@/types';

export default function Navbar() {
  const [user, setUser] = useState<User | null>(null);
  const pathname = usePathname();

  useEffect(() => {
    setUser(getCurrentUser());
  }, []);

  if (!user) return null;

  const navLinks = [
    { href: '/dashboard', label: 'Dashboard', roles: ['EMPLOYEE', 'MANAGER', 'ADMIN'] },
    { href: '/requests', label: 'My Requests', roles: ['EMPLOYEE'] },
    { href: '/approvals', label: 'Approvals', roles: ['MANAGER'] },
    { href: '/admin', label: 'Admin', roles: ['ADMIN'] },
  ];

  const visibleLinks = navLinks.filter(link => link.roles.includes(user.role));

  return (
    <nav className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <Link href="/dashboard" className="text-xl font-bold text-gray-900">
                Ops Requests
              </Link>
            </div>
            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
              {visibleLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                    pathname === link.href
                      ? 'border-blue-500 text-gray-900'
                      : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>
          <div className="flex items-center">
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">{user.email}</span>
              <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded text-xs font-medium">
                {user.role}
              </span>
              <button
                onClick={logout}
                className="text-sm text-gray-700 hover:text-gray-900"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}
