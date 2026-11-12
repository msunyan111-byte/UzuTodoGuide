import { expect, test } from "@playwright/test";
import { installApiMock } from "./fixtures";

test.describe("UZU機能ガイドのQ&A", () => {
  test("Q&Aを検索・カテゴリ絞り込みし、回答と関連画面を確認できる", async ({ page }) => {
    const state = await installApiMock(page, { projects: [] });

    await page.goto("/#/guide");

    await test.step("Q&Aが機能一覧より前に表示される", async () => {
      await expect(page.getByRole("heading", { name: "つまずいたときの原因と、次の操作。" })).toBeVisible();
      await expect(page.locator(".faq-item")).toHaveCount(3);
      await expect(page.locator(".guide-faq-count")).toHaveText("3件");
      const faqBox = await page.locator(".guide-faq-section").boundingBox();
      const filterBox = await page.locator(".guide-filter").boundingBox();
      expect(faqBox?.y).toBeLessThan(filterBox?.y ?? 0);
    });

    await test.step("キーワードでQ&Aを検索する", async () => {
      await page.getByPlaceholder("質問・回答・カテゴリで検索").fill("BGM");
      await expect(page.locator(".faq-item")).toHaveCount(2);
      await expect(page.locator(".guide-faq-section .list-summary")).toContainText("2件のQ&A");
    });

    await test.step("カテゴリでQ&Aを絞り込む", async () => {
      await page.getByPlaceholder("質問・回答・カテゴリで検索").fill("");
      await page.locator(".group-chip", { hasText: "動作確認" }).click();
      await expect(page.locator(".faq-item")).toHaveCount(1);
      await expect(page.locator(".faq-question")).toContainText("最初のフェーズのBGM");
    });

    await test.step("回答を開き、関連するガイド画面を表示する", async () => {
      await page.locator(".group-chip", { hasText: "素材・アップロード" }).click();
      await page.locator(".faq-item", { hasText: "BGMがアップロードできません" }).click();
      await expect(page.locator(".faq-answer")).toContainText("1分あたり2MB");
      await expect(page.locator(".faq-checked")).toHaveText("確認日 2026-11-10");

      await page.getByRole("button", { name: "関連する画面を見る" }).click();
      await expect(page.getByRole("dialog", { name: "BGM" })).toBeVisible();
      await expect(page.getByRole("dialog", { name: "BGM" })).toContainText("フェーズや場面ごとのBGM");
    });

    expect(state.requests.some((request) => request.path === "/guide-faqs")).toBe(true);
  });

  test("Q&A APIに接続できない場合は再読み込みを案内する", async ({ page }) => {
    await page.route("**/api/v1/guide-faqs**", (route) => route.abort());

    await page.goto("/#/guide");

    await expect(page.getByRole("heading", { name: "Q&Aを読み込めませんでした" })).toBeVisible();
    await expect(page.getByRole("button", { name: "再読み込み" })).toBeVisible();
    await expect(page.locator(".faq-item")).toHaveCount(0);
  });
});
