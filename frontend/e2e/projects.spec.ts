import { expect, test } from "@playwright/test";
import { installApiMock, makeChecklistItem, makeProject } from "./fixtures";

test.describe("作品一覧画面", () => {
  test("タイトル検索・公開形態・アーカイブ表示の3条件で作品を絞り込める", async ({ page }) => {
    // 公開中2件とアーカイブ済み1件を用意し、絞り込みの組み合わせを確認する。
    const paidProject = makeProject({
      id: "p1",
      title: "吸血鬼の館",
      updatedAt: "2026-11-04T10:30:00+09:00",
    });
    const freeProject = makeProject({
      id: "p2",
      title: "星降る夜の推理",
      releaseType: "FREE",
      stage: "PLANNING",
      updatedAt: "2026-11-03T10:30:00+09:00",
    });
    const archivedProject = makeProject({
      id: "p3",
      title: "過去の試作",
      releaseType: "FREE",
      stage: "RELEASED",
      archived: true,
      updatedAt: "2026-10-01T10:30:00+09:00",
    });
    await installApiMock(page, {
      projects: [paidProject, freeProject, archivedProject],
      checklists: {
        p1: [makeChecklistItem("p1", "作品概要の確認")],
        p2: [makeChecklistItem("p2", "キャラクターの確認")],
        p3: [makeChecklistItem("p3", "リリース情報の確認")],
      },
    });

    const cards = page.locator(".project-card");
    const search = page.getByPlaceholder("タイトル・ふりがな・ジャンルで検索");

    await test.step("作品一覧を開き、アーカイブ以外の2件を確認する", async () => {
      await page.goto("/#/projects");
      await expect(page.getByRole("heading", { name: "作品一覧" })).toBeVisible();
      await expect(cards).toHaveCount(2);
    });

    await test.step("タイトルの一部で検索する", async () => {
      await search.fill("星降る");
      await expect(cards).toHaveCount(1);
      await expect(page.getByRole("heading", { name: "星降る夜の推理" })).toBeVisible();
    });

    await test.step("公開形態を有料で絞り込む", async () => {
      await search.fill("");
      await page.getByLabel("公開形態").selectOption("PAID");
      await expect(cards).toHaveCount(1);
      await expect(page.getByRole("heading", { name: "吸血鬼の館" })).toBeVisible();
    });

    await test.step("アーカイブ済み作品も表示する", async () => {
      await page.getByLabel("公開形態").selectOption("ALL");
      await page.getByLabel("アーカイブを表示").check({ force: true });
      await expect(cards).toHaveCount(3);
      await expect(page.getByRole("heading", { name: "過去の試作" })).toBeVisible();
    });
  });
});
