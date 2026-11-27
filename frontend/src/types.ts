export type Stage =
  | "PLANNING"
  | "IN_PROGRESS"
  | "FUNCTIONAL_CHECK"
  | "PLAYTEST"
  | "REVISION"
  | "REVIEW_SUBMITTED"
  | "REVIEW_RESPONSE"
  | "RELEASE_PREPARATION"
  | "RELEASED";

export type ReleaseType = "FREE" | "PAID";
export type ScoringRule = "TIE_ALLOWED" | "SOLE_TOP_REQUIRED";
export type ChecklistStatus = "NOT_STARTED" | "IN_PROGRESS" | "DONE";
export type PlaytestPlatform = "UZU" | "OTHER";

export interface Project {
  id: string;
  title: string;
  furigana: string | null;
  genre: string | null;
  playerCount: number | null;
  estimatedMinutes: number | null;
  releaseType: ReleaseType;
  stage: Stage;
  scoringRule: ScoringRule;
  studioWorkId: string | null;
  studioUrl: string | null;
  publicUrl: string | null;
  memo: string | null;
  archived: boolean;
  checklistCompleted: number;
  checklistTotal: number;
  playtestTotal: number;
  playtestUzu: number;
  playtestQuestionnaire: number;
  createdAt: string;
  updatedAt: string;
}

export interface ChecklistItem {
  id: string;
  projectId: string;
  sectionKey: string;
  name: string;
  description: string | null;
  status: ChecklistStatus;
  memo: string | null;
  dueDate: string | null;
  displayOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface PlaytestRecord {
  id: string;
  projectId: string;
  playedAt: string;
  platform: PlaytestPlatform;
  participantCount: number | null;
  versionNote: string | null;
  questionnaireCollected: boolean;
  questionnaireUrl: string | null;
  findings: string | null;
  memo: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PlaytestRecordInput {
  playedAt: string;
  platform: PlaytestPlatform;
  participantCount?: number;
  versionNote?: string;
  questionnaireCollected?: boolean;
  questionnaireUrl?: string;
  findings?: string;
  memo?: string;
}

export interface ProjectSectionState {
  id: string;
  projectId: string;
  sectionKey: string;
  memo: string | null;
  verifiedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectSectionStateInput {
  memo: string | null;
  verifiedAt: string | null;
}

export interface ProjectCreateInput {
  title: string;
  furigana?: string;
  genre?: string;
  playerCount?: number;
  estimatedMinutes?: number;
  releaseType: ReleaseType;
  stage?: Stage;
  scoringRule?: ScoringRule;
  studioUrl?: string;
  memo?: string;
}

export type SectionGroup =
  | "アカウント"
  | "作品"
  | "エディタ"
  | "品質"
  | "感想戦"
  | "発展"
  | "共通";

export type EditorGroup =
  | "キャラクター"
  | "基本項目"
  | "特殊リソース"
  | "演出"
  | "条件";

export type NavGroup = EditorGroup | SectionGroup;

export interface SectionDefinition {
  key: string;
  label: string;
  group: SectionGroup;
  editorGroup?: EditorGroup;
  order: number;
  uzuPath: string;
  purpose: string;
  basic: "yes" | "conditional" | "no";
  advanced: "yes" | "conditional" | "no";
  fields: string[];
  constraints: string[];
  tips: string[];
  paidOnly?: boolean;
  confidence: "confirmed" | "estimated";
}

export type GuideFaqCategory =
  | "CHARACTER"
  | "PHASE"
  | "TEXT"
  | "MATERIAL"
  | "CHECK"
  | "RELEASE"
  | "MIGRATION"
  | "OTHER";

export interface GuideFaq {
  id: string;
  question: string;
  answer: string;
  category: GuideFaqCategory;
  categoryLabel: string;
  sectionKey: string | null;
  sourceUrl: string;
  checkedAt: string;
  createdAt: string;
  updatedAt: string;
}

export const STAGE_LABELS: Record<Stage, string> = {
  PLANNING: "企画",
  IN_PROGRESS: "制作中",
  FUNCTIONAL_CHECK: "動作確認",
  PLAYTEST: "テストプレイ",
  REVISION: "修正中",
  REVIEW_SUBMITTED: "審査提出済み",
  REVIEW_RESPONSE: "審査対応",
  RELEASE_PREPARATION: "公開準備",
  RELEASED: "公開済み",
};

export const RELEASE_LABELS: Record<ReleaseType, string> = {
  FREE: "無料",
  PAID: "有料",
};

export const SCORING_LABELS: Record<ScoringRule, string> = {
  TIE_ALLOWED: "同数最多を許可",
  SOLE_TOP_REQUIRED: "単独最多のみ",
};

export const PLAYTEST_PLATFORM_LABELS: Record<PlaytestPlatform, string> = {
  UZU: "UZU上",
  OTHER: "UZU外",
};

export const STATUS_LABELS: Record<ChecklistStatus, string> = {
  NOT_STARTED: "未着手",
  IN_PROGRESS: "対応中",
  DONE: "完了",
};
