import { expect, test } from "@playwright/test";
import { installApiMock, makeChecklistItem, makeProject } from "./fixtures";

test.describe("作品ワークスペース画面", () => {
  test("チェック状態の変更・項目メモの保存・チェック項目の追加ができる", async ({ page }) => {
    // 未着手と完了の項目を用意し、行ごとの操作と追加操作を確認する。
    const project = makeProject({ id: "p1" });
    await installApiMock(page, {
      projects: [project],
      checklists: {
        p1: [
          makeChecklistItem("p1", "作品概要の確認", "NOT_STARTED", {
            id: "item-1",
            displayOrder: 1,
          }),
          makeChecklistItem("p1", "キービジュアルの確認", "DONE", {
            id: "item-2",
            displayOrder: 2,
          }),
        ],
      },
    });

    const row = page.locator(".check-row", { hasText: "作品概要の確認" });

    await test.step("ワークスペースを開き、作品情報とセクション見出しを確認する", async () => {
      await page.goto("/#/projects/p1");

      await expect(
        page.getByRole("heading", { name: "吸血鬼の館のマーダーミステリー", level: 1 }),
      ).toBeVisible();
      await expect(
        page.locator(".workspace-header").getByText("制作中", { exact: true }),
      ).toBeVisible();
      await expect(
        page.locator(".workspace-header").getByText("有料", { exact: true }),
      ).toBeVisible();
      await expect(page.getByRole("heading", { name: "作品概要", level: 2 })).toBeVisible();
      await expect(page.getByRole("heading", { name: "制作サポート", level: 2 })).toBeVisible();
    });

    await test.step("チェック項目を完了にする", async () => {
      await row.getByRole("button", { name: "完了にする" }).click();
      await expect(row.getByRole("button", { name: "未着手に戻す" })).toBeVisible();
      await expect(page.getByText("完了: 作品概要の確認")).toBeVisible();
    });

    await test.step("項目メモを入力して保存する", async () => {
      await row.getByRole("button", { name: "メモを追加" }).click();
      const memo = row.getByPlaceholder("この項目のメモ");
      await memo.fill("UZU上で確認済み");
      await memo.press("Tab");
      await expect(page.getByText("メモを保存しました。")).toBeVisible();
    });

    await test.step("セクションにチェック項目を追加する", async () => {
      await page.getByRole("button", { name: "この画面にチェック項目を追加" }).click();
      await page.getByPlaceholder("チェック項目名").fill("作品概要の追記確認");
      await page.getByRole("button", { name: "追加する" }).click();
      await expect(
        page.locator(".check-row", { hasText: "作品概要の追記確認" }),
      ).toBeVisible();
    });
  });

  test("セクションに応じた制作ヒントと外部の参考リンクを確認できる", async ({ page }) => {
    await installApiMock(page, {
      projects: [makeProject({ id: "p1" })],
      checklists: { p1: [] },
    });

    await test.step("キャラクター画面で関係性づくりのヒントを確認する", async () => {
      await page.goto("/#/projects/p1?section=CHARACTERS");

      await expect(
        page.getByRole("heading", { name: "名前より先に、関係性と秘密を決める", level: 3 }),
      ).toBeVisible();
      await expect(page.getByText("3ステップで進める")).toBeVisible();
      await expect(page.getByText("この人物は、誰に知られたくない何を抱えている？")).toBeVisible();
    });

    await test.step("キャラクターアイコン素材とレクチャーへ移動できる", async () => {
      const iconResource = page.locator(".support-resource", {
        hasText: "キャラクターアイコン素材",
      });
      await expect(iconResource).toBeVisible();
      await expect(iconResource).toHaveAttribute(
        "href",
        "https://docs.studio.uzu-app.com/resource/character-icon",
      );
      await expect(iconResource).toHaveAttribute("target", "_blank");

      const lecture = page.locator(".support-resource", {
        hasText: "一晩で完成させるマダミス制作講座",
      });
      await expect(lecture).toBeVisible();
      await expect(lecture).toHaveAttribute("data-kind", "レクチャー");
    });

    await test.step("フェーズ画面では感情のリズムを整えるヒントに切り替わる", async () => {
      await page.goto("/#/projects/p1?section=PHASES");

      await expect(
        page.getByRole("heading", { name: "フェーズは「感情のリズム」で並べる", level: 3 }),
      ).toBeVisible();
      await expect(page.getByText("このフェーズが終わったとき、誰の疑いが動く？")).toBeVisible();
    });
  });
});
