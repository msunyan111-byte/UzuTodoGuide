import { useState } from "react";
import type { ChecklistItem, ChecklistStatus } from "../types";
import { STATUS_LABELS } from "../types";
import { formatDay } from "../utils";
import { Icon } from "./Icon";

const NEXT_STATUS: Record<ChecklistStatus, ChecklistStatus> = {
  NOT_STARTED: "IN_PROGRESS",
  IN_PROGRESS: "DONE",
  DONE: "NOT_STARTED",
};

export function ChecklistRow({
  item,
  onStatusChange,
  onMemoChange,
  disabled,
}: {
  item: ChecklistItem;
  onStatusChange: (status: ChecklistStatus) => void;
  onMemoChange: (memo: string) => void;
  disabled?: boolean;
}) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState("");
  const done = item.status === "DONE";

  const openMemo = () => {
    setDraft(item.memo ?? "");
    setEditing(true);
  };

  const saveMemo = () => {
    setEditing(false);
    if (draft !== (item.memo ?? "")) {
      onMemoChange(draft);
    }
  };

  return (
    <div className={`check-row${done ? " check-row-done" : ""}`}>
      <button
        type="button"
        className={`check-box${done ? " checked" : ""}`}
        onClick={() => onStatusChange(done ? "NOT_STARTED" : "DONE")}
        disabled={disabled}
        aria-label={done ? "未着手に戻す" : "完了にする"}
      >
        {done && <Icon name="check" size={14} />}
      </button>
      <div className="check-row-body">
        <div className="check-row-head">
          <span className="check-row-name">{item.name}</span>
          <button
            type="button"
            className={`status-pill status-${item.status.toLowerCase()}`}
            onClick={() => onStatusChange(NEXT_STATUS[item.status])}
            disabled={disabled}
            title="クリックで状態を切り替え"
          >
            {STATUS_LABELS[item.status]}
          </button>
        </div>
        {item.description && <p className="check-row-description">{item.description}</p>}
        <div className="check-row-meta">
          <span className={item.dueDate ? "due-date" : ""}>
            <Icon name="clock" size={13} />
            {formatDay(item.dueDate)}
          </span>
          {item.memo && !editing && <span className="check-memo-preview">{item.memo}</span>}
          <button type="button" className="text-button" onClick={() => (editing ? setEditing(false) : openMemo())}>
            {editing ? "閉じる" : item.memo ? "メモを編集" : "メモを追加"}
          </button>
        </div>
        {editing && (
          <textarea
            className="textarea check-memo-input"
            value={draft}
            onChange={(event) => setDraft(event.target.value)}
            onBlur={saveMemo}
            placeholder="この項目のメモ"
            rows={2}
            autoFocus
          />
        )}
      </div>
    </div>
  );
}
