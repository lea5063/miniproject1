const LABELS = {
  DRAFT: "임시저장",
  SENT: "발송완료",
  APPROVED: "승인완료",
  REJECTED: "거절됨",
  UNPAID: "미결제",
  PAID: "결제완료",
  OVERDUE: "기한초과",
};

export default function StatusBadge({ status }) {
  const cls = `badge badge-${status?.toLowerCase()}`;
  return <span className={cls}>{LABELS[status] || status}</span>;
}
