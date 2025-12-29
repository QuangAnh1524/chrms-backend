import { useEffect, useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { fetchHospital, fetchHospitals } from "../api/hospitals.js";

export default function Hospitals() {
  const [hospitals, setHospitals] = useState([]);
  const [selected, setSelected] = useState(null);
  const [hospitalId, setHospitalId] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    fetchHospitals()
      .then(setHospitals)
      .catch((err) => setError(err?.response?.data?.message || "Không tải được bệnh viện"));
  }, []);

  const handleLoadDetail = async () => {
    if (!hospitalId) {
      return;
    }
    setError("");
    try {
      const data = await fetchHospital(hospitalId);
      setSelected(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được chi tiết");
    }
  };

  return (
    <>
      <Section title="Danh sách bệnh viện" description="Đọc dữ liệu seed từ backend.">
        {error ? <p className="muted">{error}</p> : null}
        <div className="grid two">
          {hospitals.map((hospital) => (
            <Card key={hospital.id} title={hospital.name} subtitle={hospital.address}>
              <p className="muted">SĐT: {hospital.phone || "Chưa có"}</p>
              <p className="muted">Mã: {hospital.code || hospital.id}</p>
            </Card>
          ))}
        </div>
      </Section>

      <Section title="Chi tiết bệnh viện" description="Nhập ID để lấy chi tiết một bệnh viện.">
        <Field label="Hospital ID">
          <input value={hospitalId} onChange={(event) => setHospitalId(event.target.value)} />
        </Field>
        <button className="button" type="button" onClick={handleLoadDetail}>
          Tải chi tiết
        </button>
        {selected ? (
          <Card title={selected.name} subtitle={selected.address}>
            <p className="muted">Phone: {selected.phone || "Chưa có"}</p>
            <p className="muted">Email: {selected.email || "Chưa có"}</p>
            <p className="muted">Mô tả: {selected.description || "Chưa có"}</p>
          </Card>
        ) : null}
      </Section>
    </>
  );
}
