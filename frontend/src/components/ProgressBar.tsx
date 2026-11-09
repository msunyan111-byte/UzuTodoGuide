export function ProgressBar({
  value,
  tone = "purple",
  size = "md",
}: {
  value: number;
  tone?: "purple" | "green" | "amber";
  size?: "sm" | "md";
}) {
  const safe = Math.max(0, Math.min(100, value));
  return (
    <div
      className={`progress progress-${size}`}
      role="progressbar"
      aria-valuenow={safe}
      aria-valuemin={0}
      aria-valuemax={100}
    >
      <span className={`progress-fill progress-${tone}`} style={{ width: `${safe}%` }} />
    </div>
  );
}
