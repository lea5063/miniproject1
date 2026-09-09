import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink to="/" className="brand">
          프리인보이스
        </NavLink>
        <nav className="nav-links">
          <NavLink to="/">대시보드</NavLink>
          {user?.role === "FREELANCER" && <NavLink to="/clients">거래처</NavLink>}
          <NavLink to="/quotes">견적서</NavLink>
          <NavLink to="/invoices">인보이스</NavLink>
          <span className="user-chip">
            {user?.name} ({user?.role === "FREELANCER" ? "프리랜서" : "클라이언트"})
          </span>
          <button className="btn btn-outline btn-sm" onClick={handleLogout}>
            로그아웃
          </button>
        </nav>
      </header>
      <main>{children}</main>
    </div>
  );
}
