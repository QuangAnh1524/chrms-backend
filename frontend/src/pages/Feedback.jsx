import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { createFeedback, fetchDoctorAverageRating, fetchDoctorFeedback } from "../api/feedback.js";

export default function Feedback() {
  const [appointmentId, setAppointmentId] = useState("");
  const [rating, setRating] = useState("5");
  const [comment, setComment] = useState("");
  const [doctorId, setDoctorId] = useState("");
  const [feedbackList, setFeedbackList] = useState([]);
  const [average, setAverage] = useState(null);
  const [error, setError] = useState("");

  const handleSubmit = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      await createFeedback({ appointmentId: Number(appointmentId), rating: Number(rating), comment: comment || undefined });
      setComment("");
    } catch (err) {
      setError(err?.response?.data?.message || "Không gửi được đánh giá");
    }
  };

  const handleFetch = async () => {
    if (!doctorId) {
      return;
    }
    setError("");
    try {
      const data = await fetchDoctorFeedback(doctorId);
      setFeedbackList(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải feedback");
    }
  };

  const handleAverage = async () => {
    if (!doctorId) {
      return;
    }
    setError("");
    try {
      const data = await fetchDoctorAverageRating(doctorId);
      setAverage(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy được rating" );
    }
  };

  return (
    <Section title="Feedback & Rating">
      <div className="grid two">
        <div>
          <Field label="Appointment ID">
            <input value={appointmentId} onChange={(event) => setAppointmentId(event.target.value)} />
          </Field>
          <Field label="Rating">
            <select value={rating} onChange={(event) => setRating(event.target.value)}>
              {[1, 2, 3, 4, 5].map((value) => (
                <option key={value} value={value}>
                  {value}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Bình luận">
            <input value={comment} onChange={(event) => setComment(event.target.value)} />
          </Field>
          <button className="button" type="button" onClick={handleSubmit}>
            Gửi feedback
          </button>
        </div>
        <div>
          <Field label="Doctor ID">
            <input value={doctorId} onChange={(event) => setDoctorId(event.target.value)} />
          </Field>
          <div className="inline-actions">
            <button className="button outline" type="button" onClick={handleFetch}>
              Lấy feedback
            </button>
            <button className="button secondary" type="button" onClick={handleAverage}>
              Rating trung bình
            </button>
          </div>
          {average ? (
            <Card title="Rating trung bình">
              <p className="muted">Doctor: {doctorId}</p>
              <p className="muted">Average: {average.averageRating || average.rating || JSON.stringify(average)}</p>
            </Card>
          ) : null}
        </div>
      </div>
      {error ? <p className="muted">{error}</p> : null}
      <Card title="Danh sách feedback">
        {feedbackList.length ? (
          <ul>
            {feedbackList.map((item) => (
              <li key={item.id}>
                Appointment {item.appointmentId} - Rating {item.rating} - {item.comment}
              </li>
            ))}
          </ul>
        ) : (
          <p className="muted">Chưa có feedback.</p>
        )}
      </Card>
    </Section>
  );
}
