import { Link } from "react-router-dom";
import { Icon } from "../Icon";

export type CompactTab = "next" | "screen" | "memo";

interface CompactTabsProps {
  tab: CompactTab;
  projectId: string;
  onChange: (tab: CompactTab) => void;
}

export function CompactTabs({ tab, projectId, onChange }: CompactTabsProps) {
  return (
    <nav className="compact-tabs" aria-label="コンパクトモード">
      <button type="button" className={tab === "next" ? "active" : ""} onClick={() => onChange("next")}>
        <Icon name="arrowRight" size={19} />
        次にやる
      </button>
      <button type="button" className={tab === "screen" ? "active" : ""} onClick={() => onChange("screen")}>
        <Icon name="guide" size={19} />
        UZU画面
      </button>
      <button type="button" className={tab === "memo" ? "active" : ""} onClick={() => onChange("memo")}>
        <Icon name="copy" size={19} />
        メモ
      </button>
      <Link to={`/projects/${projectId}`}>
        <Icon name="more" size={19} />
        その他
      </Link>
    </nav>
  );
}
