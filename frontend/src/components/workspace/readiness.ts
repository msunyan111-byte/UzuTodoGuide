import type { ChecklistItem, Project } from "../../types";

export interface Readiness {
  label: string;
  detail: string;
  state: "done" | "pending" | "na";
}

export function readinessFor(items: ChecklistItem[], project: Project): Readiness[] {
  const findItem = (keyword: string) => items.find((item) => item.name.includes(keyword));
  const stateOfItem = (item: ChecklistItem | undefined): "done" | "pending" =>
    item?.status === "DONE" ? "done" : "pending";
  const stateOf = (keyword: string, paidOnly = false): "done" | "pending" | "na" => {
    if (paidOnly && project.releaseType !== "PAID") return "na";
    return stateOfItem(findItem(keyword));
  };
  const playtestReady = project.playtestTotal >= 3 && project.playtestUzu >= 1;
  const playtestDetail = `合計${project.playtestTotal}回 / UZU ${project.playtestUzu}回`;
  const questionnaireDetail = `回収済み ${project.playtestQuestionnaire}件`;
  return [
    {
      label: "テスト3回以上",
      detail: project.releaseType === "PAID" ? playtestDetail : "UZU 1回を含む",
      state:
        project.releaseType === "PAID" ? (playtestReady ? "done" : "pending") : "na",
    },
    {
      label: "アンケート提出",
      detail:
        project.releaseType === "PAID" ? questionnaireDetail : "有料作品の詳細審査",
      state:
        project.releaseType === "PAID"
          ? project.playtestQuestionnaire > 0
            ? "done"
            : "pending"
          : "na",
    },
    {
      label: "審査・公開",
      detail: "提出と指摘対応",
      state: stateOf("審査"),
    },
    {
      label: "素材・容量",
      detail: "画像・音声・動画",
      state: stateOfItem(findItem("キービジュアル") ?? findItem("BGM")),
    },
    {
      label: "最新バージョン",
      detail: "テストプレイ用・公開用",
      state: stateOf("最新バージョン"),
    },
  ];
}
