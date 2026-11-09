import { expect, test } from "@playwright/test";
import { installApiMock } from "./fixtures";

test.describe("作品の新規作成画面", () => {
  test("入力したタイトル・ふりがな・公開形態を送信し、作成した作品のワークスペースへ遷移する", async ({ page }) => {
    // 作成APIのリクエスト本文と遷移先を併せて確認する。
    const mock = await installApiMock(page, { projects: [] });

    await test.step("新規作成フォームへ入力する", async () => {
      await page.goto("/#/projects/new");
      await page.getByLabel(/タイトル/).fill("新作ミステリー");
      await page.getByLabel("ふりがな").fill("しんさくみすてりー");
      await page.getByText("有料公開", { exact: true }).click();
    });

    await test.step("送信してワークスペースへ遷移する", async () => {
      await page.getByRole("button", { name: "作品を作成" }).click();

      await expect(page).toHaveURL(/#\/projects\/project-1$/);
      await expect(
        page.getByRole("heading", { name: "新作ミステリー", level: 1 }),
      ).toBeVisible();
    });

    await test.step("送信内容が作成APIへ渡ったことを確認する", async () => {
      const createRequest = mock.requests.find(
        (request) => request.method === "POST" && request.path === "/projects",
      );
      expect(createRequest?.body).toMatchObject({
        title: "新作ミステリー",
        furigana: "しんさくみすてりー",
        releaseType: "PAID",
      });
    });
  });
});
