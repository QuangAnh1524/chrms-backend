import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

const links = [
  { to: "/", label: "Tổng quan" },
  { to: "/register", label: "Đăng ký" },
  { to: "/hospitals", label: "Bệnh viện" },
  { to: "/doctors", label: "Bác sĩ" },
  { to: "/appointments", label: "Lịch hẹn" },
  { to: "/payments", label: "Thanh toán" },
  { to: "/records", label: "Hồ sơ khám" },
  { to: "/chat", label: "Chat" },
  { to: "/feedback", label: "Đánh giá" },
  { to: "/profile", label: "Hồ sơ" }
];

export default function NavBar({ currentPath }) {
  const { token, logout } = useAuth();

  return (
    <header className="navbar">
      <div>
        <div className="navbar-title">CHRMS Frontend</div>
        <div className="muted">Base: {import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1"}</div>
      </div>
      <nav className="nav-links">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? "active" : "")}
          >
            {link.label}
          </NavLink>
        ))}
        {token ? (
          <button className="button outline" type="button" onClick={logout}>
            Đăng xuất
          </button>
        ) : (
          <NavLink
            to="/login"
            className={currentPath === "/login" ? "active" : ""}
          >
            Đăng nhập
          </NavLink>
        )}
      </nav>
    </header>
  );
}
