import type { ReactNode } from "react";
import { RELEASE_LABELS, SCORING_LABELS, STAGE_LABELS, type Project, type Stage } from "../types";

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

export function ProjectBadges({
  project,
  showScoring = false,
}: {
  project: Project;
  showScoring?: boolean;
}) {
  return (
    <>
      <StageBadge stage={project.stage} />
      <Badge tone={project.releaseType === "PAID" ? "amber" : "green"} outline>
        {RELEASE_LABELS[project.releaseType]}
      </Badge>
      {showScoring && (
        <Badge tone="neutral" outline>
          {SCORING_LABELS[project.scoringRule]}
        </Badge>
      )}
    </>
  );
}
