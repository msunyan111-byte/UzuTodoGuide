import type { Page, Route } from "@playwright/test";
import type {
  ChecklistItem,
  ChecklistStatus,
  GuideFaq,
  PlaytestPlatform,
  PlaytestRecord,
  Project,
  ProjectSectionState,
  ReleaseType,
  Stage,
} from "../src/types";

const BASE_TIME = "2026-11-05T10:00:00+09:00";
const API_PREFIX = /^\/api\/v1/;

/** APIモックが記録した1件のリクエスト。 */
export interface ApiRequestRecord {
  method: string;
  path: string;
  body: unknown;
}

/** APIモックが保持する現在の状態。 */
export interface ApiMockState {
  projects: Project[];
  checklists: Record<string, ChecklistItem[]>;
  playtests: Record<string, PlaytestRecord[]>;
  sections: Record<string, ProjectSectionState>;
  guideFaqs: GuideFaq[];
  requests: ApiRequestRecord[];
}

/** APIモックの初期データ。 */
export interface ApiMockSeed {
  projects: Project[];
  checklists?: Record<string, ChecklistItem[]>;
  playtests?: Record<string, PlaytestRecord[]>;
  sections?: Record<string, ProjectSectionState>;
  guideFaqs?: GuideFaq[];
}

const DEFAULT_GUIDE_FAQS: GuideFaq[] = [
  {
    id: "faq-1",
    question: "BGMがアップロードできません。",
    answer: "BGMは1分あたり2MB、かつ最大4.5MBまでです。圧縮してから再アップロードしてください。",
    category: "MATERIAL",
    categoryLabel: "素材・アップロード",
    sectionKey: "BGM",
    sourceUrl: "https://docs.studio.uzu-app.com/qanda/qanda.md#bgm-se",
    checkedAt: "2026-11-10",
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
  },
  {
    id: "faq-2",
    question: "動作確認で最初のフェーズのBGMが再生されません。",
    answer: "Chromeの自動再生制限が原因です。音声を許可するか、Safariなど別ブラウザで確認してください。",
    category: "CHECK",
    categoryLabel: "動作確認",
    sectionKey: "DEMO",
    sourceUrl: "https://docs.studio.uzu-app.com/qanda/qanda.md#others",
    checkedAt: "2026-11-10",
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
  },
  {
    id: "faq-3",
    question: "v2からv1へ戻せますか？",
    answer: "v2としてリリースした作品はv1へ戻せません。他のv1作品の編集は続けられます。",
    category: "MIGRATION",
    categoryLabel: "v1→v2移行",
    sectionKey: "MIGRATION",
    sourceUrl: "https://docs.studio.uzu-app.com/qanda/qanda-v1-v2.md",
    checkedAt: "2026-11-10",
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
  },
];

let nextItemOrder = 1;
let nextPlaytestOrder = 1;

/**
 * テスト用の作品を生成します。
 *
 * @param overrides 上書きする項目
 * @returns テスト用の作品
 */
export function makeProject(overrides: Partial<Project> = {}): Project {
  return {
    id: "11111111-1111-4111-8111-111111111111",
    title: "吸血鬼の館のマーダーミステリー",
    furigana: "きゅうけつきのやかたのマーダーミステリー",
    genre: "マーダーミステリー",
    playerCount: 4,
    estimatedMinutes: 90,
    releaseType: "PAID",
    stage: "IN_PROGRESS",
    scoringRule: "TIE_ALLOWED",
    studioWorkId: null,
    studioUrl: "https://studio.uzu-app.com/works/1",
    publicUrl: null,
    memo: null,
    archived: false,
    checklistCompleted: 1,
    checklistTotal: 2,
    playtestTotal: 0,
    playtestUzu: 0,
    playtestQuestionnaire: 0,
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
    ...overrides,
  };
}

/**
 * テスト用のテストプレイ記録を生成します。
 *
 * @param projectId 作品ID
 * @param playedAt 実施日
 * @param platform 実施場所
 * @param overrides 上書きする項目
 * @returns テスト用のテストプレイ記録
 */
export function makePlaytest(
  projectId: string,
  playedAt: string,
  platform: PlaytestPlatform = "UZU",
  overrides: Partial<PlaytestRecord> = {},
): PlaytestRecord {
  return {
    id: `playtest-${nextPlaytestOrder++}`,
    projectId,
    playedAt,
    platform,
    participantCount: 4,
    versionNote: "v1.0",
    questionnaireCollected: false,
    questionnaireUrl: null,
    findings: null,
    memo: null,
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
    ...overrides,
  };
}

/**
 * テスト用のチェック項目を生成します。
 *
 * @param projectId 作品ID
 * @param name 項目名
 * @param status チェック状態
 * @param overrides 上書きする項目
 * @returns テスト用のチェック項目
 */
