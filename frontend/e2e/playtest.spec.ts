import { expect, test } from "@playwright/test";
import { installApiMock, makePlaytest, makeProject } from "./fixtures";

test.describe("テストプレイ記録", () => {
  test("記録の追加・更新・削除ができ、有料公開の条件判定に反映される", async ({ page }) => {
    const project = makeProject({ id: "p1", releaseType: "PAID" });
    const state = await installApiMock(page, {
      projects: [project],
      checklists: { p1: [] },
      playtests: {
        p1: [
          makePlaytest("p1", "2026-11-01", "UZU", {
            id: "playtest-old-uzu",
            questionnaireCollected: true,
            questionnaireUrl: "https://example.com/form",
            findings: "導入の説明が長い",
            versionNote: "v1.0",
          }),
          makePlaytest("p1", "2026-11-02", "OTHER", {
            id: "playtest-old-other",
            questionnaireCollected: false,
            findings: "手がかりの公開順を確認する",
            versionNote: "v1.1",
          }),
        ],
      },
    });

    await test.step("テストプレイセクションに既存記録と条件未達を表示する", async () => {
      await page.goto("/#/projects/p1?section=PLAYTEST");

      await expect(page.getByRole("heading", { name: "テストプレイ記録", level: 2 })).toBeVisible();
      await expect(page.locator(".playtest-summary")).toContainText("2回");
      await expect(page.locator(".playtest-summary")).toContainText("1回");
      await expect(page.getByText("有料公開の条件に未達です")).toBeVisible();
      await expect(
        page.locator(".readiness-list li", { hasText: "テスト3回以上" }),
      ).toContainText("合計2回 / UZU 1回");
    });

    await test.step("UZU上の記録を追加して条件を満たす", async () => {
      await page.getByRole("button", { name: "テストプレイ記録を追加" }).click();
      await page.getByLabel("実施日").fill("2026-11-05");
      await page.getByLabel("実施場所").selectOption("UZU");
      await page.getByLabel("参加人数").fill("5");
      await page.getByLabel("対象バージョン").fill("v1.2");
      await page.getByLabel("アンケートを回収済み").check();
      await page.getByLabel("気づき・改善点").fill("投票前の会話が増えると分かりやすい");
      await page.getByRole("button", { name: "追加する" }).click();

      await expect(page.getByText("テストプレイ記録を追加しました。")).toBeVisible();
      await expect(page.getByText("有料公開の記録条件を満たしています")).toBeVisible();
      await expect.poll(() => state.playtests.p1).toHaveLength(3);
      await expect.poll(() => state.projects[0].playtestTotal).toBe(3);
      await expect.poll(() => state.projects[0].playtestQuestionnaire).toBe(2);
    });

    await test.step("記録を更新する", async () => {
      const record = page.locator(".playtest-record", { hasText: "11月2日" });
      await record.getByRole("button", { name: "編集" }).click();
      await page.getByLabel("気づき・改善点").fill("公開順を修正済み");
      await page.getByRole("button", { name: "更新する" }).click();

      await expect(page.getByText("テストプレイ記録を更新しました。")).toBeVisible();
      await expect(
        page.locator(".playtest-record", { hasText: "11月2日" }),
      ).toContainText("公開順を修正済み");
    });

    await test.step("確認後に記録を削除する", async () => {
      page.once("dialog", (dialog) => void dialog.accept());
      const record = page.locator(".playtest-record", { hasText: "11月2日" });
      await record.getByRole("button", { name: "削除" }).click();

      await expect(page.getByText("テストプレイ記録を削除しました。")).toBeVisible();
      await expect(page.locator(".playtest-record")).toHaveCount(2);
      await expect(page.getByText("有料公開の条件に未達です")).toBeVisible();
      await expect.poll(() => state.playtests.p1).toHaveLength(2);
    });

    await test.step("再読み込み後も記録が復元される", async () => {
      await page.reload();
      await expect(page.locator(".playtest-record")).toHaveCount(2);
      await expect(page.locator(".playtest-record", { hasText: "11月5日" })).toContainText(
        "投票前の会話が増えると分かりやすい",
      );
    });
  });
});
