import { useState, type FormEvent } from "react";
import { usePlaytests, type PlaytestCounts } from "../../hooks/usePlaytests";
import type { PlaytestPlatform, PlaytestRecord, Project } from "../../types";
import { PLAYTEST_PLATFORM_LABELS } from "../../types";
import { formatDay } from "../../utils";
import { Badge } from "../Badge";
import { Icon } from "../Icon";

interface PlaytestFormState {
  playedAt: string;
  platform: PlaytestPlatform;
  participantCount: string;
  versionNote: string;
  questionnaireCollected: boolean;
  questionnaireUrl: string;
  findings: string;
  memo: string;
}

function todayInTokyo(): string {
  return new Intl.DateTimeFormat("en-CA", { timeZone: "Asia/Tokyo" }).format(new Date());
}

function emptyForm(): PlaytestFormState {
  return {
    playedAt: todayInTokyo(),
    platform: "UZU",
    participantCount: "",
    versionNote: "",
    questionnaireCollected: false,
    questionnaireUrl: "",
    findings: "",
    memo: "",
  };
}

function formFor(record: PlaytestRecord): PlaytestFormState {
  return {
    playedAt: record.playedAt,
    platform: record.platform,
    participantCount: record.participantCount === null ? "" : String(record.participantCount),
    versionNote: record.versionNote ?? "",
    questionnaireCollected: record.questionnaireCollected,
    questionnaireUrl: record.questionnaireUrl ?? "",
    findings: record.findings ?? "",
    memo: record.memo ?? "",
  };
}

