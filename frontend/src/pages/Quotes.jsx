import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";
import StatusBadge from "../components/StatusBadge";
import { useAuth } from "../context/AuthContext";

export default function Quotes() {
  const { user } = useAuth();
  const [quotes, setQuotes] = useState([]);

  useEffect(() => {
    apiClient.get("/quotes").then((res) => setQuotes(res.data));
  }, []);

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>견적서</h1>
          {user?.role === "FREELANCER" && (
            <Link to="/quotes/new" className="btn btn-primary">
              + 새 견적서 작성
            </Link>
          )}
        </div>

        <div className="card">
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
                  <th>유효기한</th>
                </tr>
              </thead>
              <tbody>
                {quotes.map((q) => (
                  <tr key={q.id}>
                    <td><Link to={`/quotes/${q.id}`}>{q.title}</Link></td>
                    <td>{q.clientCompanyName}</td>
                    <td><StatusBadge status={q.status} /></td>
                    <td>{Number(q.totalAmount).toLocaleString()}원</td>
                    <td>{q.validUntil || "-"}</td>
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
