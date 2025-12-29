import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import { useAuth } from "../context/AuthContext.jsx";

export default function Login() {
  const { login, status, setStatus } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "", role: "PATIENT" });
  const [error, setError] = useState("");

  const onChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    try {
      await login({ email: form.email, password: form.password }, form.role);
      navigate("/");
    } catch (err) {
      setError(err?.response?.data?.message || "Không thể đăng nhập");
      setStatus("");
    }
  };

  return (
    <Section title="Đăng nhập" description="Sử dụng tài khoản seed từ README hoặc user tự tạo.">
      <form onSubmit={handleSubmit}>
        <Field label="Email">
          <input name="email" value={form.email} onChange={onChange} placeholder="patient1@test.com" />
        </Field>
        <Field label="Mật khẩu">
          <input name="password" type="password" value={form.password} onChange={onChange} />
        </Field>
        <Field label="Gợi ý role (lưu local)">
          <select name="role" value={form.role} onChange={onChange}>
            <option value="PATIENT">PATIENT</option>
            <option value="DOCTOR">DOCTOR</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </Field>
        {error ? <p className="muted">{error}</p> : null}
        {status ? <p className="muted">{status}</p> : null}
        <button className="button" type="submit">
          Đăng nhập
        </button>
      </form>
    </Section>
  );
}