export function makeChecklistItem(
  projectId: string,
  name: string,
  status: ChecklistStatus = "NOT_STARTED",
  overrides: Partial<ChecklistItem> = {},
): ChecklistItem {
  return {
    id: `check-${nextItemOrder}`,
    projectId,
    sectionKey: "SUMMARY",
    name,
    description: null,
    status,
    memo: null,
    dueDate: null,
    displayOrder: nextItemOrder++,
    createdAt: BASE_TIME,
    updatedAt: BASE_TIME,
    ...overrides,
  };
}

/**
 * PlaywrightのリクエストからJSON本文を取り出します。
 *
 * @param route 対象のルート
 * @returns 解析済みの本文。解析できない場合はundefined
 */
function parseBody(route: Route): unknown {
  const data = route.request().postData();
  if (!data) return undefined;
  try {
    return JSON.parse(data);
  } catch {
    return undefined;
  }
}

/**
 * JSONレスポンスを生成します。
 *
 * @param body レスポンス本文
 * @param status HTTPステータス
 * @returns Playwrightのfulfillに渡すレスポンス
 */
function json(body: unknown, status = 200) {
  return {
    status,
    contentType: "application/json",
    body: JSON.stringify(body),
  };
}

/**
 * 作品APIのモックをページへ登録します。
 *
 * @param page 対象のページ
 * @param seed モックの初期データ
 * @returns モックの状態と記録済みリクエスト
 */
