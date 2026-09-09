import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";
import StatusBadge from "../components/StatusBadge";
import { useAuth } from "../context/AuthContext";

export default function InvoiceDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState("");

  function load() {
    apiClient.get(`/invoices/${id}`).then((res) => setInvoice(res.data));
  }

  useEffect(() => {
    load();
  }, [id]);

  async function handleMarkPaid() {
    try {
      await apiClient.put(`/invoices/${id}/status`, { status: "PAID" });
      load();
    } catch (err) {
      setError(err.response?.data?.message || "처리에 실패했습니다.");
    }
  }

  if (!invoice) {
    return (
      <Layout>
        <div className="page">불러오는 중...</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>{invoice.invoiceNumber}</h1>
          <StatusBadge status={invoice.status} />
        </div>

        {error && <div className="form-error">{error}</div>}

        <div className="card">
          <div className="detail-grid">
            <div>
              <div className="k">견적서</div>
              <div className="v">{invoice.quoteTitle}</div>
            </div>
            <div>
              <div className="k">거래처</div>
              <div className="v">{invoice.clientCompanyName}</div>
            </div>
            <div>
              <div className="k">청구 금액</div>
              <div className="v">{Number(invoice.totalAmount).toLocaleString()}원</div>
            </div>
            <div>
              <div className="k">지급 기한</div>
              <div className="v">{invoice.dueDate}</div>
            </div>
            <div>
              <div className="k">발행일</div>
              <div className="v">{invoice.issuedAt?.slice(0, 10)}</div>
            </div>
            {invoice.paidAt && (
              <div>
                <div className="k">입금 확인일</div>
                <div className="v">{invoice.paidAt.slice(0, 10)}</div>
              </div>
            )}
          </div>

          {user?.role === "FREELANCER" && invoice.status !== "PAID" && (
            <div className="action-bar">
              <button className="btn btn-primary" onClick={handleMarkPaid}>입금 확인 처리</button>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}
