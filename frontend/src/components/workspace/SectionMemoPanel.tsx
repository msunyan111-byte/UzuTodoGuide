import { useState } from "react";
import { formatDate, readStorage, writeStorage } from "../../utils";
import { Icon } from "../Icon";
import { useToast } from "../Toast";

export function SectionMemoPanel({ projectId, sectionKey }: { projectId: string; sectionKey: string }) {
  const toast = useToast();
  const [memo, setMemo] = useState(() => readStorage(`uzu:memo:${projectId}:${sectionKey}`, ""));
  const [verifiedAt, setVerifiedAt] = useState<string | null>(() =>
    readStorage<string | null>(`uzu:verified:${projectId}:${sectionKey}`, null),
  );

  const saveMemo = () => {
    writeStorage(`uzu:memo:${projectId}:${sectionKey}`, memo);
    toast.show("セクションのメモを保存しました。", "success");
  };

  const markVerified = () => {
    const now = new Date().toISOString();
    writeStorage(`uzu:verified:${projectId}:${sectionKey}`, now);
    setVerifiedAt(now);
    toast.show("UZU上での確認日を記録しました。", "success");
  };

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>セクションのメモ</h2>
          <p>短い気づきだけを残します。ブラウザに自動保存されます。</p>
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
      />
      <div className="memo-footer">
        <span>{memo.length}/2000文字</span>
        <div className="memo-verified">
          <span>
            <Icon name="clock" size={14} />
            最終確認: {verifiedAt ? formatDate(verifiedAt) : "未記録"}
          </span>
          <button type="button" className="button button-ghost button-small" onClick={markVerified}>
            <Icon name="check" size={15} />
            UZU上で確認した
          </button>
        </div>
      </div>
    </section>
  );
}
