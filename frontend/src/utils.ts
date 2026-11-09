import { SECTIONS } from "./data/uzu";
import type { ChecklistItem, Project, SectionDefinition } from "./types";

export function formatDate(value: string | null | undefined): string {
  if (!value) return "未設定";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("ja-JP", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export function formatDay(value: string | null | undefined): string {
  if (!value) return "期限なし";
  const date = new Date(`${value}T00:00:00+09:00`);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("ja-JP", {
    month: "short",
    day: "numeric",
  }).format(date);
}

export function progressPercent(project: Project): number {
  if (project.checklistTotal <= 0) return 0;
  return Math.round((project.checklistCompleted / project.checklistTotal) * 100);
}

export function nextChecklistItem(items: ChecklistItem[]): ChecklistItem | undefined {
  return [...items]
    .filter((item) => item.status !== "DONE")
    .sort((a, b) => a.displayOrder - b.displayOrder)[0];
}

const SECTION_RULES: Array<{ key: string; pattern: RegExp }> = [
  { key: "SUMMARY", pattern: /作品概要|あらすじ|キービジュアル|タイトル|注意事項|作者コメント/ },
  { key: "CHARACTERS", pattern: /キャラクター|ミッション|NPC|PC|種別/ },
  { key: "PHASES", pattern: /フェーズ|投票|密談|ラウンド/ },
  { key: "FLOW", pattern: /進行|分岐|エンディング/ },
  { key: "TEXTS", pattern: /テキスト|誤字|表記|句読点/ },
  { key: "CLUES", pattern: /手がかり|証拠|カード/ },
  { key: "DECK", pattern: /山札|デッキ/ },
  { key: "ROOMS", pattern: /ルーム|部屋|移動/ },
  { key: "TOKENS", pattern: /トークン|コマ/ },
  { key: "TEXT_FIELDS", pattern: /自由記述|入力欄/ },
  { key: "PRODUCTION", pattern: /BGM|SE|演出|音量|効果音/ },
  { key: "CONDITIONS", pattern: /条件|AND|OR/ },
  { key: "SCREEN_DESIGN", pattern: /画面デザイン|レイアウト|配色/ },
  { key: "DEMO", pattern: /動作確認|通し|デモ/ },
  { key: "VERSION", pattern: /バージョン|最新版/ },
  { key: "PLAYTEST", pattern: /テストプレイ|アンケート|フィードバック/ },
  { key: "REVIEW", pattern: /審査|提出|指摘|公開準備/ },
  { key: "LOCALIZATION", pattern: /外国語|翻訳|ローカライズ/ },
  { key: "REFLECTION", pattern: /感想戦|真相|解説|推理/ },
  { key: "RELEASE", pattern: /リリース|公開日|料金|セール/ },
  { key: "MARKETING", pattern: /マーケティング|告知|タグ/ },
  { key: "ASSETS", pattern: /素材|画像|音声|動画|権利/ },
  { key: "MEMBERS", pattern: /メンバー|共同制作|担当/ },
  { key: "REPORT", pattern: /レポート|売上|分析/ },
];

const SECTION_KEY_BY_LABEL = new Map(SECTIONS.map((section) => [section.label, section.key]));

export function sectionKeyForItem(item: ChecklistItem): string {
  const prefix = item.name.match(/^【([^】]+)】/);
  if (prefix) {
    const key = SECTION_KEY_BY_LABEL.get(prefix[1]);
    if (key) return key;
  }
  const text = `${item.name} ${item.description ?? ""}`;
  const matched = SECTION_RULES.find((rule) => rule.pattern.test(text));
  return matched?.key ?? "OTHER";
}

export function groupItemsBySection(items: ChecklistItem[]): Record<string, ChecklistItem[]> {
  return items.reduce<Record<string, ChecklistItem[]>>((groups, item) => {
    const key = sectionKeyForItem(item);
    groups[key] = [...(groups[key] ?? []), item];
    return groups;
  }, {});
}

export function sectionProgress(items: ChecklistItem[]): { done: number; total: number } {
  return {
    done: items.filter((item) => item.status === "DONE").length,
    total: items.length,
  };
}

export function findSection(
  sections: SectionDefinition[],
  key: string,
): SectionDefinition | undefined {
  return sections.find((section) => section.key === key);
}

export function officialUrl(path: string): string {
  return `https://docs.studio.uzu-app.com/${path}`;
}

export function copyText(text: string): Promise<void> {
  if (navigator.clipboard?.writeText) {
    return navigator.clipboard.writeText(text);
  }
  return Promise.reject(new Error("クリップボードを利用できません"));
}

export function readStorage<T>(key: string, fallback: T): T {
  try {
    const value = window.localStorage.getItem(key);
    return value ? (JSON.parse(value) as T) : fallback;
  } catch {
    return fallback;
  }
}

export function writeStorage(key: string, value: unknown): void {
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
  } catch {
    return;
  }
}
