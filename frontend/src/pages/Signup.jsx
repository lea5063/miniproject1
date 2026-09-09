import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Signup() {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [role, setRole] = useState("FREELANCER");
  const [form, setForm] = useState({ name: "", email: "", password: "", companyName: "" });
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      await signup({ ...form, role });
      setDone(true);
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(err.response?.data?.message || "회원가입에 실패했습니다.");
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h1>회원가입</h1>
        <p className="sub">역할을 선택하고 정보를 입력해주세요</p>
        {error && <div className="form-error">{error}</div>}
        {done && <div className="form-error" style={{ background: "#e6f6ee", color: "#1b8a5a" }}>가입 완료! 로그인 화면으로 이동합니다.</div>}
        <div className="role-toggle">
          <button type="button" className={role === "FREELANCER" ? "selected" : ""} onClick={() => setRole("FREELANCER")}>
            프리랜서
          </button>
          <button type="button" className={role === "CLIENT" ? "selected" : ""} onClick={() => setRole("CLIENT")}>
            클라이언트
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label>이름</label>
            <input value={form.name} onChange={(e) => update("name", e.target.value)} required />
          </div>
          <div className="field">
            <label>이메일</label>
            <input type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required />
          </div>
          <div className="field">
            <label>비밀번호</label>
            <input type="password" value={form.password} onChange={(e) => update("password", e.target.value)} required />
          </div>
          <div className="field">
            <label>{role === "FREELANCER" ? "상호 (선택)" : "회사명"}</label>
            <input
              value={form.companyName}
              onChange={(e) => update("companyName", e.target.value)}
              required={role === "CLIENT"}
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: "100%" }}>
            가입하기
          </button>
        </form>
        <div className="auth-switch">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </div>
      </div>
    </div>
  );
}
