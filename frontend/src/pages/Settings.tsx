import { useState, type ChangeEvent } from "react";
import { api } from "../api";
import { Icon } from "../components/Icon";
import { Modal } from "../components/Modal";
import { useToast } from "../components/Toast";
import { formatDate, readStorage, writeStorage } from "../utils";

export function Settings() {
  const toast = useToast();
  const [busy, setBusy] = useState(false);
  const [restorePayload, setRestorePayload] = useState<Record<string, unknown> | null>(null);
  const [restoreName, setRestoreName] = useState("");
  const [confirmRestore, setConfirmRestore] = useState(false);
  const [lastBackup, setLastBackup] = useState<string | null>(
    readStorage<string | null>("uzu:last-backup", null),
  );

  const download = async () => {
    setBusy(true);
    try {
      const payload = await api.downloadBackup();
      const blob = new Blob([JSON.stringify(payload, null, 2)], { type: "application/json" });
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      const stamp = new Date().toISOString().slice(0, 10);
      anchor.href = url;
      anchor.download = `uzu-todo-guide-backup-${stamp}.json`;
      anchor.click();
      URL.revokeObjectURL(url);
      const now = new Date().toISOString();
      writeStorage("uzu:last-backup", now);
      setLastBackup(now);
      toast.show("バックアップをダウンロードしました。", "success");
    } catch (error) {
      toast.show(error instanceof Error ? error.message : "バックアップを取得できませんでした。", "error");
    } finally {
      setBusy(false);
    }
  };

  const pickFile = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;
    try {
      const text = await file.text();
      const parsed: unknown = JSON.parse(text);
      if (!parsed || typeof parsed !== "object" || Array.isArray(parsed)) {
        throw new Error("JSONオブジェクトではありません。");
      }
      setRestorePayload(parsed as Record<string, unknown>);
      setRestoreName(file.name);
    } catch (error) {
      toast.show(error instanceof Error ? error.message : "ファイルを読み込めませんでした。", "error");
    } finally {
      event.target.value = "";
    }
  };

  const restore = async () => {
    if (!restorePayload) return;
    setBusy(true);
    try {
      await api.restoreBackup(restorePayload);
      setConfirmRestore(false);
      setRestorePayload(null);
      setRestoreName("");
      toast.show("バックアップを復元しました。", "success");
    } catch (error) {
      toast.show(error instanceof Error ? error.message : "復元できませんでした。", "error");
    } finally {
      setBusy(false);
    }
  };

  const clearLocalNotes = () => {
    const keys = Object.keys(window.localStorage).filter((key) => key.startsWith("uzu:memo:") || key.startsWith("uzu:verified:"));
    keys.forEach((key) => window.localStorage.removeItem(key));
    toast.show(`${keys.length}件のローカルメモを削除しました。`, "success");
  };

  const payloadSummary = restorePayload
    ? Object.entries(restorePayload)
        .filter(([, value]) => Array.isArray(value))
        .map(([key, value]) => `${key}: ${(value as unknown[]).length}件`)
    : [];

  return (
    <div className="page-stack narrow-page">
      <div className="page-heading">
        <div>
          <p className="eyebrow">設定・バックアップ</p>
          <h1>データと表示の設定</h1>
          <p className="page-subtitle">
            作品データはAPI、セクションの短いメモはブラウザに保存されます。
          </p>
        </div>
      </div>

      <section className="panel settings-card">
        <div className="settings-icon">
          <Icon name="download" size={21} />
        </div>
        <div className="settings-body">
          <div className="settings-heading">
            <div>
              <h2>JSONバックアップ</h2>
              <p>作品、チェックリスト、テストプレイ、審査、ガイド記事をまとめて保存します。</p>
            </div>
            <span className="settings-meta">
              最終: {lastBackup ? formatDate(lastBackup) : "未実施"}
            </span>
          </div>
          <div className="settings-actions">
            <button className="button button-primary" type="button" onClick={() => void download()} disabled={busy}>
              <Icon name="download" size={16} />
              バックアップをダウンロード
            </button>
          </div>
        </div>
      </section>

      <section className="panel settings-card">
        <div className="settings-icon amber">
          <Icon name="refresh" size={21} />
        </div>
        <div className="settings-body">
          <div className="settings-heading">
            <div>
              <h2>バックアップから復元</h2>
              <p>復元は既存データを置き換える可能性があります。事前にバックアップを取得してください。</p>
            </div>
          </div>
          <label className="file-drop">
            <input type="file" accept="application/json,.json" onChange={(event) => void pickFile(event)} />
            <span className="file-drop-icon">
              <Icon name="download" size={19} />
            </span>
            <span>
              <strong>{restoreName || "JSONファイルを選択"}</strong>
              <small>{restoreName ? "内容を確認して復元してください" : "uzu-todo-guide-backup.json"}</small>
            </span>
          </label>
          {restorePayload && (
            <div className="restore-preview">
              <div>
                <strong>読み込み内容</strong>
                <span>{payloadSummary.join(" / ") || "復元対象の配列が見つかりません"}</span>
              </div>
              <button
                className="button button-danger button-small"
                type="button"
                onClick={() => setConfirmRestore(true)}
                disabled={payloadSummary.length === 0}
              >
                復元する
              </button>
            </div>
          )}
        </div>
      </section>

      <section className="panel settings-card">
        <div className="settings-icon neutral">
          <Icon name="info" size={21} />
        </div>
        <div className="settings-body">
          <div className="settings-heading">
            <div>
              <h2>ローカルメモ</h2>
              <p>セクションのメモと最終確認日は、このブラウザにだけ保存されています。</p>
            </div>
          </div>
          <button className="button button-ghost" type="button" onClick={clearLocalNotes}>
            ローカルメモを削除
          </button>
        </div>
      </section>

      <section className="panel settings-card">
        <div className="settings-icon purple">
          <Icon name="guide" size={21} />
        </div>
        <div className="settings-body">
          <div className="settings-heading">
            <div>
              <h2>公式情報</h2>
              <p>機能の最終的な仕様は、必ずUZUの公式ガイドと現行画面で確認してください。</p>
            </div>
          </div>
          <div className="settings-links">
            <a
              className="button button-ghost button-small"
              href="https://docs.studio.uzu-app.com/"
              target="_blank"
              rel="noreferrer"
            >
              <Icon name="external" size={15} />
              公式ガイド
            </a>
            <a
              className="button button-ghost button-small"
              href="https://studio.uzu-app.com/"
              target="_blank"
              rel="noreferrer"
            >
              <Icon name="external" size={15} />
              UZU Studio
            </a>
          </div>
        </div>
      </section>

      <Modal open={confirmRestore} onClose={() => setConfirmRestore(false)} title="バックアップを復元">
        <div className="confirm-body">
          <div className="confirm-alert">
            <Icon name="alert" size={20} />
            <p>現在のデータに上書き・追加されます。復元前にバックアップを取得しましたか？</p>
          </div>
          <ul className="tip-list">
            {payloadSummary.map((line) => (
              <li key={line}>{line}</li>
            ))}
          </ul>
          <div className="form-actions">
            <button className="button button-ghost" type="button" onClick={() => setConfirmRestore(false)}>
              キャンセル
            </button>
            <button className="button button-danger" type="button" onClick={() => void restore()} disabled={busy}>
              {busy ? "復元中..." : "復元する"}
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
