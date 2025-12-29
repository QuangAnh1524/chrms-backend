import Section from "../components/Section.jsx";
import Card from "../components/Card.jsx";
import StatusBadge from "../components/StatusBadge.jsx";

const quickActions = [
  { title: "Đăng nhập", description: "Lấy JWT để truy cập API bảo mật.", path: "/login" },
  { title: "Danh sách bệnh viện", description: "Đọc dữ liệu seed bệnh viện/khoa.", path: "/hospitals" },
  { title: "Đặt lịch hẹn", description: "Tạo appointment, xác nhận và thanh toán.", path: "/appointments" },
  { title: "Hồ sơ khám", description: "Tạo hồ sơ, upload file, kê đơn.", path: "/records" }
];

export default function Home() {
  return (
    <>
      <Section
        title="CHRMS Frontend"
        description="Bộ giao diện React tách riêng dành cho backend CHRMS. Kết nối trực tiếp với REST API đã mô tả trong README."
      >
        <div className="notice">
          <strong>Mẹo:</strong> đặt biến môi trường <code>VITE_API_BASE_URL</code> để trỏ sang API khác.
          <div className="inline-actions" style={{ marginTop: 12 }}>
            <StatusBadge text="Auth/JWT" />
            <StatusBadge text="Appointments" />
            <StatusBadge text="Medical Records" />
            <StatusBadge text="Chat + Feedback" />
          </div>
        </div>
      </Section>

      <Section title="Lối tắt nhanh" description="Truy cập nhanh các module chính để test API.">
        <div className="grid two">
          {quickActions.map((action) => (
            <Card key={action.title} title={action.title} subtitle={action.description}>
              <a className="button outline" href={action.path}>
                Mở trang
              </a>
            </Card>
          ))}
        </div>
      </Section>
    </>
  );
}
