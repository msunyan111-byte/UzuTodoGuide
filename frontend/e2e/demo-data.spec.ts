import { expect, test } from "@playwright/test";
import { DEMO_CHECKLIST, DEMO_PROJECT, DEMO_PROJECT_ID } from "../src/data/demo";
import { SECTIONS } from "../src/data/uzu";
import { groupItemsBySection, sectionKeyForItem } from "../src/utils";

test.describe("デモTODOの画面割り当て", () => {
  test("54件すべてが既知のUZU画面名と完了条件を持つ", () => {
    const labels = new Set(SECTIONS.map((section) => section.label));

    expect(DEMO_CHECKLIST).toHaveLength(54);
    for (const item of DEMO_CHECKLIST) {
      const prefix = item.name.match(/^【([^】]+)】/)?.[1] ?? "";
      expect(prefix, item.name).not.toBe("");
      expect(labels.has(prefix), item.name).toBe(true);
      expect(sectionKeyForItem(item), item.name).not.toBe("OTHER");
      expect(item.description, item.name).toContain("完了条件:");
    }
  });

  test("画面名ごとの件数が意図どおりに集計される", () => {
    const groups = groupItemsBySection(DEMO_CHECKLIST);

    expect(groups.CREATE).toHaveLength(1);
    expect(groups.SUMMARY).toHaveLength(3);
    expect(groups.GENRE).toHaveLength(1);
    expect(groups.CHARACTERS).toHaveLength(5);
    expect(groups.NPC).toHaveLength(1);
    expect(groups.PHASES).toHaveLength(3);
    expect(groups.FLOW).toHaveLength(1);
    expect(groups.TEXTS).toHaveLength(5);
    expect(groups.CLUES).toHaveLength(4);
    expect(groups.DECK).toHaveLength(1);
    expect(groups.ROOMS).toHaveLength(1);
    expect(groups.TOKENS).toHaveLength(1);
    expect(groups.TEXT_FIELDS).toHaveLength(1);
    expect(groups.TRIGGERS).toHaveLength(1);
    expect(groups.ACTIONS).toHaveLength(1);
    expect(groups.BGM).toHaveLength(1);
    expect(groups.SE).toHaveLength(1);
    expect(groups.MOVIE).toHaveLength(1);
    expect(groups.BACKGROUND).toHaveLength(1);
    expect(groups.COLOR_THEME).toHaveLength(1);
    expect(groups.CONDITIONS).toHaveLength(3);
    expect(groups.SCREEN_DESIGN).toHaveLength(1);
    expect(groups.REFLECTION).toHaveLength(1);
    expect(groups.ASSETS).toHaveLength(1);
    expect(groups.DEMO).toHaveLength(3);
    expect(groups.VERSION).toHaveLength(1);
    expect(groups.PLAYTEST).toHaveLength(2);
    expect(groups.REVIEW).toHaveLength(2);
    expect(groups.RELEASE).toHaveLength(2);
    expect(groups.TERMS).toHaveLength(1);
    expect(groups.REVENUE).toHaveLength(1);
    expect(groups.MARKETING).toHaveLength(1);
    expect(groups.OTHER ?? []).toHaveLength(0);
  });

  test("API未接続時はデモTODOを画面ごとに表示する", async ({ page }) => {
    await page.route("**/api/v1/**", (route) => route.abort());

    await test.step("デモ作品を開き、全体進捗と次アクションを確認する", async () => {
      await page.goto(`/#/projects/${DEMO_PROJECT_ID}`);

      await expect(
        page.getByRole("heading", { name: DEMO_PROJECT.title, level: 1 }),
      ).toBeVisible();
      await expect(page.locator(".inline-banner")).toContainText("デモデータ");
      await expect(page.locator(".workspace-progress")).toContainText("10/54");
      await expect(page.locator(".workspace-progress-note")).toContainText(
        "次: 【NPC】NPCと表示方法を登録する",
      );
    });

    await test.step("キャラクター画面を開き、5件のTODOが表示される", async () => {
      await expect(page.locator(".section-group-toggle", { hasText: "キャラクター" })).toBeVisible();
      await page.locator(".section-link", { hasText: "キャラクター" }).first().click();

      await expect(page.getByRole("heading", { name: "キャラクター", level: 2 })).toBeVisible();
      await expect(page.locator(".check-row")).toHaveCount(5);
      await expect(page.locator(".check-row").first()).toContainText(
        "【キャラクター】人物の関係性と秘密の見せ場を整理する",
      );
    });

    await test.step("中項目のNPCを開き、1件のTODOが表示される", async () => {
      await page.locator(".section-link", { hasText: "NPC" }).click();

      await expect(page.getByRole("heading", { name: "NPC", level: 2 })).toBeVisible();
      await expect(page.locator(".check-row")).toHaveCount(1);
      await expect(page.locator(".check-row").first()).toContainText(
        "【NPC】NPCと表示方法を登録する",
      );
    });
  });
});
