import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { completePayment, createPayment, fetchPaymentByAppointment } from "../api/payments.js";

export default function Payments() {
  const [appointmentId, setAppointmentId] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [transactionRef, setTransactionRef] = useState("");
  const [paymentDetail, setPaymentDetail] = useState(null);
  const [error, setError] = useState("");

  const handleCreate = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      const data = await createPayment({
        appointmentId: Number(appointmentId),
        paymentMethod
      });
      setPaymentDetail(data);
      setTransactionRef(data.transactionRef || "");
    } catch (err) {
      setError(err?.response?.data?.message || "Không tạo được giao dịch");
    }
  };

  const handleFetch = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      const data = await fetchPaymentByAppointment(appointmentId);
      setPaymentDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không lấy được giao dịch");
    }
  };

  const handleComplete = async () => {
    if (!transactionRef) {
      return;
    }
    setError("");
    try {
      const data = await completePayment(transactionRef);
      setPaymentDetail(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không thể hoàn tất giao dịch");
    }
  };

  return (
    <Section title="Thanh toán" description="Tạo giao dịch và đánh dấu hoàn tất theo appointment.">
      <div className="grid two">
        <div>
          <Field label="Appointment ID">
            <input value={appointmentId} onChange={(event) => setAppointmentId(event.target.value)} />
          </Field>
          <Field label="Phương thức">
            <select value={paymentMethod} onChange={(event) => setPaymentMethod(event.target.value)}>
              <option value="CASH">CASH</option>
              <option value="CARD">CARD</option>
              <option value="VNPAY">VNPAY</option>
            </select>
          </Field>
          <div className="inline-actions">
            <button className="button" type="button" onClick={handleCreate}>
              Tạo giao dịch
            </button>
            <button className="button outline" type="button" onClick={handleFetch}>
              Kiểm tra giao dịch
            </button>
          </div>
        </div>
        <div>
          <Field label="Transaction Ref">
            <input value={transactionRef} onChange={(event) => setTransactionRef(event.target.value)} />
          </Field>
          <button className="button secondary" type="button" onClick={handleComplete}>
            Hoàn tất giao dịch
          </button>
        </div>
      </div>
      {error ? <p className="muted">{error}</p> : null}
      {paymentDetail ? (
        <Card title={`Transaction ${paymentDetail.transactionRef || ""}`} subtitle={`Status: ${paymentDetail.status || paymentDetail.paymentStatus}`}>
          <p className="muted">Appointment: {paymentDetail.appointmentId}</p>
          <p className="muted">Amount: {paymentDetail.amount}</p>
          <p className="muted">Method: {paymentDetail.paymentMethod}</p>
        </Card>
      ) : null}
    </Section>
  );
}
