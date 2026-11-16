import { useEffect, useRef, useState } from "react";
import { ApiError, api } from "../api";
import { useToast } from "../components/Toast";
import { errorMessage, readStorage, writeStorage } from "../utils";

/**
 * セクションのメモと最終確認日時を読み込み・保存するフックです。
 *
 * @param projectId 作品ID
 * @param sectionKey 画面セクションのキー
 * @param demo デモ表示の場合はtrue
 * @returns メモ、最終確認日時、保存状態、更新関数
 */
export function useSectionMemo(projectId: string, sectionKey: string, demo: boolean) {
  const toast = useToast();
  const storageKey = `uzu:memo:${projectId}:${sectionKey}`;
  const verifiedStorageKey = `uzu:verified:${projectId}:${sectionKey}`;
  const [memo, setMemo] = useState(() => (demo ? readStorage(storageKey, "") : ""));
  const [verifiedAt, setVerifiedAt] = useState<string | null>(() =>
    demo ? readStorage<string | null>(verifiedStorageKey, null) : null,
  );
  const [loading, setLoading] = useState(!demo);
  const [saving, setSaving] = useState(false);
  const saveQueue = useRef<Promise<boolean>>(Promise.resolve(true));
  const pendingSaves = useRef(0);

  useEffect(() => {
    if (demo) {
      return;
    }

    let cancelled = false;
    const load = async () => {
      setLoading(true);
      try {
        const state = await api.getProjectSection(projectId, sectionKey);
        if (!cancelled) {
          setMemo(state.memo ?? "");
          setVerifiedAt(state.verifiedAt);
        }
      } catch (loadError) {
        if (cancelled) return;
        if (loadError instanceof ApiError && loadError.status === 404) {
          setMemo("");
          setVerifiedAt(null);
        } else {
          toast.show(errorMessage(loadError, "セクションメモを読み込めませんでした。"), "error");
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    void load();
    return () => {
      cancelled = true;
    };
  }, [demo, projectId, sectionKey, toast]);

  const persist = async (nextMemo: string, nextVerifiedAt: string | null): Promise<boolean> => {
    pendingSaves.current += 1;
    setSaving(true);
    try {
      if (demo) {
        writeStorage(storageKey, nextMemo);
        writeStorage(verifiedStorageKey, nextVerifiedAt);
        setMemo(nextMemo);
        setVerifiedAt(nextVerifiedAt);
        toast.show("メモを保存しました。", "success");
        return true;
      }
      const state = await api.saveProjectSection(projectId, sectionKey, {
        memo: nextMemo,
        verifiedAt: nextVerifiedAt,
      });
      setMemo(state.memo ?? "");
      setVerifiedAt(state.verifiedAt);
      toast.show("メモを保存しました。", "success");
      return true;
    } catch (saveError) {
      toast.show(errorMessage(saveError, "メモを保存できませんでした。"), "error");
      return false;
    } finally {
      pendingSaves.current -= 1;
      if (pendingSaves.current === 0) {
        setSaving(false);
      }
    }
  };

  const save = (nextMemo: string, nextVerifiedAt: string | null): Promise<boolean> => {
    const task = saveQueue.current.then(() => persist(nextMemo, nextVerifiedAt));
    saveQueue.current = task;
    return task;
  };

  const saveMemo = () => {
    void save(memo, verifiedAt);
  };

  const markVerified = () => {
    void save(memo, new Date().toISOString());
  };

  return {
    memo,
    verifiedAt,
    loading,
    saving,
    setMemo,
    saveMemo,
    markVerified,
  };
}
