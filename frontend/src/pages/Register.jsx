import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import { useAuth } from "../context/AuthContext.jsx";

const initialState = {
  email: "",
  password: "",
  role: "PATIENT",
  fullName: "",
  phone: "",
  hospitalId: "",
  specialty: "",
  licenseNumber: ""
};

export default function Register() {
  const { register, status, setStatus } = useAuth();
  const [form, setForm] = useState(initialState);
  const [error, setError] = useState("");

  const onChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    try {
      await register({
        email: form.email,
        password: form.password,
        role: form.role,
        fullName: form.fullName,
        phone: form.phone || undefined,
        hospitalId: form.hospitalId ? Number(form.hospitalId) : undefined,
        specialty: form.specialty || undefined,
        licenseNumber: form.licenseNumber || undefined
      });
      setForm(initialState);
    } catch (err) {
      setError(err?.response?.data?.message || "Không thể đăng ký");
      setStatus("");
    }
  };

  return (
    <Section title="Đăng ký tài khoản" description="PATIENT chỉ cần email + mật khẩu. DOCTOR cần hospitalId, specialty, licenseNumber.">
      <form onSubmit={handleSubmit}>
        <div className="grid two">
          <Field label="Email">
            <input name="email" value={form.email} onChange={onChange} />
          </Field>
          <Field label="Mật khẩu">
            <input name="password" type="password" value={form.password} onChange={onChange} />
          </Field>
          <Field label="Họ tên">
            <input name="fullName" value={form.fullName} onChange={onChange} />
          </Field>
          <Field label="Số điện thoại">
            <input name="phone" value={form.phone} onChange={onChange} />
          </Field>
          <Field label="Vai trò">
            <select name="role" value={form.role} onChange={onChange}>
              <option value="PATIENT">PATIENT</option>
              <option value="DOCTOR">DOCTOR</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </Field>
          <Field label="Hospital ID (Doctor)">
            <input name="hospitalId" value={form.hospitalId} onChange={onChange} />
          </Field>
          <Field label="Chuyên môn (Doctor)">
            <input name="specialty" value={form.specialty} onChange={onChange} />
          </Field>
          <Field label="Số giấy phép (Doctor)">
            <input name="licenseNumber" value={form.licenseNumber} onChange={onChange} />
          </Field>
        </div>
        {error ? <p className="muted">{error}</p> : null}
        {status ? <p className="muted">{status}</p> : null}
        <button className="button" type="submit">
          Đăng ký
        </button>
      </form>
    </Section>
  );
}
