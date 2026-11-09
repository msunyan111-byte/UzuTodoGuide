import type { ReactNode } from "react";
import { STAGE_LABELS, type Stage } from "../types";

type Tone = "neutral" | "purple" | "green" | "amber" | "red" | "blue" | "dark";

export function Badge({
  children,
  tone = "neutral",
  outline = false,
}: {
  children: ReactNode;
  tone?: Tone;
  outline?: boolean;
}) {
  return (
    <span className={`badge badge-${tone}${outline ? " badge-outline" : ""}`}>
      {children}
    </span>
  );
}

const STAGE_TONES: Record<Stage, Tone> = {
  PLANNING: "neutral",
  IN_PROGRESS: "purple",
  FUNCTIONAL_CHECK: "blue",
  PLAYTEST: "amber",
  REVISION: "amber",
  REVIEW_SUBMITTED: "blue",
  REVIEW_RESPONSE: "red",
  RELEASE_PREPARATION: "purple",
  RELEASED: "green",
};

export function StageBadge({ stage }: { stage: Stage }) {
  return <Badge tone={STAGE_TONES[stage]}>{STAGE_LABELS[stage]}</Badge>;
}
