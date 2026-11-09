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
  return [
    {
      label: "テスト3回以上",
      detail: "UZU 1回を含む",
      state: stateOf("テストプレイ3回", true),
    },
    {
      label: "アンケート提出",
      detail: "有料作品の詳細審査",
      state: stateOf("アンケート", true),
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
