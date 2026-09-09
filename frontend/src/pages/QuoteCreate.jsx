import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";
import Layout from "../components/Layout";

const emptyItem = () => ({ name: "", quantity: 1, unitPrice: 0 });

export default function QuoteCreate() {
  const navigate = useNavigate();
  const [clients, setClients] = useState([]);
  const [clientId, setClientId] = useState("");
  const [title, setTitle] = useState("");
  const [memo, setMemo] = useState("");
  const [validUntil, setValidUntil] = useState("");
  const [items, setItems] = useState([emptyItem()]);
  const [error, setError] = useState("");

  useEffect(() => {
    apiClient.get("/clients").then((res) => {
      setClients(res.data);
      if (res.data.length > 0) setClientId(String(res.data[0].id));
    });
  }, []);

  function updateItem(index, field, value) {
    setItems((prev) => prev.map((it, i) => (i === index ? { ...it, [field]: value } : it)));
  }

  function addItem() {
    setItems((prev) => [...prev, emptyItem()]);
  }

  function removeItem(index) {
    setItems((prev) => prev.filter((_, i) => i !== index));
  }

  const total = items.reduce((sum, it) => sum + Number(it.quantity || 0) * Number(it.unitPrice || 0), 0);

  async function handleSubmit(e, sendImmediately) {
    e.preventDefault();
    setError("");
    try {
      const payload = {
        clientId: Number(clientId),
        title,
        memo,
        validUntil: validUntil || null,
        items: items.map((it) => ({
          name: it.name,
          quantity: Number(it.quantity),
          unitPrice: Number(it.unitPrice),
        })),
      };
      const { data } = await apiClient.post("/quotes", payload);
      if (sendImmediately) {
        await apiClient.post(`/quotes/${data.id}/send`);
      }
      navigate(`/quotes/${data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || "견적서 작성에 실패했습니다.");
    }
  }

  return (
    <Layout>
      <div className="page">
        <div className="page-header">
          <h1>새 견적서 작성</h1>
        </div>

        {error && <div className="form-error">{error}</div>}

        <form className="card">
          <div className="field">
            <label>거래처</label>
            <select value={clientId} onChange={(e) => setClientId(e.target.value)} required>
              {clients.length === 0 && <option value="">등록된 거래처가 없습니다</option>}
              {clients.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.companyName} ({c.contactName})
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>견적서 제목</label>
            <input value={title} onChange={(e) => setTitle(e.target.value)} required />
          </div>
          <div className="field">
            <label>비고</label>
            <textarea rows={3} value={memo} onChange={(e) => setMemo(e.target.value)} />
          </div>
          <div className="field">
            <label>견적 유효기한</label>
            <input type="date" value={validUntil} onChange={(e) => setValidUntil(e.target.value)} />
          </div>

          <label style={{ fontSize: 13, color: "#6b7280" }}>견적 항목</label>
          <div style={{ marginTop: 8 }}>
            {items.map((item, index) => (
              <div className="item-row" key={index}>
                <input
                  placeholder="품목명"
                  value={item.name}
                  onChange={(e) => updateItem(index, "name", e.target.value)}
                  required
                />
                <input
                  type="number"
                  min="1"
                  placeholder="수량"
                  value={item.quantity}
                  onChange={(e) => updateItem(index, "quantity", e.target.value)}
                  required
                />
                <input
                  type="number"
                  min="0"
                  placeholder="단가"
                  value={item.unitPrice}
                  onChange={(e) => updateItem(index, "unitPrice", e.target.value)}
                  required
                />
                <div>{(Number(item.quantity || 0) * Number(item.unitPrice || 0)).toLocaleString()}원</div>
                <button type="button" className="btn btn-danger btn-sm" onClick={() => removeItem(index)}>
                  ✕
                </button>
              </div>
            ))}
          </div>
          <button type="button" className="btn btn-outline btn-sm" onClick={addItem}>
            + 항목 추가
          </button>

          <div className="total-row">
            <span>합계</span>
            <span>{total.toLocaleString()}원</span>
          </div>

          <div className="action-bar">
            <button className="btn btn-outline" onClick={(e) => handleSubmit(e, false)}>
              임시저장
            </button>
            <button className="btn btn-primary" onClick={(e) => handleSubmit(e, true)}>
              작성 후 바로 발송
            </button>
          </div>
        </form>
      </div>
    </Layout>
  );
}
