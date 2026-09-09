import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";
import StatusBadge from "../components/StatusBadge";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user } = useAuth();
  const [quotes, setQuotes] = useState([]);
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    apiClient.get("/quotes").then((res) => setQuotes(res.data));
    apiClient.get("/invoices").then((res) => setInvoices(res.data));
  }, []);

  const isFreelancer = user?.role === "FREELANCER";

  const pendingApproval = quotes.filter((q) => q.status === "SENT").length;
  const approved = quotes.filter((q) => q.status === "APPROVED").length;
  const unpaidTotal = invoices
    .filter((i) => i.status !== "PAID")
    .reduce((sum, i) => sum + Number(i.totalAmount), 0);

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>대시보드</h1>
        </div>

        <div className="summary-grid">
          <div className="summary-card">
            <div className="label">{isFreelancer ? "발송한 견적서" : "받은 견적서"}</div>
            <div className="value">{quotes.length}</div>
          </div>
          <div className="summary-card">
            <div className="label">승인 대기</div>
            <div className="value">{pendingApproval}</div>
          </div>
          <div className="summary-card">
            <div className="label">승인 완료</div>
            <div className="value">{approved}</div>
          </div>
          <div className="summary-card">
            <div className="label">{isFreelancer ? "미수금" : "미결제 금액"}</div>
            <div className="value">{unpaidTotal.toLocaleString()}원</div>
          </div>
        </div>

        <div className="card">
          <div className="page-header">
            <h1 style={{ fontSize: 16 }}>최근 견적서</h1>
            <Link to="/quotes" className="btn btn-outline btn-sm">전체 보기</Link>
          </div>
          {quotes.length === 0 ? (
            <div className="empty-state">아직 견적서가 없습니다.</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>제목</th>
                  <th>거래처</th>
                  <th>상태</th>
                  <th>금액</th>
                </tr>
              </thead>
              <tbody>
                {quotes.slice(0, 5).map((q) => (
                  <tr key={q.id}>
                    <td><Link to={`/quotes/${q.id}`}>{q.title}</Link></td>
                    <td>{q.clientCompanyName}</td>
                    <td><StatusBadge status={q.status} /></td>
                    <td>{Number(q.totalAmount).toLocaleString()}원</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </Layout>
  );
}
