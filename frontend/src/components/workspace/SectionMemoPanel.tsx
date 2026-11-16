import { useSectionMemo } from "../../hooks/useSectionMemo";
import { formatDate } from "../../utils";
import { Icon } from "../Icon";

/**
 * セクションのメモと最終確認日時を編集するパネルです。
 *
 * @param projectId 作品ID
 * @param sectionKey 画面セクションのキー
 * @param demo デモ表示の場合はtrue
 */
export function SectionMemoPanel({
  projectId,
  sectionKey,
  demo,
}: {
  projectId: string;
  sectionKey: string;
  demo: boolean;
}) {
  const { memo, verifiedAt, loading, saving, setMemo, saveMemo, markVerified } = useSectionMemo(
    projectId,
    sectionKey,
    demo,
  );

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>セクションのメモ</h2>
          <p>
            {demo
              ? "デモ中のメモはブラウザ内だけに保存されます。"
              : "短い気づきを残します。サーバーに保存され、バックアップにも含まれます。"}
          </p>
        </div>
        <span className="panel-soft">最大2000文字</span>
      </div>
      <textarea
        className="textarea"
        value={memo}
        onChange={(event) => setMemo(event.target.value.slice(0, 2000))}
        onBlur={saveMemo}
        placeholder="例: 投票結果の表示が同票時にどうなるか、UZU上で確認する"
        rows={4}
        aria-label="セクションメモ"
        disabled={loading}
      />
      <div className="memo-footer">
        <span>
          {saving ? "保存中..." : loading ? "読み込み中..." : `${memo.length}/2000文字`}
        </span>
        <div className="memo-verified">
          <span>
            <Icon name="clock" size={14} />
            最終確認: {verifiedAt ? formatDate(verifiedAt) : "未記録"}
          </span>
          <button
            type="button"
            className="button button-ghost button-small"
            onClick={markVerified}
            disabled={loading}
          >
            <Icon name="check" size={15} />
            UZU上で確認した
          </button>
        </div>
      </div>
    </section>
  );
}
