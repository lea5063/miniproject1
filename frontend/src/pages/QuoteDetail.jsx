import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";
import StatusBadge from "../components/StatusBadge";
import { useAuth } from "../context/AuthContext";

export default function QuoteDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [quote, setQuote] = useState(null);
  const [error, setError] = useState("");
  const [dueDate, setDueDate] = useState("");

  function load() {
    apiClient.get(`/quotes/${id}`).then((res) => setQuote(res.data));
  }

  useEffect(() => {
    load();
  }, [id]);

  async function handleSend() {
    try {
      await apiClient.post(`/quotes/${id}/send`);
      load();
    } catch (err) {
      setError(err.response?.data?.message || "발송에 실패했습니다.");
    }
  }

  async function handleDelete() {
    if (!confirm("이 견적서를 삭제할까요?")) return;
    await apiClient.delete(`/quotes/${id}`);
    navigate("/quotes");
  }

  async function handleDecision(decision) {
    try {
      await apiClient.put(`/quotes/${id}/decision`, { decision });
      load();
    } catch (err) {
      setError(err.response?.data?.message || "처리에 실패했습니다.");
    }
  }

  async function handleIssueInvoice(e) {
    e.preventDefault();
    try {
      await apiClient.post(`/quotes/${id}/invoice`, { dueDate });
      navigate("/invoices");
    } catch (err) {
      setError(err.response?.data?.message || "인보이스 발행에 실패했습니다.");
    }
  }

  if (!quote) {
    return (
      <Layout>
        <div className="page">불러오는 중...</div>
      </Layout>
    );
  }

  const isFreelancer = user?.role === "FREELANCER";

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>{quote.title}</h1>
          <StatusBadge status={quote.status} />
        </div>

        {error && <div className="form-error">{error}</div>}

        <div className="card">
          <div className="detail-grid">
            <div>
              <div className="k">거래처</div>
              <div className="v">{quote.clientCompanyName}</div>
            </div>
            <div>
              <div className="k">유효기한</div>
              <div className="v">{quote.validUntil || "-"}</div>
            </div>
            <div>
              <div className="k">비고</div>
              <div className="v">{quote.memo || "-"}</div>
            </div>
          </div>

          <table>
            <thead>
              <tr>
                <th>품목</th>
                <th>수량</th>
                <th>단가</th>
                <th>금액</th>
              </tr>
            </thead>
            <tbody>
              {quote.items.map((item) => (
                <tr key={item.id}>
                  <td>{item.name}</td>
                  <td>{item.quantity}</td>
                  <td>{Number(item.unitPrice).toLocaleString()}원</td>
                  <td>{Number(item.amount).toLocaleString()}원</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="total-row">
            <span>합계</span>
            <span>{Number(quote.totalAmount).toLocaleString()}원</span>
          </div>

          <div className="action-bar">
            {isFreelancer && quote.status === "DRAFT" && (
              <>
                <button className="btn btn-primary" onClick={handleSend}>견적서 발송</button>
                <button className="btn btn-danger" onClick={handleDelete}>삭제</button>
              </>
            )}
            {!isFreelancer && quote.status === "SENT" && (
              <>
                <button className="btn btn-primary" onClick={() => handleDecision("APPROVED")}>승인</button>
                <button className="btn btn-danger" onClick={() => handleDecision("REJECTED")}>거절</button>
              </>
            )}
          </div>
        </div>

        {isFreelancer && quote.status === "APPROVED" && (
          <div className="card">
            <h2 style={{ fontSize: 15, marginTop: 0 }}>인보이스 발행</h2>
            <form onSubmit={handleIssueInvoice} style={{ display: "flex", gap: 10, alignItems: "flex-end" }}>
              <div className="field" style={{ marginBottom: 0 }}>
                <label>지급 기한</label>
                <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
              </div>
              <button type="submit" className="btn btn-primary">인보이스 발행</button>
            </form>
          </div>
        )}
      </div>
    </Layout>
  );
}
