import { useSectionMemo } from "../../hooks/useSectionMemo";

interface SectionMemoProps {
  projectId: string;
  sectionKey: string;
  label: string;
  demo: boolean;
}

/**
 * コンパクトモードでセクションのメモを編集するパネルです。
 *
 * @param props 作品ID、セクションキー、表示ラベル、デモ表示かどうか
 */
export function SectionMemo({ projectId, sectionKey, label, demo }: SectionMemoProps) {
  const { memo, loading, saving, setMemo, saveMemo } = useSectionMemo(
    projectId,
    sectionKey,
    demo,
  );

  return (
    <div className="compact-memo">
      <span className="compact-label">{label}</span>
      <textarea
        className="textarea"
        value={memo}
        onChange={(event) => setMemo(event.target.value.slice(0, 2000))}
        onBlur={saveMemo}
        placeholder="気づきを短く残す"
        rows={8}
        aria-label={`${label}のメモ`}
        disabled={loading}
      />
      <div className="compact-memo-footer">
        <span>{saving ? "保存中..." : loading ? "読み込み中..." : `${memo.length}/2000`}</span>
        <button
          type="button"
          className="button button-ghost button-small"
          onClick={saveMemo}
          disabled={saving || loading}
        >
          保存
        </button>
      </div>
    </div>
  );
}
