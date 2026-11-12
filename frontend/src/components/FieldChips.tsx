export function FieldChips({
  fields,
  limit,
}: {
  fields: string[];
  limit?: number;
}) {
  if (fields.length === 0) return null;
  const visible = limit === undefined ? fields : fields.slice(0, limit);
  const hiddenCount = fields.length - visible.length;
  return (
    <div className="chip-cloud">
      {visible.map((field) => (
        <span className="info-chip" key={field}>
          {field}
        </span>
      ))}
      {hiddenCount > 0 && <span className="info-chip info-chip-more">+{hiddenCount}</span>}
    </div>
  );
}
