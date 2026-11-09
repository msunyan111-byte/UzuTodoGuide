import { useState, type FormEvent } from "react";
import type { ChecklistItem, ChecklistStatus } from "../../types";
import { ChecklistRow } from "../ChecklistRow";
import { Icon } from "../Icon";

export function ChecklistPanel({
  items,
  progress,
  savingIds,
  onStatusChange,
  onMemoChange,
  onAdd,
}: {
  items: ChecklistItem[];
  progress: { done: number; total: number };
  savingIds: Set<string>;
  onStatusChange: (item: ChecklistItem, status: ChecklistStatus) => void;
  onMemoChange: (item: ChecklistItem, memo: string) => void;
  onAdd: (name: string, description: string) => Promise<boolean>;
}) {
  const [adding, setAdding] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const added = await onAdd(name.trim(), description.trim());
    if (added) {
      setName("");
      setDescription("");
      setAdding(false);
    }
  };

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>チェック項目</h2>
          <p>
            {progress.total > 0
              ? `${progress.done}/${progress.total} 完了。UZU上で確認した項目から完了にしてください。`
              : "この画面に対応するチェック項目はまだありません。"}
          </p>
        </div>
        <span className="panel-count">{progress.total}</span>
      </div>

      {items.length > 0 ? (
        <div className="check-list">
          {items.map((item) => (
            <ChecklistRow
              key={item.id}
              item={item}
              onStatusChange={(status) => onStatusChange(item, status)}
              onMemoChange={(memo) => onMemoChange(item, memo)}
              disabled={savingIds.has(item.id)}
            />
          ))}
        </div>
      ) : (
        <div className="section-empty">
          <p>この画面の項目を追加するか、UZU上で設定を確認してください。</p>
        </div>
      )}

      {adding ? (
        <form className="add-check-form" onSubmit={(event) => void submit(event)}>
          <input
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="チェック項目名"
            maxLength={200}
            autoFocus
          />
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="補足（任意）"
            rows={2}
          />
          <div className="add-check-actions">
            <button
              type="button"
              className="button button-ghost button-small"
              onClick={() => setAdding(false)}
            >
              キャンセル
            </button>
            <button type="submit" className="button button-primary button-small">
              追加する
            </button>
          </div>
        </form>
      ) : (
        <button type="button" className="add-check-button" onClick={() => setAdding(true)}>
          <Icon name="plus" size={16} />
          この画面にチェック項目を追加
        </button>
      )}
    </section>
  );
}
