import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { fetchAvailableSlots, fetchDoctorSchedules, fetchDoctors, fetchDoctorsByDepartment, fetchDoctorsByHospital } from "../api/doctors.js";

export default function Doctors() {
  const [doctors, setDoctors] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [slots, setSlots] = useState([]);
  const [hospitalId, setHospitalId] = useState("");
  const [departmentId, setDepartmentId] = useState("");
  const [doctorId, setDoctorId] = useState("");
  const [date, setDate] = useState("");
  const [error, setError] = useState("");

  const loadAll = async () => {
    setError("");
    try {
      const data = await fetchDoctors();
      setDoctors(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được bác sĩ");
    }
  };

  const loadByHospital = async () => {
    if (!hospitalId) {
      return;
    }
    setError("");
    try {
      const data = await fetchDoctorsByHospital(hospitalId);
      setDoctors(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được bác sĩ");
    }
  };

  const loadByDepartment = async () => {
    if (!departmentId) {
      return;
    }
    setError("");
    try {
      const data = await fetchDoctorsByDepartment(departmentId);
      setDoctors(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được bác sĩ");
    }
  };

  const loadSchedules = async () => {
    if (!doctorId) {
      return;
    }
    setError("");
    try {
      const data = await fetchDoctorSchedules(doctorId);
      setSchedules(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được lịch bác sĩ");
    }
  };

  const loadSlots = async () => {
    if (!doctorId || !date) {
      return;
    }
    setError("");
    try {
      const data = await fetchAvailableSlots(doctorId, date);
      setSlots(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được slot" );
    }
  };

  return (
    <>
      <Section title="Danh sách bác sĩ" description="Lọc theo bệnh viện hoặc khoa nếu cần.">
        <div className="inline-actions">
          <button className="button" type="button" onClick={loadAll}>
            Tải tất cả
          </button>
          <Field label="Hospital ID">
            <input value={hospitalId} onChange={(event) => setHospitalId(event.target.value)} />
          </Field>
          <button className="button outline" type="button" onClick={loadByHospital}>
            Theo bệnh viện
          </button>
          <Field label="Department ID">
            <input value={departmentId} onChange={(event) => setDepartmentId(event.target.value)} />
          </Field>
          <button className="button outline" type="button" onClick={loadByDepartment}>
            Theo khoa
          </button>
        </div>
        {error ? <p className="muted">{error}</p> : null}
        <div className="grid two">
          {doctors.map((doctor) => (
            <Card key={doctor.id} title={doctor.fullName} subtitle={doctor.specialty}>
              <p className="muted">ID: {doctor.id}</p>
              <p className="muted">Hospital: {doctor.hospitalId || "-"}</p>
              <p className="muted">Phone: {doctor.phone || "-"}</p>
            </Card>
          ))}
        </div>
      </Section>

      <Section title="Lịch làm việc & slot trống" description="Xem lịch hoặc slot theo ngày.">
        <div className="grid two">
          <div>
            <Field label="Doctor ID">
              <input value={doctorId} onChange={(event) => setDoctorId(event.target.value)} />
            </Field>
            <button className="button" type="button" onClick={loadSchedules}>
              Tải lịch làm việc
            </button>
          </div>
          <div>
            <Field label="Ngày khám (YYYY-MM-DD)">
              <input value={date} onChange={(event) => setDate(event.target.value)} />
            </Field>
            <button className="button" type="button" onClick={loadSlots}>
              Xem slot trống
            </button>
          </div>
        </div>
        <div className="grid two">
          <Card title="Lịch làm việc">
            {schedules.length ? (
              <ul>
                {schedules.map((schedule) => (
                  <li key={schedule.id}>
                    Thứ {schedule.dayOfWeek}: {schedule.startTime} - {schedule.endTime} ({schedule.isAvailable ? "Available" : "Off"})
                  </li>
                ))}
              </ul>
            ) : (
              <p className="muted">Chưa có dữ liệu lịch.</p>
            )}
          </Card>
          <Card title="Slot trống">
            {slots.length ? (
              <ul>
                {slots.map((slot) => (
                  <li key={slot}>{slot}</li>
                ))}
              </ul>
            ) : (
              <p className="muted">Chưa có dữ liệu slot.</p>
            )}
          </Card>
        </div>
      </Section>
    </>
  );
}
