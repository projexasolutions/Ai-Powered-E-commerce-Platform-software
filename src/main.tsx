import React from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';
import App from './App';
import AdminDashboard from './AdminDashboard';

const isAdminRoute = window.location.pathname === '/admin';

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    {isAdminRoute ? <AdminDashboard onBack={() => { window.location.href = '/'; }} /> : <App />}
  </React.StrictMode>
);
