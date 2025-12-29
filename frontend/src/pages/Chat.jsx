import { useState } from "react";
import Section from "../components/Section.jsx";
import Field from "../components/Field.jsx";
import Card from "../components/Card.jsx";
import { fetchMessages, fetchUnreadMessages, markMessagesRead, sendMessage } from "../api/chat.js";

export default function Chat() {
  const [appointmentId, setAppointmentId] = useState("");
  const [message, setMessage] = useState("");
  const [after, setAfter] = useState("");
  const [messages, setMessages] = useState([]);
  const [unread, setUnread] = useState([]);
  const [error, setError] = useState("");

  const handleSend = async () => {
    if (!appointmentId || !message) {
      return;
    }
    setError("");
    try {
      const data = await sendMessage(appointmentId, { message });
      setMessages((prev) => [...prev, data]);
      setMessage("");
    } catch (err) {
      setError(err?.response?.data?.message || "Không gửi được tin nhắn");
    }
  };

  const handleFetchMessages = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      const data = await fetchMessages(appointmentId, after || undefined);
      setMessages(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được tin nhắn");
    }
  };

  const handleFetchUnread = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      const data = await fetchUnreadMessages(appointmentId);
      setUnread(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Không tải được tin chưa đọc");
    }
  };

  const handleMarkRead = async () => {
    if (!appointmentId) {
      return;
    }
    setError("");
    try {
      await markMessagesRead(appointmentId, { upToDatetime: new Date().toISOString() });
      setUnread([]);
    } catch (err) {
      setError(err?.response?.data?.message || "Không đánh dấu được" );
    }
  };

  return (
    <Section title="Chat theo lịch hẹn" description="Nhập appointmentId để gửi/nhận tin nhắn.">
      <Field label="Appointment ID">
        <input value={appointmentId} onChange={(event) => setAppointmentId(event.target.value)} />
      </Field>
      <div className="grid two">
        <div>
          <Field label="Nội dung">
            <input value={message} onChange={(event) => setMessage(event.target.value)} />
          </Field>
          <button className="button" type="button" onClick={handleSend}>
            Gửi tin
          </button>
        </div>
        <div>
          <Field label="Lấy tin từ thời điểm (ISO)">
            <input value={after} onChange={(event) => setAfter(event.target.value)} />
          </Field>
          <div className="inline-actions">
            <button className="button outline" type="button" onClick={handleFetchMessages}>
              Lấy tin nhắn
            </button>
            <button className="button outline" type="button" onClick={handleFetchUnread}>
              Tin chưa đọc
            </button>
            <button className="button secondary" type="button" onClick={handleMarkRead}>
              Đánh dấu đã đọc
            </button>
          </div>
        </div>
      </div>
      {error ? <p className="muted">{error}</p> : null}
      <div className="grid two">
        <Card title="Tin nhắn">
          {messages.length ? (
            <ul>
              {messages.map((msg) => (
                <li key={msg.id}>
                  {msg.senderId}: {msg.message} ({msg.sentAt})
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">Chưa có tin nhắn.</p>
          )}
        </Card>
        <Card title="Tin chưa đọc">
          {unread.length ? (
            <ul>
              {unread.map((msg) => (
                <li key={msg.id}>
                  {msg.senderId}: {msg.message}
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">Không có tin chưa đọc.</p>
          )}
        </Card>
      </div>
    </Section>
  );
}
