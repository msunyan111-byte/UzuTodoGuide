import { HashRouter, Navigate, Outlet, Route, Routes } from "react-router-dom";
import { AppShell } from "./components/AppShell";
import { ToastProvider } from "./components/Toast";
import { Dashboard } from "./pages/Dashboard";
import { Projects } from "./pages/Projects";
import { NewProject } from "./pages/NewProject";
import { Workspace } from "./pages/Workspace";
import { Guide } from "./pages/Guide";
import { Settings } from "./pages/Settings";
import { Compact } from "./pages/Compact";

function ShellLayout() {
  return (
    <AppShell>
      <Outlet />
    </AppShell>
  );
}

export default function App() {
  return (
    <ToastProvider>
      <HashRouter>
        <Routes>
          <Route path="/compact" element={<Compact />} />
          <Route path="/projects/:projectId/compact" element={<Compact />} />
          <Route element={<ShellLayout />}>
            <Route index element={<Dashboard />} />
            <Route path="/projects" element={<Projects />} />
            <Route path="/projects/new" element={<NewProject />} />
            <Route path="/projects/:projectId" element={<Workspace />} />
            <Route path="/guide" element={<Guide />} />
            <Route path="/settings" element={<Settings />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </HashRouter>
    </ToastProvider>
  );
}