export function PlaytestPanel({
  project,
  demo,
  onCountsChange,
}: {
  project: Project;
  demo: boolean;
  onCountsChange: (counts: PlaytestCounts) => void;
}) {
  const { records, loading, saving, error, counts, add, update, remove } = usePlaytests(
    project.id,
    demo,
    onCountsChange,
  );
  const [formOpen, setFormOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<PlaytestFormState>(emptyForm);

  const isPaid = project.releaseType === "PAID";
  const metRequirement = counts.total >= 3 && counts.uzu >= 1 && counts.questionnaire >= 1;

  const updateForm = <K extends keyof PlaytestFormState>(key: K, value: PlaytestFormState[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
  };

  const startAdd = () => {
    setEditingId(null);
    setForm(emptyForm());
    setFormOpen(true);
  };

  const startEdit = (record: PlaytestRecord) => {
    setEditingId(record.id);
    setForm(formFor(record));
    setFormOpen(true);
  };

  const cancelForm = () => {
    setEditingId(null);
    setForm(emptyForm());
    setFormOpen(false);
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const input = {
      playedAt: form.playedAt,
      platform: form.platform,
      participantCount: form.participantCount ? Number(form.participantCount) : undefined,
      versionNote: form.versionNote.trim(),
      questionnaireCollected: form.questionnaireCollected,
      questionnaireUrl: form.questionnaireUrl.trim(),
      findings: form.findings.trim(),
      memo: form.memo.trim(),
    };
    const saved = editingId ? await update(editingId, input) : await add(input);
    if (saved) {
      cancelForm();
    }
  };

  const confirmRemove = async (record: PlaytestRecord) => {
    const label = `${formatDay(record.playedAt)}の記録`;
    if (!window.confirm(`${label}を削除しますか？`)) return;
    const removed = await remove(record.id);
    if (removed && editingId === record.id) {
      cancelForm();
    }
  };

  return (
    <section className="panel playtest-panel">
      <div className="panel-heading">
        <div>
          <h2>テストプレイ記録</h2>
          <p>実施日ごとの結果を残し、有料公開に必要な回数とアンケート回収を確認します。</p>
        </div>
        <span className="panel-count">{counts.total}</span>
      </div>

      <div className="playtest-summary">
        <div>
          <span>合計</span>
          <strong>{counts.total}回</strong>
          <small>3回以上が必要</small>
        </div>
        <div>
          <span>UZU上</span>
          <strong>{counts.uzu}回</strong>
          <small>1回以上が必要</small>
        </div>
        <div>
          <span>アンケート</span>
          <strong>{counts.questionnaire}件</strong>
          <small>回収済みの記録</small>
        </div>
      </div>

      {isPaid && !metRequirement ? (
        <div className="playtest-requirement">
          <Icon name="alert" size={17} />
          <div>
            <strong>有料公開の条件に未達です</strong>
            <span>合計3回以上、うちUZU上1回以上、アンケート回収が必要です。</span>
          </div>
        </div>
      ) : (
        <div className="playtest-requirement met">
          <Icon name="check" size={17} />
          <div>
            <strong>{isPaid ? "有料公開の記録条件を満たしています" : "テストプレイ記録を管理中です"}</strong>
            <span>{isPaid ? "提出前にアンケートの共有リンクを確認してください。" : "無料作品でも記録と気づきを残せます。"}</span>
          </div>
        </div>
      )}

      {error && (
        <div className="form-error">
          <Icon name="alert" size={16} />
          {error}
        </div>
      )}

      {formOpen ? (
        <form className="playtest-form" onSubmit={(event) => void submit(event)}>
          <div className="form-grid">
            <label className="field">
              <span>実施日 <em>必須</em></span>
              <input
                type="date"
                value={form.playedAt}
                onChange={(event) => updateForm("playedAt", event.target.value)}
                required
              />
            </label>
            <label className="field">
              <span>実施場所 <em>必須</em></span>
              <select
                value={form.platform}
                onChange={(event) => updateForm("platform", event.target.value as PlaytestPlatform)}
              >
                {(Object.keys(PLAYTEST_PLATFORM_LABELS) as PlaytestPlatform[]).map((value) => (
                  <option key={value} value={value}>
                    {PLAYTEST_PLATFORM_LABELS[value]}
                  </option>
                ))}
              </select>
            </label>
            <label className="field">
              <span>参加人数</span>
              <input
                type="number"
                min={1}
                value={form.participantCount}
                onChange={(event) => updateForm("participantCount", event.target.value)}
                placeholder="4"
              />
            </label>
            <label className="field">
              <span>対象バージョン</span>
              <input
                value={form.versionNote}
                onChange={(event) => updateForm("versionNote", event.target.value)}
                maxLength={200}
                placeholder="v1.2"
              />
            </label>
          </div>

          <label className="playtest-check">
            <input
              type="checkbox"
              checked={form.questionnaireCollected}
              onChange={(event) => updateForm("questionnaireCollected", event.target.checked)}
            />
            アンケートを回収済み
          </label>

          <label className="field">
            <span>アンケートURL</span>
            <input
              type="url"
              value={form.questionnaireUrl}
              onChange={(event) => updateForm("questionnaireUrl", event.target.value)}
              maxLength={1000}
              placeholder="https://docs.google.com/..."
            />
          </label>

          <label className="field">
            <span>気づき・改善点</span>
            <textarea
              className="textarea"
              value={form.findings}
              onChange={(event) => updateForm("findings", event.target.value)}
              rows={3}
              placeholder="進行、推理、表示、音量などで気づいたこと"
            />
          </label>

          <label className="field">
            <span>メモ</span>
            <textarea
              className="textarea"
              value={form.memo}
              onChange={(event) => updateForm("memo", event.target.value)}
              rows={2}
              placeholder="参加者、次回の対応など"
            />
          </label>

          <div className="form-actions">
            <button type="button" className="button button-ghost" onClick={cancelForm}>
              キャンセル
            </button>
            <button type="submit" className="button button-primary" disabled={saving}>
              {saving ? "保存中..." : editingId ? "更新する" : "追加する"}
            </button>
          </div>
        </form>
      ) : (
        <button type="button" className="add-check-button" onClick={startAdd}>
          <Icon name="plus" size={16} />
          テストプレイ記録を追加
        </button>
      )}

      {loading ? (
        <p className="playtest-loading">テストプレイ記録を読み込んでいます...</p>
      ) : records.length === 0 ? (
        <div className="section-empty">
          <p>まだ記録がありません。実施したテストプレイから追加してください。</p>
        </div>
      ) : (
        <div className="playtest-list">
          {records.map((record) => (
            <article className="playtest-record" key={record.id}>
              <div className="playtest-record-head">
                <div>
                  <strong>{formatDay(record.playedAt)}</strong>
                  <div className="playtest-record-badges">
                    <Badge tone={record.platform === "UZU" ? "purple" : "neutral"} outline>
                      {PLAYTEST_PLATFORM_LABELS[record.platform]}
                    </Badge>
                    <Badge tone={record.questionnaireCollected ? "green" : "amber"} outline>
                      {record.questionnaireCollected ? "アンケート回収済み" : "アンケート未回収"}
                    </Badge>
                    {record.participantCount !== null && (
                      <span className="playtest-record-meta">{record.participantCount}人</span>
                    )}
                    {record.versionNote && (
                      <span className="playtest-record-meta">{record.versionNote}</span>
                    )}
                  </div>
                </div>
                <div className="playtest-record-actions">
                  <button
                    type="button"
                    className="button button-ghost button-small"
                    onClick={() => startEdit(record)}
                  >
                    編集
                  </button>
                  <button
                    type="button"
                    className="button button-danger button-small"
                    onClick={() => void confirmRemove(record)}
                    disabled={saving}
                  >
                    削除
                  </button>
                </div>
              </div>
              {record.questionnaireUrl && (
                <a
                  className="text-link playtest-record-link"
                  href={record.questionnaireUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  <Icon name="external" size={14} />
                  アンケートを開く
                </a>
              )}
              {record.findings && <p className="playtest-record-text">{record.findings}</p>}
              {record.memo && <p className="playtest-record-memo">{record.memo}</p>}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
