import { expect, test } from "@playwright/test";
import { installApiMock, makeChecklistItem, makeProject } from "./fixtures";

test.describe("コンパクトモード画面", () => {
  test("次にやることの完了・UZU画面情報の表示・短いメモの保存ができる", async ({ page }) => {
    // コンパクト画面で主要3操作が1画面に収まっていることを確認する。
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

    await test.step("コンパクトモードを開き、作品名と次にやることを確認する", async () => {
      await page.goto("/#/compact");

      await expect(page.locator(".compact-header")).toContainText(
        "吸血鬼の館のマーダーミステリー",
      );
      await expect(page.locator(".compact-action-card")).toContainText("作品概要の確認");
    });

    await test.step("次にやることを完了にする", async () => {
      await page.getByRole("button", { name: "完了にする" }).click();
      await expect(page.getByText("完了: 作品概要の確認")).toBeVisible();
    });

    await test.step("UZU画面の情報を表示する", async () => {
      await page.getByRole("button", { name: "UZU画面" }).click();
      await expect(page.getByRole("heading", { name: "作品概要", level: 2 })).toBeVisible();
    });

    await test.step("短いメモを保存する", async () => {
      await page.getByRole("button", { name: "メモ" }).click();
      await page.getByPlaceholder("気づきを短く残す").fill("コンパクトメモ");
      await page.getByRole("button", { name: "保存" }).click();
      await expect(page.getByText("メモを保存しました。").first()).toBeVisible();
    });
  });
});
