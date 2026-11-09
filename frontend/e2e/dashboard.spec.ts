import { expect, test } from "@playwright/test";
import { installApiMock, makeChecklistItem, makeProject } from "./fixtures";

test.describe("ダッシュボード画面", () => {
  test("進行中の作品・続きから・統計カードを表示し、ワークスペースへ遷移する", async ({ page }) => {
    // 更新日時の異なる2作品を用意し、「続きから」が最新作品を選ぶことを確認する。
    const continueProject = makeProject({
      id: "p1",
      title: "吸血鬼の館のマーダーミステリー",
      updatedAt: "2026-11-04T10:30:00+09:00",
    });
    const planningProject = makeProject({
      id: "p2",
      title: "星降る夜の推理",
      releaseType: "FREE",
      stage: "PLANNING",
      updatedAt: "2026-11-03T10:30:00+09:00",
      checklistCompleted: 0,
      checklistTotal: 1,
    });
    await installApiMock(page, {
      projects: [planningProject, continueProject],
      checklists: {
        p1: [
          makeChecklistItem("p1", "作品概要の確認"),
          makeChecklistItem("p1", "キービジュアルの確認", "DONE"),
        ],
        p2: [makeChecklistItem("p2", "キャラクターの確認")],
      },
    });

    await test.step("ダッシュボードを開く", async () => {
      await page.goto("/#/");
    });

    await test.step("見出し・続きからカード・統計カードを確認する", async () => {
      await expect(
        page.getByRole("heading", { name: "制作の続きを、ここから。" }),
      ).toBeVisible();
      await expect(page.locator(".hero-card")).toContainText(
        "吸血鬼の館のマーダーミステリー",
      );
      await expect(page.locator(".hero-card")).toContainText("作品概要の確認");
      await expect(page.locator(".stat-card", { hasText: "進行中の作品" })).toContainText("2");
      await expect(page.locator(".stat-card", { hasText: "完了チェック" })).toContainText("1");
    });

    await test.step("続きからカードからワークスペースへ遷移する", async () => {
      await page.locator(".hero-card").getByRole("link", { name: "ワークスペースを開く" }).click();

      await expect(page).toHaveURL(/#\/projects\/p1/);
      await expect(
        page.getByRole("heading", { name: "吸血鬼の館のマーダーミステリー", level: 1 }),
      ).toBeVisible();
    });
  });
});
