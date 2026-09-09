import { useEffect, useState } from "react";
import apiClient from "../api/client";
import Layout from "../components/Layout";

export default function Clients() {
  const [clients, setClients] = useState([]);
  const [form, setForm] = useState({ companyName: "", contactName: "", email: "", phone: "" });
  const [error, setError] = useState("");

  function load() {
    apiClient.get("/clients").then((res) => setClients(res.data));
  }

  useEffect(() => {
    load();
  }, []);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      await apiClient.post("/clients", form);
      setForm({ companyName: "", contactName: "", email: "", phone: "" });
      load();
    } catch (err) {
      setError(err.response?.data?.message || "등록에 실패했습니다.");
    }
  }

  async function handleDelete(id) {
    if (!confirm("이 거래처를 삭제할까요?")) return;
    try {
      await apiClient.delete(`/clients/${id}`);
      load();
    } catch (err) {
      alert(err.response?.data?.message || "삭제에 실패했습니다.");
    }
  }

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>거래처 관리</h1>
        </div>

        <div className="card">
          <h2 style={{ fontSize: 15, marginTop: 0 }}>신규 거래처 등록</h2>
          {error && <div className="form-error">{error}</div>}
          <form onSubmit={handleSubmit} style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <div className="field">
              <label>회사명</label>
              <input value={form.companyName} onChange={(e) => update("companyName", e.target.value)} required />
            </div>
            <div className="field">
              <label>담당자명</label>
              <input value={form.contactName} onChange={(e) => update("contactName", e.target.value)} required />
            </div>
            <div className="field">
              <label>이메일</label>
              <input type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required />
            </div>
            <div className="field">
              <label>연락처</label>
              <input value={form.phone} onChange={(e) => update("phone", e.target.value)} />
            </div>
            <div style={{ gridColumn: "span 2" }}>
              <button type="submit" className="btn btn-primary">거래처 등록</button>
            </div>
          </form>
        </div>

        <div className="card">
          <h2 style={{ fontSize: 15, marginTop: 0 }}>거래처 목록</h2>
          {clients.length === 0 ? (
            <div className="empty-state">등록된 거래처가 없습니다.</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>회사명</th>
                  <th>담당자</th>
                  <th>이메일</th>
                  <th>연락처</th>
                  <th>가입 여부</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {clients.map((c) => (
                  <tr key={c.id}>
                    <td>{c.companyName}</td>
                    <td>{c.contactName}</td>
                    <td>{c.email}</td>
                    <td>{c.phone || "-"}</td>
                    <td>{c.isRegistered ? "가입완료" : "가입대기"}</td>
                    <td>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(c.id)}>삭제</button>
                    </td>
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
