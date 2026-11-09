import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api";
import { useToast } from "../components/Toast";
import { Icon } from "../components/Icon";
import type { ReleaseType, ScoringRule } from "../types";

export function NewProject() {
  const navigate = useNavigate();
  const toast = useToast();
  const [title, setTitle] = useState("");
  const [furigana, setFurigana] = useState("");
  const [genre, setGenre] = useState("マーダーミステリー");
  const [playerCount, setPlayerCount] = useState("4");
  const [estimatedMinutes, setEstimatedMinutes] = useState("90");
  const [releaseType, setReleaseType] = useState<ReleaseType>("FREE");
  const [scoringRule, setScoringRule] = useState<ScoringRule>("TIE_ALLOWED");
  const [studioUrl, setStudioUrl] = useState("");
  const [memo, setMemo] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!title.trim()) {
      setError("タイトルを入力してください。");
      return;
    }
    setSaving(true);
    setError(null);
    try {
      const project = await api.createProject({
        title: title.trim(),
        furigana: furigana.trim() || undefined,
        genre: genre.trim() || undefined,
        playerCount: playerCount ? Number(playerCount) : undefined,
        estimatedMinutes: estimatedMinutes ? Number(estimatedMinutes) : undefined,
        releaseType,
        scoringRule,
        studioUrl: studioUrl.trim() || undefined,
        memo: memo.trim() || undefined,
      });
      toast.show("作品を作成しました。", "success");
      navigate(`/projects/${project.id}`);
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : "作品を作成できませんでした。");
      setSaving(false);
    }
  };

  return (
    <div className="page-stack narrow-page">
      <div className="breadcrumb">
        <Link to="/projects">作品</Link>
        <span>/</span>
        <span>新規作成</span>
      </div>
      <div className="page-heading">
        <div>
          <p className="eyebrow">作品を追加</p>
          <h1>新しい作品を登録する</h1>
          <p className="page-subtitle">
            UZUの作品情報と照らし合わせながら、制作の管理単位を作ります。
          </p>
        </div>
      </div>

      <form className="form-panel" onSubmit={(event) => void submit(event)}>
        <div className="form-section">
          <div className="form-section-heading">
            <span className="form-step">01</span>
            <div>
              <h2>基本情報</h2>
              <p>UZUで登録する内容と同じ値を入れてください。</p>
            </div>
          </div>
          <div className="form-grid">
            <label className="field field-full">
              <span>
                タイトル <em>必須</em>
              </span>
              <input
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                placeholder="例: 吸血鬼の館のマーダーミステリー"
                maxLength={200}
                required
              />
            </label>
            <label className="field">
              <span>ふりがな</span>
              <input
                value={furigana}
                onChange={(event) => setFurigana(event.target.value)}
                placeholder="きゅうけつきのやかたの…"
                maxLength={200}
              />
            </label>
            <label className="field">
              <span>ジャンル</span>
              <input
                value={genre}
                onChange={(event) => setGenre(event.target.value)}
                placeholder="マーダーミステリー"
                maxLength={100}
              />
            </label>
            <label className="field">
              <span>プレイ人数</span>
              <div className="input-suffix">
                <input
                  type="number"
                  min={1}
                  value={playerCount}
                  onChange={(event) => setPlayerCount(event.target.value)}
                />
                <span>人</span>
              </div>
            </label>
            <label className="field">
              <span>想定プレイ時間</span>
              <div className="input-suffix">
                <input
                  type="number"
                  min={1}
                  value={estimatedMinutes}
                  onChange={(event) => setEstimatedMinutes(event.target.value)}
                />
                <span>分</span>
              </div>
            </label>
            <label className="field field-full">
              <span>UZU作品URL</span>
              <input
                type="url"
                value={studioUrl}
                onChange={(event) => setStudioUrl(event.target.value)}
                placeholder="https://studio.uzu-app.com/..."
                maxLength={1000}
              />
            </label>
          </div>
        </div>

        <div className="form-section">
          <div className="form-section-heading">
            <span className="form-step">02</span>
            <div>
              <h2>公開と進行</h2>
              <p>有料作品はテストプレイと審査の条件が増えます。</p>
            </div>
          </div>
          <div className="choice-row">
            <label className={`choice-card${releaseType === "FREE" ? " selected" : ""}`}>
              <input
                type="radio"
                name="releaseType"
                value="FREE"
                checked={releaseType === "FREE"}
                onChange={() => setReleaseType("FREE")}
              />
              <span className="choice-title">無料公開</span>
              <span className="choice-description">まず遊んでもらうことを優先</span>
            </label>
            <label className={`choice-card${releaseType === "PAID" ? " selected" : ""}`}>
              <input
                type="radio"
                name="releaseType"
                value="PAID"
                checked={releaseType === "PAID"}
                onChange={() => setReleaseType("PAID")}
              />
              <span className="choice-title">有料公開</span>
              <span className="choice-description">詳細審査とテスト3回以上が必要</span>
            </label>
          </div>
          <div className="choice-row">
            <label className={`choice-card${scoringRule === "TIE_ALLOWED" ? " selected" : ""}`}>
              <input
                type="radio"
                name="scoringRule"
                value="TIE_ALLOWED"
                checked={scoringRule === "TIE_ALLOWED"}
                onChange={() => setScoringRule("TIE_ALLOWED")}
              />
              <span className="choice-title">同数最多を許可</span>
              <span className="choice-description">同票でも最多として扱う</span>
            </label>
            <label className={`choice-card${scoringRule === "SOLE_TOP_REQUIRED" ? " selected" : ""}`}>
              <input
                type="radio"
                name="scoringRule"
                value="SOLE_TOP_REQUIRED"
                checked={scoringRule === "SOLE_TOP_REQUIRED"}
                onChange={() => setScoringRule("SOLE_TOP_REQUIRED")}
              />
              <span className="choice-title">単独最多のみ</span>
              <span className="choice-description">同票の場合は最多とみなさない</span>
            </label>
          </div>
        </div>

        <div className="form-section">
          <div className="form-section-heading">
            <span className="form-step">03</span>
            <div>
              <h2>制作メモ</h2>
              <p>短いメモだけを残します。長文の設計資料は外部に置いてください。</p>
            </div>
          </div>
          <label className="field field-full">
            <span>メモ</span>
            <textarea
              value={memo}
              onChange={(event) => setMemo(event.target.value)}
              placeholder="例: 事件は殺人未遂。犯行は施錠前。"
              maxLength={10000}
              rows={3}
            />
            <span className="field-hint">{memo.length}/10000文字</span>
          </label>
        </div>

        {error && (
          <div className="form-error">
            <Icon name="alert" size={17} />
            {error}
          </div>
        )}

        <div className="form-actions">
          <Link to="/projects" className="button button-ghost">
            キャンセル
          </Link>
          <button className="button button-primary" type="submit" disabled={saving}>
            {saving ? "作成中..." : "作品を作成"}
            {!saving && <Icon name="arrowRight" size={16} />}
          </button>
        </div>
      </form>
    </div>
  );
}
