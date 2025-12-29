import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import {
  cancelAppointment,
  completeAppointment,
  confirmAppointment,
  createAppointment,
  fetchAppointmentById,
  fetchAppointmentHistory,
  fetchDoctorUpcomingAppointments,
  fetchUpcomingAppointments
} from "../api/appointments.js";
import { fetchAvailableSlots } from "../api/doctors.js";

const initialAppointment = {
  doctorId: "",
  hospitalId: "",
  departmentId: "",
  appointmentDate: "",
  appointmentTime: "",
  notes: ""
};

export default function Appointments() {
  const [form, setForm] = useState(initialAppointment);
  const [upcoming, setUpcoming] = useState([]);
  const [history, setHistory] = useState([]);
  const [doctorUpcoming, setDoctorUpcoming] = useState([]);
  const [detail, setDetail] = useState(null);
  const [slots, setSlots] = useState([]);
  const [appointmentId, setAppointmentId] = useState("");
  const [slotDoctorId, setSlotDoctorId] = useState("");
  const [slotDate, setSlotDate] = useState("");
  const [error, setError] = useState("");

  const onChange = (event) => {
    setForm((prev) => ({ ...prev, [event.target.name]: event.target.value }));
  };

  const handleCreate = async (event) => {
    event.preventDefault();
    setError("");
    try {
      const payload = {
        doctorId: Number(form.doctorId),
        hospitalId: Number(form.hospitalId),
        departmentId: Number(form.departmentId),
        appointmentDate: form.appointmentDate,
        appointmentTime: form.appointmentTime,
        notes: form.notes || undefined
      };
      const data = await createAppointment(payload);
      setDetail(data);
      setForm(initialAppointment);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tạo được lịch hẹn");
    }
  };

  const loadUpcoming = async () => {
    setError("");
    try {
      const data = await fetchUpcomingAppointments();
      setUpcoming(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải lịch sắp tới");
    }
  };

  const loadHistory = async () => {
    setError("");
    try {
      const data = await fetchAppointmentHistory();
      setHistory(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải lịch sử");
    }
  };

  const loadDoctorUpcoming = async () => {
    setError("");
    try {
      const data = await fetchDoctorUpcomingAppointments();
      setDoctorUpcoming(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải lịch bác sĩ");
    }
  };

  const loadDetail = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      const data = await fetchAppointmentById(appointmentId);
      setDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải chi tiết");
    }
  };

  const loadSlots = async () => {
    if (!slotDoctorId || !slotDate) {
      return;
    }
    setError("");
    try {
      const data = await fetchAvailableSlots(slotDoctorId, slotDate);
      setSlots(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải slot trống");
    }
  };

  const handleAction = async (action) => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      if (action === "confirm") {
        setDetail(await confirmAppointment(appointmentId));
      }
      if (action === "complete") {
        setDetail(await completeAppointment(appointmentId));
      }
      if (action === "cancel") {
        setDetail(await cancelAppointment(appointmentId, { reason: "Cập nhật từ FE" }));
      }
    } catch (err) {
      setError(err?.response?.data?.message || "Không cập nhật được lịch");
    }
  };

  return (
    <>
      <Section title="Đặt lịch khám" description="Chỉ PATIENT mới tạo appointment.">
        <form onSubmit={handleCreate}>
          <div className="grid two">
            <Field label="Doctor ID">
              <input name="doctorId" value={form.doctorId} onChange={onChange} />
            </Field>
            <Field label="Hospital ID">
              <input name="hospitalId" value={form.hospitalId} onChange={onChange} />
            </Field>
            <Field label="Department ID">
              <input name="departmentId" value={form.departmentId} onChange={onChange} />
            </Field>
            <Field label="Ngày khám (YYYY-MM-DD)">
              <input name="appointmentDate" value={form.appointmentDate} onChange={onChange} />
            </Field>
            <Field label="Giờ khám (HH:mm)">
              <input name="appointmentTime" value={form.appointmentTime} onChange={onChange} />
            </Field>
            <Field label="Ghi chú">
              <input name="notes" value={form.notes} onChange={onChange} />
            </Field>
          </div>
          {error ? <p className="muted">{error}</p> : null}
          <button className="button" type="submit">
            Tạo lịch hẹn
          </button>
        </form>
      </Section>

      <Section title="Slot trống theo bác sĩ">
        <div className="inline-actions">
          <Field label="Doctor ID">
            <input value={slotDoctorId} onChange={(event) => setSlotDoctorId(event.target.value)} />
          </Field>
          <Field label="Ngày">
            <input value={slotDate} onChange={(event) => setSlotDate(event.target.value)} />
          </Field>
          <button className="button outline" type="button" onClick={loadSlots}>
            Xem slot
          </button>
        </div>
        {slots.length ? (
          <Card title="Slots">
            <ul>
              {slots.map((slot) => (
                <li key={slot}>{slot}</li>
              ))}
            </ul>
          </Card>
        ) : (
          <p className="muted">Chưa có dữ liệu slot.</p>
        )}
      </Section>

      <Section title="Lịch hẹn của bệnh nhân">
        <div className="inline-actions">
          <button className="button" type="button" onClick={loadUpcoming}>
            Lịch sắp tới
          </button>
          <button className="button outline" type="button" onClick={loadHistory}>
            Lịch sử
          </button>
        </div>
        <div className="grid two">
          {upcoming.map((item) => (
            <Card key={item.id} title={`Appointment #${item.id}`} subtitle={`Status: ${item.status}`}>
              <p className="muted">Ngày: {item.appointmentDate} {item.appointmentTime}</p>
              <p className="muted">Doctor: {item.doctorId}</p>
            </Card>
          ))}
          {history.map((item) => (
            <Card key={`history-${item.id}`} title={`Appointment #${item.id}`} subtitle={`Status: ${item.status}`}>
              <p className="muted">Ngày: {item.appointmentDate} {item.appointmentTime}</p>
              <p className="muted">Doctor: {item.doctorId}</p>
            </Card>
          ))}
        </div>
      </Section>

      <Section title="Lịch hẹn của bác sĩ">
        <button className="button" type="button" onClick={loadDoctorUpcoming}>
          Tải lịch bác sĩ
        </button>
        <div className="grid two">
          {doctorUpcoming.map((item) => (
            <Card key={`doctor-${item.id}`} title={`Appointment #${item.id}`} subtitle={`Status: ${item.status}`}>
              <p className="muted">Ngày: {item.appointmentDate} {item.appointmentTime}</p>
              <p className="muted">Patient: {item.patientId}</p>
            </Card>
          ))}
        </div>
      </Section>

      <Section title="Cập nhật trạng thái lịch hẹn">
        <Field label="Appointment ID">
          <input value={appointmentId} onChange={(event) => setAppointmentId(event.target.value)} />
        </Field>
        <div className="inline-actions">
          <button className="button" type="button" onClick={loadDetail}>
            Lấy chi tiết
          </button>
          <button className="button outline" type="button" onClick={() => handleAction("confirm")}>
            Confirm
          </button>
          <button className="button outline" type="button" onClick={() => handleAction("complete")}>
            Complete
          </button>
          <button className="button secondary" type="button" onClick={() => handleAction("cancel")}>
            Cancel
          </button>
        </div>
        {detail ? (
          <Card title={`Appointment #${detail.id}`} subtitle={`Status: ${detail.status}`}>
            <p className="muted">Patient: {detail.patientId}</p>
            <p className="muted">Doctor: {detail.doctorId}</p>
            <p className="muted">Queue: {detail.queueNumber}</p>
          </Card>
        ) : null}
      </Section>
    </>
  );
}