export async function installApiMock(page: Page, seed: ApiMockSeed): Promise<ApiMockState> {
  let nextProject = 1;
  const state: ApiMockState = {
    projects: [...seed.projects],
    checklists: { ...(seed.checklists ?? {}) },
    playtests: { ...(seed.playtests ?? {}) },
    sections: { ...(seed.sections ?? {}) },
    guideFaqs: [...(seed.guideFaqs ?? DEFAULT_GUIDE_FAQS)],
    requests: [],
  };

  const syncPlaytestCounts = (projectId: string) => {
    const project = state.projects.find((entry) => entry.id === projectId);
    if (!project) return;
    const records = state.playtests[projectId] ?? [];
    project.playtestTotal = records.length;
    project.playtestUzu = records.filter((record) => record.platform === "UZU").length;
    project.playtestQuestionnaire = records.filter(
      (record) => record.questionnaireCollected,
    ).length;
  };

  Object.keys(state.playtests).forEach(syncPlaytestCounts);

  await page.route("**/api/v1/**", async (route) => {
    const request = route.request();
    const method = request.method();
    const path = new URL(request.url()).pathname.replace(API_PREFIX, "");
    const body = parseBody(route);
    state.requests.push({ method, path, body });

    const projectsMatch = path === "/projects";
    const projectMatch = path.match(/^\/projects\/([^/]+)$/);
    const archiveMatch = path.match(/^\/projects\/([^/]+)\/archive$/);
    const checklistMatch = path.match(/^\/projects\/([^/]+)\/checklist$/);
    const checklistItemMatch = path.match(/^\/projects\/([^/]+)\/checklist\/([^/]+)$/);
    const playtestMatch = path.match(/^\/projects\/([^/]+)\/playtests$/);
    const playtestItemMatch = path.match(/^\/projects\/([^/]+)\/playtests\/([^/]+)$/);
    const sectionMatch = path.match(/^\/projects\/([^/]+)\/sections\/([^/]+)$/);
    const guideFaqsMatch = path === "/guide-faqs";

    if (guideFaqsMatch && method === "GET") {
      const url = new URL(request.url());
      const category = url.searchParams.get("category");
      const keyword = url.searchParams.get("keyword")?.trim().toLowerCase() ?? "";
      const faqs = state.guideFaqs.filter((faq) => {
        if (category && faq.category !== category && faq.categoryLabel !== category) return false;
        if (!keyword) return true;
        return [faq.question, faq.answer, faq.categoryLabel]
          .join(" ")
          .toLowerCase()
          .includes(keyword);
      });
      return route.fulfill(json(faqs));
    }

    if (projectsMatch && method === "GET") {
      return route.fulfill(json(state.projects));
    }

    if (projectsMatch && method === "POST") {
      const input = (body ?? {}) as Partial<Project>;
      const project = makeProject({
        id: `project-${nextProject++}`,
        title: typeof input.title === "string" ? input.title : "無題の作品",
        releaseType: (input.releaseType ?? "FREE") as ReleaseType,
        stage: (input.stage ?? "PLANNING") as Stage,
        furigana: input.furigana ?? null,
        genre: input.genre ?? null,
        playerCount: input.playerCount ?? null,
        estimatedMinutes: input.estimatedMinutes ?? null,
        scoringRule: input.scoringRule ?? "TIE_ALLOWED",
        studioUrl: input.studioUrl ?? null,
        memo: input.memo ?? null,
        checklistCompleted: 0,
        checklistTotal: 0,
      });
      state.projects.unshift(project);
      state.checklists[project.id] = [];
      return route.fulfill(json(project, 201));
    }

    if (projectMatch) {
      const projectId = projectMatch[1];
      const project = state.projects.find((entry) => entry.id === projectId);
      if (!project) {
        return route.fulfill(json({ error: { message: "作品が見つかりません。" } }, 404));
      }
      if (method === "GET") {
        return route.fulfill(json(project));
      }
      if (method === "PATCH") {
        Object.assign(project, body ?? {}, { updatedAt: new Date().toISOString() });
        return route.fulfill(json(project));
      }
    }

    if (archiveMatch && method === "POST") {
      const project = state.projects.find((entry) => entry.id === archiveMatch[1]);
      if (!project) {
        return route.fulfill(json({ error: { message: "作品が見つかりません。" } }, 404));
      }
      project.archived = true;
      return route.fulfill(json(project));
    }

    if (checklistMatch && method === "GET") {
      return route.fulfill(json(state.checklists[checklistMatch[1]] ?? []));
    }

    if (checklistMatch && method === "POST") {
      const projectId = checklistMatch[1];
      const input = (body ?? {}) as {
        name?: string;
        description?: string;
        displayOrder?: number;
        sectionKey?: string;
      };
      const item = makeChecklistItem(
        projectId,
        input.name ?? "新しいチェック項目",
        "NOT_STARTED",
        {
          description: input.description ?? null,
          displayOrder: input.displayOrder,
          sectionKey: input.sectionKey ?? "OTHER",
        },
      );
      state.checklists[projectId] = [...(state.checklists[projectId] ?? []), item];
      return route.fulfill(json(item, 201));
    }

    if (checklistItemMatch) {
      const [, projectId, itemId] = checklistItemMatch;
      const items = state.checklists[projectId] ?? [];
      const item = items.find((entry) => entry.id === itemId);
      if (!item) {
        return route.fulfill(json({ error: { message: "チェック項目が見つかりません。" } }, 404));
      }
      if (method === "PATCH") {
        Object.assign(item, body ?? {}, { updatedAt: new Date().toISOString() });
      }
      return route.fulfill(json(item));
    }

    if (playtestMatch && method === "GET") {
      return route.fulfill(json(state.playtests[playtestMatch[1]] ?? []));
    }

    if (playtestMatch && method === "POST") {
      const projectId = playtestMatch[1];
      const input = (body ?? {}) as Partial<PlaytestRecord>;
      const now = new Date().toISOString();
      const record = makePlaytest(
        projectId,
        input.playedAt ?? "2026-11-01",
        input.platform ?? "UZU",
        {
          participantCount: input.participantCount ?? null,
          versionNote: input.versionNote ?? null,
          questionnaireCollected: input.questionnaireCollected ?? false,
          questionnaireUrl: input.questionnaireUrl || null,
          findings: input.findings ?? null,
          memo: input.memo ?? null,
          createdAt: now,
          updatedAt: now,
        },
      );
      state.playtests[projectId] = [...(state.playtests[projectId] ?? []), record];
      syncPlaytestCounts(projectId);
      return route.fulfill(json(record, 201));
    }

    if (playtestItemMatch) {
      const [, projectId, playtestId] = playtestItemMatch;
      const records = state.playtests[projectId] ?? [];
      const record = records.find((entry) => entry.id === playtestId);
      if (!record) {
        return route.fulfill(json({ error: { message: "テストプレイ記録が見つかりません。" } }, 404));
      }
      if (method === "PATCH") {
        Object.assign(record, body ?? {}, { updatedAt: new Date().toISOString() });
        syncPlaytestCounts(projectId);
        return route.fulfill(json(record));
      }
      if (method === "DELETE") {
        state.playtests[projectId] = records.filter((entry) => entry.id !== playtestId);
        syncPlaytestCounts(projectId);
        return route.fulfill({ status: 204 });
      }
    }

    if (sectionMatch) {
      const [, projectId, sectionKey] = sectionMatch;
      const key = `${projectId}:${sectionKey}`;
      const existing = state.sections[key];
      if (method === "GET") {
        if (!existing) {
          return route.fulfill(json({ error: { message: "セクションメモが見つかりません。" } }, 404));
        }
        return route.fulfill(json(existing));
      }
      if (method === "PATCH") {
        const input = (body ?? {}) as { memo?: string | null; verifiedAt?: string | null };
        const now = new Date().toISOString();
        const saved: ProjectSectionState = existing
          ? {
              ...existing,
              memo: input.memo ?? null,
              verifiedAt: input.verifiedAt ?? null,
              updatedAt: now,
            }
          : {
              id: `section-${projectId}-${sectionKey}`,
              projectId,
              sectionKey,
              memo: input.memo ?? null,
              verifiedAt: input.verifiedAt ?? null,
              createdAt: now,
              updatedAt: now,
            };
        state.sections[key] = saved;
        return route.fulfill(json(saved));
      }
    }

    return route.fulfill(json({ error: { message: `未定義のAPI: ${method} ${path}` } }, 404));
  });

  return state;
}
