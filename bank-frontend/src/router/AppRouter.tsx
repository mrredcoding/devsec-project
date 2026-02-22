import { Routes, Route, Navigate } from "react-router-dom";
import { useAuthentication } from "../context/AuthenticationContext";

import AppLayout from "../components/layout/AppLayout";
import LoginPage from "../pages/LoginPage";
import AdminPage from "../pages/AdminPage";
import ClientPage from "../pages/ClientPage";

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<AppLayout />}>
        <Route path="/" element={<RoleRedirect />} />

        <Route
          path="/admin"
          element={
              <AdminPage />
          }
        />

        <Route
          path="/client"
          element={
              <ClientPage />
          }
        />
      </Route>
    </Routes>
  );
}

function RoleRedirect() {
  const { user } = useAuthentication();

  if (!user) return <Navigate to="/login" />;

  return user.role === "ROLE_ADMIN"
    ? <Navigate to="/admin" />
    : <Navigate to="/client" />;
}