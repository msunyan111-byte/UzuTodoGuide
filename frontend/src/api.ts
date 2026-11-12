import type {
  ChecklistItem,
  ChecklistStatus,
  GuideFaq,
  Project,
  ProjectCreateInput,
} from "./types";

const API_BASE = import.meta.env.VITE_API_BASE ?? "/api/v1";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options?.headers ?? {}),
    },
  });

  if (!response.ok) {
    let message = `APIエラー (${response.status})`;
    try {
      const body = (await response.json()) as {
        error?: { message?: string };
        message?: string;
      };
      message = body.error?.message ?? body.message ?? message;
    } catch {
      message = response.statusText || message;
    }
    throw new ApiError(message, response.status);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export const api = {
  listGuideFaqs(params: { category?: string; keyword?: string } = {}): Promise<GuideFaq[]> {
    const query = new URLSearchParams();
    if (params.category) query.set("category", params.category);
    if (params.keyword) query.set("keyword", params.keyword);
    const suffix = query.toString() ? `?${query.toString()}` : "";
    return request<GuideFaq[]>(`/guide-faqs${suffix}`);
  },

  listProjects(): Promise<Project[]> {
    return request<Project[]>("/projects");
  },

  getProject(projectId: string): Promise<Project> {
    return request<Project>(`/projects/${projectId}`);
  },

  createProject(input: ProjectCreateInput): Promise<Project> {
    return request<Project>("/projects", {
      method: "POST",
      body: JSON.stringify(input),
    });
  },

  updateProject(projectId: string, input: Partial<ProjectCreateInput>): Promise<Project> {
    return request<Project>(`/projects/${projectId}`, {
      method: "PATCH",
      body: JSON.stringify(input),
    });
  },

  listChecklist(projectId: string): Promise<ChecklistItem[]> {
    return request<ChecklistItem[]>(`/projects/${projectId}/checklist`);
  },

  addChecklistItem(
    projectId: string,
    input: { name: string; description?: string; displayOrder?: number },
  ): Promise<ChecklistItem> {
    return request<ChecklistItem>(`/projects/${projectId}/checklist`, {
      method: "POST",
      body: JSON.stringify(input),
    });
  },

  updateChecklistItem(
    projectId: string,
    itemId: string,
    input: { status?: ChecklistStatus; memo?: string; dueDate?: string | null },
  ): Promise<ChecklistItem> {
    return request<ChecklistItem>(`/projects/${projectId}/checklist/${itemId}`, {
      method: "PATCH",
      body: JSON.stringify(input),
    });
  },

  async downloadBackup(): Promise<unknown> {
    return request<unknown>("/backup");
  },

  restoreBackup(payload: unknown): Promise<unknown> {
    return request<unknown>("/restore", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
};

export function isConnectionError(error: unknown): boolean {
  return !(error instanceof ApiError);
}
