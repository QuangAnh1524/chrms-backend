import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { fetchMyProfile, updateMyProfile } from "../api/patients.js";

const initialProfile = {
  fullName: "",
  phone: "",
  dob: "",
  gender: "",
  address: "",
  emergencyContact: "",
  bloodType: "",
  allergies: ""
};

export default function Profile() {
  const [profile, setProfile] = useState(initialProfile);
  const [detail, setDetail] = useState(null);
  const [error, setError] = useState("");

  const onChange = (event) => {
    setProfile((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleFetch = async () => {
    setError("");
    try {
      const data = await fetchMyProfile();
      setDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy được hồ sơ" );
    }
  };

  const handleUpdate = async () => {
    setError("");
    try {
      const data = await updateMyProfile({
        fullName: profile.fullName || undefined,
        phone: profile.phone || undefined,
        dob: profile.dob || undefined,
        gender: profile.gender || undefined,
        address: profile.address || undefined,
        emergencyContact: profile.emergencyContact || undefined,
        bloodType: profile.bloodType || undefined,
        allergies: profile.allergies || undefined
      });
      setDetail(data);
      setProfile(initialProfile);
    } catch (err) {
      setError(err?.response?.data?.message || "Không cập nhật được hồ sơ" );
    }
  };

  return (
    <Section title="Hồ sơ bệnh nhân" description="Chỉ PATIENT mới truy cập /patients/me.">
      <div className="inline-actions">
        <button className="button" type="button" onClick={handleFetch}>
          Lấy hồ sơ
        </button>
        <button className="button outline" type="button" onClick={handleUpdate}>
          Cập nhật
        </button>
      </div>
      <div className="grid two">
        <div>
          <Field label="Họ tên">
            <input name="fullName" value={profile.fullName} onChange={onChange} />
          </Field>
          <Field label="Số điện thoại">
            <input name="phone" value={profile.phone} onChange={onChange} />
          </Field>
          <Field label="Ngày sinh (YYYY-MM-DD)">
            <input name="dob" value={profile.dob} onChange={onChange} />
          </Field>
          <Field label="Giới tính">
            <input name="gender" value={profile.gender} onChange={onChange} />
          </Field>
        </div>
        <div>
          <Field label="Địa chỉ">
            <input name="address" value={profile.address} onChange={onChange} />
          </Field>
          <Field label="Liên hệ khẩn">
            <input name="emergencyContact" value={profile.emergencyContact} onChange={onChange} />
          </Field>
          <Field label="Nhóm máu">
            <input name="bloodType" value={profile.bloodType} onChange={onChange} />
          </Field>
          <Field label="Dị ứng">
            <input name="allergies" value={profile.allergies} onChange={onChange} />
          </Field>
        </div>
      </div>
      {error ? <p className="muted">{error}</p> : null}
      {detail ? (
        <Card title={detail.fullName || detail.email} subtitle={`Patient #${detail.id || ""}`}>
          <pre>{JSON.stringify(detail, null, 2)}</pre>
        </Card>
      ) : null}
    </Section>
  );
}
