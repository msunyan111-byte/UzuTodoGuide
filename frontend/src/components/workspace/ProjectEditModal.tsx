import { useState, type FormEvent } from "react";
import { api } from "../../api";
import type { Project, ReleaseType, ScoringRule, Stage } from "../../types";
import { RELEASE_LABELS, SCORING_LABELS, STAGE_LABELS } from "../../types";
import { Modal } from "../Modal";
import { useToast } from "../Toast";

export function ProjectEditModal({
  project,
  demo,
  onClose,
  onSaved,
}: {
  project: Project;
  demo: boolean;
  onClose: () => void;
  onSaved: (project: Project) => void;
}) {
  const toast = useToast();
  const [title, setTitle] = useState(project.title);
  const [playerCount, setPlayerCount] = useState(project.playerCount ? String(project.playerCount) : "");
  const [estimatedMinutes, setEstimatedMinutes] = useState(
    project.estimatedMinutes ? String(project.estimatedMinutes) : "",
  );
  const [releaseType, setReleaseType] = useState<ReleaseType>(project.releaseType);
  const [scoringRule, setScoringRule] = useState<ScoringRule>(project.scoringRule);
  const [stage, setStage] = useState<Stage>(project.stage);
  const [studioUrl, setStudioUrl] = useState(project.studioUrl ?? "");
  const [saving, setSaving] = useState(false);

  const save = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    const input = {
      title: title.trim(),
      playerCount: playerCount ? Number(playerCount) : undefined,
      estimatedMinutes: estimatedMinutes ? Number(estimatedMinutes) : undefined,
      releaseType,
      scoringRule,
      stage,
      studioUrl: studioUrl.trim() || undefined,
    };
    try {
      if (demo) {
        onSaved({
          ...project,
          title: input.title,
          playerCount: input.playerCount ?? null,
          estimatedMinutes: input.estimatedMinutes ?? null,
          releaseType,
          scoringRule,
          stage,
          studioUrl: studioUrl.trim() || null,
        });
      } else {
        const updated = await api.updateProject(project.id, input);
        onSaved(updated);
      }
      toast.show("作品情報を更新しました。", "success");
      onClose();
    } catch (error) {
      toast.show(error instanceof Error ? error.message : "更新できませんでした。", "error");
      setSaving(false);
    }
  };

  return (
    <Modal open onClose={onClose} title="作品情報を編集">
      <form className="modal-form" onSubmit={(event) => void save(event)}>
        <label className="field">
          <span>タイトル</span>
          <input value={title} onChange={(event) => setTitle(event.target.value)} required />
        </label>
        <div className="form-grid">
          <label className="field">
            <span>プレイ人数</span>
            <input
              type="number"
              min={1}
              value={playerCount}
              onChange={(event) => setPlayerCount(event.target.value)}
            />
          </label>
          <label className="field">
            <span>想定時間（分）</span>
            <input
              type="number"
              min={1}
              value={estimatedMinutes}
              onChange={(event) => setEstimatedMinutes(event.target.value)}
            />
          </label>
        </div>
        <div className="form-grid">
          <label className="field">
            <span>公開形態</span>
            <select
              value={releaseType}
              onChange={(event) => setReleaseType(event.target.value as ReleaseType)}
            >
              {(Object.keys(RELEASE_LABELS) as ReleaseType[]).map((value) => (
                <option key={value} value={value}>
                  {RELEASE_LABELS[value]}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>制作段階</span>
            <select value={stage} onChange={(event) => setStage(event.target.value as Stage)}>
              {(Object.keys(STAGE_LABELS) as Stage[]).map((value) => (
                <option key={value} value={value}>
                  {STAGE_LABELS[value]}
                </option>
              ))}
            </select>
          </label>
        </div>
        <label className="field">
          <span>得点ルール</span>
          <select
            value={scoringRule}
            onChange={(event) => setScoringRule(event.target.value as ScoringRule)}
          >
            {(Object.keys(SCORING_LABELS) as ScoringRule[]).map((value) => (
              <option key={value} value={value}>
                {SCORING_LABELS[value]}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span>UZU作品URL</span>
          <input
            type="url"
            value={studioUrl}
            onChange={(event) => setStudioUrl(event.target.value)}
            placeholder="https://studio.uzu-app.com/..."
          />
        </label>
        <div className="form-actions">
          <button type="button" className="button button-ghost" onClick={onClose}>
            キャンセル
          </button>
          <button type="submit" className="button button-primary" disabled={saving}>
            {saving ? "保存中..." : "保存する"}
          </button>
        </div>
      </form>
    </Modal>
  );
}
