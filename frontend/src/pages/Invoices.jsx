import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";
import StatusBadge from "../components/StatusBadge";

export default function Invoices() {
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    apiClient.get("/invoices").then((res) => setInvoices(res.data));
  }, []);

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>인보이스</h1>
        </div>

        <div className="card">
          {invoices.length === 0 ? (
            <div className="empty-state">발행된 인보이스가 없습니다.</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>인보이스 번호</th>
                  <th>견적서</th>
                  <th>거래처</th>
                  <th>금액</th>
                  <th>지급기한</th>
                  <th>상태</th>
                </tr>
              </thead>
              <tbody>
                {invoices.map((inv) => (
                  <tr key={inv.id}>
                    <td><Link to={`/invoices/${inv.id}`}>{inv.invoiceNumber}</Link></td>
                    <td>{inv.quoteTitle}</td>
                    <td>{inv.clientCompanyName}</td>
                    <td>{Number(inv.totalAmount).toLocaleString()}원</td>
                    <td>{inv.dueDate}</td>
                    <td><StatusBadge status={inv.status} /></td>
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
