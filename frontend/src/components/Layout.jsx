import { useLocation } from "react-router-dom";
import NavBar from "./NavBar.jsx";

export default function Layout({ children }) {
  const location = useLocation();

  return (
    <div className="layout">
      <NavBar currentPath={location.pathname} />
      <main className="content">{children}</main>
    </div>
  );
}
