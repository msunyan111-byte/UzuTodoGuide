import { useCallback, useEffect, useState } from "react";
import { api, isConnectionError } from "../api";
import { useToast } from "../components/Toast";
import { DEMO_PLAYTESTS } from "../data/demo";
import type { PlaytestRecord, PlaytestRecordInput } from "../types";
import { errorMessage } from "../utils";

export interface PlaytestCounts {
  total: number;
  uzu: number;
  questionnaire: number;
}

function countsOf(records: PlaytestRecord[]): PlaytestCounts {
  return {
    total: records.length,
    uzu: records.filter((record) => record.platform === "UZU").length,
    questionnaire: records.filter((record) => record.questionnaireCollected).length,
  };
}

function sortByPlayedAt(records: PlaytestRecord[]): PlaytestRecord[] {
  return [...records].sort((a, b) => {
    const dateOrder = b.playedAt.localeCompare(a.playedAt);
    return dateOrder !== 0 ? dateOrder : b.updatedAt.localeCompare(a.updatedAt);
  });
}

export function usePlaytests(
  projectId: string,
  demo: boolean,
  onCountsChange: (counts: PlaytestCounts) => void,
) {
  const toast = useToast();
  const [records, setRecords] = useState<PlaytestRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      if (demo) {
        const demos = sortByPlayedAt(DEMO_PLAYTESTS);
        setRecords(demos);
        onCountsChange(countsOf(demos));
        setLoading(false);
        return;
      }
      setLoading(true);
      setError(null);
      try {
        const loaded = sortByPlayedAt(await api.listPlaytests(projectId));
        if (!cancelled) {
          setRecords(loaded);
          onCountsChange(countsOf(loaded));
        }
      } catch (loadError) {
        if (!cancelled) {
          setError(
            isConnectionError(loadError)
              ? "APIに接続できません。Spring Bootが起動しているか確認してください。"
              : errorMessage(loadError, "テストプレイ記録を読み込めませんでした。"),
          );
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    void load();
    return () => {
      cancelled = true;
    };
  }, [projectId, demo, onCountsChange]);

  const applyRecords = useCallback(
    (next: PlaytestRecord[]) => {
      const sorted = sortByPlayedAt(next);
      setRecords(sorted);
      onCountsChange(countsOf(sorted));
    },
    [onCountsChange],
  );

  const add = async (input: PlaytestRecordInput): Promise<boolean> => {
    setSaving(true);
    try {
      if (demo) {
        const now = new Date().toISOString();
        const created: PlaytestRecord = {
          id: crypto.randomUUID(),
          projectId,
          playedAt: input.playedAt,
          platform: input.platform,
          participantCount: input.participantCount ?? null,
          versionNote: input.versionNote ?? null,
          questionnaireCollected: input.questionnaireCollected ?? false,
          questionnaireUrl: input.questionnaireUrl ?? null,
          findings: input.findings ?? null,
          memo: input.memo ?? null,
          createdAt: now,
          updatedAt: now,
        };
        applyRecords([...records, created]);
      } else {
        const created = await api.addPlaytest(projectId, input);
        applyRecords([...records, created]);
      }
      toast.show("テストプレイ記録を追加しました。", "success");
      return true;
    } catch (addError) {
      toast.show(errorMessage(addError, "テストプレイ記録を追加できませんでした。"), "error");
      return false;
    } finally {
      setSaving(false);
    }
  };

  const update = async (playtestId: string, input: Partial<PlaytestRecordInput>): Promise<boolean> => {
    setSaving(true);
    try {
      if (demo) {
        applyRecords(
          records.map((record) =>
            record.id === playtestId
              ? {
                  ...record,
                  ...input,
                  participantCount: input.participantCount ?? null,
                  versionNote: input.versionNote ?? null,
                  questionnaireUrl: input.questionnaireUrl ?? null,
                  findings: input.findings ?? null,
                  memo: input.memo ?? null,
                  questionnaireCollected:
                    input.questionnaireCollected ?? record.questionnaireCollected,
                  updatedAt: new Date().toISOString(),
                }
              : record,
          ),
        );
      } else {
        const updated = await api.updatePlaytest(projectId, playtestId, input);
        applyRecords(records.map((record) => (record.id === playtestId ? updated : record)));
      }
      toast.show("テストプレイ記録を更新しました。", "success");
      return true;
    } catch (updateError) {
      toast.show(errorMessage(updateError, "テストプレイ記録を更新できませんでした。"), "error");
      return false;
    } finally {
      setSaving(false);
    }
  };

  const remove = async (playtestId: string): Promise<boolean> => {
    setSaving(true);
    try {
      if (!demo) {
        await api.deletePlaytest(projectId, playtestId);
      }
      applyRecords(records.filter((record) => record.id !== playtestId));
      toast.show("テストプレイ記録を削除しました。", "success");
      return true;
    } catch (deleteError) {
      toast.show(errorMessage(deleteError, "テストプレイ記録を削除できませんでした。"), "error");
      return false;
    } finally {
      setSaving(false);
    }
  };

  return {
    records,
    loading,
    saving,
    error,
    counts: countsOf(records),
    add,
    update,
    remove,
  };
}
