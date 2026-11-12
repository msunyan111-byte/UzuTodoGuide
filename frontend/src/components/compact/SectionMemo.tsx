import { useState } from "react";
import { useToast } from "../Toast";
import { readStorage, writeStorage } from "../../utils";

interface SectionMemoProps {
  storageKey: string;
  label: string;
}

export function SectionMemo({ storageKey, label }: SectionMemoProps) {
  const toast = useToast();
  const [memo, setMemo] = useState(() => readStorage(storageKey, ""));

  const saveMemo = () => {
    writeStorage(storageKey, memo);
    toast.show("メモを保存しました。", "success");
  };

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
      />
      <div className="compact-memo-footer">
        <span>{memo.length}/2000</span>
        <button type="button" className="button button-ghost button-small" onClick={saveMemo}>
          保存
        </button>
      </div>
    </div>
  );
}
