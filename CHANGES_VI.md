# Tóm tắt thay đổi gần đây

## Đăng ký tài khoản
- Tài khoản PATIENT vẫn được tạo kèm hồ sơ bệnh nhân nhưng **không bắt buộc** các trường nhân khẩu (ngày sinh, giới tính, địa chỉ, liên hệ khẩn cấp, nhóm máu, dị ứng); người dùng có thể bổ sung sau. Xem `RegisterUseCase`.

## Đặt lịch khám
- Bệnh nhân phải chọn **khoa** và hệ thống kiểm tra khoa thuộc bệnh viện, bác sĩ có thuộc khoa/bệnh viện đó không.
- Giờ khám chuẩn hóa tới **phút** (bỏ giây/nano) và được kiểm tra **không nằm trong quá khứ** kể cả khi đặt lịch trong ngày.
- Chặn đặt trùng slot nếu bác sĩ đã có lịch hoặc chính bệnh nhân đã có lịch cùng thời điểm.
- Tự động cấp **số thứ tự** theo bác sĩ/bệnh viện/ngày; trả về đầy đủ thông tin bệnh nhân, bác sĩ, bệnh viện, khoa, giờ hẹn, số thứ tự và ghi chú.

## Danh sách lịch khám cho bệnh nhân
- Hai API cung cấp lịch sắp tới và lịch sử, kèm tên bệnh viện, bác sĩ, khoa, giờ khám (đã chuẩn hóa), trạng thái và số thứ tự.
